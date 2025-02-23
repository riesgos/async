package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.config.RetryConfiguration
import org.n.riesgos.asyncwrapper.config.WPSOutputDefinition
import org.n.riesgos.asyncwrapper.process.*
import org.n.riesgos.asyncwrapper.process.Process
import org.n.riesgos.asyncwrapper.utils.Version
import org.n.riesgos.asyncwrapper.utils.retry
import org.n52.geoprocessing.wps.client.ExecuteRequestBuilder
import org.n52.geoprocessing.wps.client.WPSClientException
import org.n52.geoprocessing.wps.client.WPSClientSession
import org.n52.geoprocessing.wps.client.encoder.WPS100ExecuteEncoder
import org.n52.geoprocessing.wps.client.encoder.WPS20ExecuteEncoder
import org.n52.geoprocessing.wps.client.model.*
import org.n52.geoprocessing.wps.client.model.execution.Data
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger



class WPSProcess(private val wpsClient : WPSClientSession, private val url: String, private val processID: String, private val wpsVersion: Version, private val dialect: String, private val expectedOutputs : List<WPSOutputDefinition>, private val retryConfig : RetryConfiguration) : Process {
    companion object {
        val LOGGER = Logger.getLogger("WPSProcess")
    }
    override fun runProcess(input: ProcessInput): ProcessOutput {

        // take a look at the process description
        val processDescription = retry<org.n52.geoprocessing.wps.client.model.Process>(retryConfig.maxRetries, retryConfig.backoffMillis, this::isWPSClientException) {
            LOGGER.info("retrieve wps process description for ${this.processID} (retries: $it)")
            val processDescription = getCompleteProcessDescription()
            LOGGER.info("retrieved wps process description for ${this.processID} (retries: $it)")
            return@retry processDescription
        }

        // create the request, add literal input
        val executeBuilder = ExecuteRequestBuilder(processDescription)
        processDescription.inputs.forEach {
            val parameterIn = it.id

            if(it is ComplexInputDescription){
                if (input.inlineParameters.containsKey(parameterIn)) {
                    val data = input.inlineParameters[parameterIn]!!
                    executeBuilder.addComplexData(parameterIn, data.value, data.schema, data.encoding, data.mimeType)
                } else if (input.referenceParameters.containsKey(parameterIn)) {
                    val data = input.referenceParameters[parameterIn]!!
                    // Seem to be the case that our riesgos wps server doesn't allow to set the schema, nor the encoding for the
                    // complex reference inputs.
                    // (It complains about the mime type then & that it doesn't find generators for it).
                    // Maybe this is just a weird setting of our very own server.
                    if (dialect == "52nwps") {
                        executeBuilder.addComplexDataReference(parameterIn, data.link, null, null, data.mimeType)
                    } else {
                        executeBuilder.addComplexDataReference(parameterIn, data.link, data.schema, data.encoding, data.mimeType)
                    }
                }
            }else if(it is LiteralInputDescription){
                if (input.inlineParameters.containsKey(parameterIn)) {
                    val data = input.inlineParameters[parameterIn]!!
                    executeBuilder.addLiteralData(parameterIn, data.value, data.schema, data.encoding, data.mimeType)
                }
            }else if(it is BoundingBoxInputDescription) {
                if (input.bboxParameters.containsKey(parameterIn)) {
                    val data = input.bboxParameters[parameterIn]!!
                    executeBuilder.addBoundingBoxData(parameterIn, data.bbox, "", "", "")
                }
            }
        }

        for (parameterOut in expectedOutputs) { //set expected output parameters
            executeBuilder.setResponseDocument(parameterOut.wpsIdentifier, parameterOut.xmlschema, parameterOut.encoding, parameterOut.mimeType)
            // executeBuilder.setAsReference(parameterOut.wpsIdentifier, true)
            //
            // unfortunately the executeBuilder.setAsReference method doesn't deal with situations
            // in that we could have multiple outputs for the same identifier (say multiple formats)

            // So we must do that ourselves. (But we are able to check the source code so we
            // are lucky.
            for (output in executeBuilder.execute.outputs) {
                if (output.id == parameterOut.wpsIdentifier) {
                    output.transmissionMode = TransmissionMode.REFERENCE
                }
            }



        }

        // build and send the request document

        try {
            val executeRequest = executeBuilder.execute
            // Print the text out
            val requestText = if(wpsVersion >= Version("2.0.0")) {
                WPS20ExecuteEncoder.encode(executeRequest)
            }else{
                WPS100ExecuteEncoder.encode(executeRequest)
            }
            LOGGER.info(requestText)

           //execute wps process, retry if (network) error
           val processOutput = retry<ProcessOutput>(retryConfig.maxRetries, retryConfig.backoffMillis, this::isWPSClientException) {
               LOGGER.info("execute WPS process ${this.processID} (retries: $it)")
               val wpsOutput = wpsClient.execute(url, executeRequest, this.wpsVersion.versionStr)
               val processOutput = parseProcessOutput(wpsOutput) //attempt successful
               LOGGER.info("executed WPS process ${this.processID} (needed $it retries")
               return@retry processOutput
           }

            return processOutput

        }catch (e : Exception){
            println(e.message)
            print(e.stackTrace)
            throw e
        }
    }

    private fun getOutputsById(outputId: String, outputs: List<Data>) : List<Data> {
        return outputs.filter { it.id == outputId }
    }

    private fun emptyStringIfNull (nullableString: String?): String {
        if (nullableString == null) {
            return ""
        }
        return nullableString
    }

    private fun parseProcessOutput(wpsOutput: Any): ProcessOutput {
        var result: org.n52.geoprocessing.wps.client.model.Result =
            if (wpsOutput is org.n52.geoprocessing.wps.client.model.Result) {
                wpsOutput
            } else {
                (wpsOutput as StatusInfo).result
            }

        LOGGER.info("request result: ${result.toString()}")
        val outputs: List<Data> = result.outputs
        LOGGER.info("request outputs: ${outputs.toString()}")
        var refOutputs = HashMap<String, MutableList<ReferenceParameter>>()

        LOGGER.info("Start extracting results from the wps")
        val setOutputIdentifiers = HashSet(expectedOutputs.map { it.wpsIdentifier })
        for (expectedOutputIdentifier in setOutputIdentifiers) {
            val outputs = getOutputsById(expectedOutputIdentifier, outputs)
            if (outputs.isNotEmpty()) {
                for (output in outputs) {
                    val complexOutput = output.asComplexReferenceData()
                    val format = complexOutput.format
                    val schema = emptyStringIfNull(format.schema)
                    val outputParam = ReferenceParameter(
                        complexOutput.id,
                        complexOutput.reference.href.toString(),
                        format.mimeType,
                        format.encoding,
                        schema
                    )
                    if (!refOutputs.containsKey(complexOutput.id)) {
                        refOutputs[complexOutput.id] = mutableListOf(outputParam)
                    } else {
                        refOutputs[complexOutput.id]!!.add(outputParam)
                    }
                    LOGGER.info("Stored result for " + complexOutput.id)
                }
            } else {
                println("did not find expected output parameter $expectedOutputIdentifier in wps result")
                throw java.lang.IllegalArgumentException("Not found wps output parameter $expectedOutputIdentifier")
            }
        }

        return ProcessOutput(
            processID,
            HashMap(),
            refOutputs
        )
    }

    /**
     * check if wpsClient retrieved complete process description from server
     */
    private fun getCompleteProcessDescription() : org.n52.geoprocessing.wps.client.model.Process{
        val processDescription = wpsClient.getProcessDescription(url, processID, wpsVersion.versionStr)
        //description not complete if network error
        if(processDescription.inputs == null || processDescription.outputs == null){
            throw WPSClientException("could not retrieve complete process description for process $processID")
        }

        return processDescription
    }

    private fun isWPSClientException(ex: Exception) : Boolean{
        val retryable = ex is WPSClientException
        LOGGER.log(Level.WARNING, "wps process returned an exception, retryable: $retryable", ex)
        return retryable
    }
}