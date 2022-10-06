package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.config.WPSOutputDefinition
import org.n.riesgos.asyncwrapper.process.*
import org.n.riesgos.asyncwrapper.process.Process
import org.n52.geoprocessing.wps.client.ExecuteRequestBuilder
import org.n52.geoprocessing.wps.client.WPSClientSession
import org.n52.geoprocessing.wps.client.encoder.WPS20ExecuteEncoder
import org.n52.geoprocessing.wps.client.model.*
import org.n52.geoprocessing.wps.client.model.execution.Data
import java.sql.Ref
import java.util.logging.Logger


class WPSProcess(private val wpsClient : WPSClientSession, private val url: String, private val processID: String, private val wpsVersion: String, private val expectedOutputs : List<WPSOutputDefinition>) : Process {

    companion object {
        val LOGGER = Logger.getLogger("WPSProcess")
    }
    override fun runProcess(input: ProcessInput): ProcessOutput {

        // take a look at the process description
        val processDescription = wpsClient.getProcessDescription(url, processID, wpsVersion)

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
                    executeBuilder.addComplexDataReference(parameterIn, data.link, null, null, data.mimeType)
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
            executeBuilder.setAsReference(parameterOut.wpsIdentifier, true)
        }

        // build and send the request document

        try {
            val executeRequest = executeBuilder.execute
            // Print the text out
            val requestText = WPS20ExecuteEncoder.encode(executeRequest)
            LOGGER.info(requestText)

            val output = wpsClient.execute(url, executeRequest, wpsVersion)

            var result: org.n52.geoprocessing.wps.client.model.Result =
                if (output is org.n52.geoprocessing.wps.client.model.Result) {
                    output
                } else {
                    (output as StatusInfo).result
                }

            println(result)
            val outputs: List<Data> = result.outputs
            println(outputs)
            var refOutputs = HashMap<String, MutableList<ReferenceParameter>>()

            LOGGER.info("Start extracting results from the wps")
            for(expectedOutput in expectedOutputs){
                val output = getOutputById(expectedOutput.wpsIdentifier, outputs)
                if(output != null){
                    val complexOutput = output.asComplexReferenceData()
                    val format = complexOutput.format
                    val schema = emptyStringIfNull(format.schema)
                    val outputParam = ReferenceParameter(complexOutput.id, complexOutput.reference.href.toString(), format.mimeType, format.encoding, schema)
                    if(!refOutputs.containsKey(complexOutput.id)){
                        refOutputs[complexOutput.id] = mutableListOf(outputParam)
                    }else{
                        refOutputs[complexOutput.id]!!.add(outputParam)
                    }
                    LOGGER.info("Stored result for " + complexOutput.id)
                }else{
                    println("did not find expected outut parameter ${expectedOutput.wpsIdentifier} in wps result")
                    throw java.lang.IllegalArgumentException("Not found wps output parameter ${expectedOutput.wpsIdentifier}")
                }
            }


            var processOut = ProcessOutput(
                processID,
                HashMap<String, InlineParameter>(),
                refOutputs
            )
            return processOut
        }catch (e : Exception){
            println(e.message)
            print(e.stackTrace)
            throw e
        }
    }

    fun getOutputById(outputId : String, outputs : List<Data> ) : Data? {
        for(output in outputs){
            if(output.id == outputId){
                return output
            }
        }

        return null;
    }

    fun emptyStringIfNull (nullableString: String?): String {
        if (nullableString == null) {
            return ""
        }
        return nullableString
    }
}