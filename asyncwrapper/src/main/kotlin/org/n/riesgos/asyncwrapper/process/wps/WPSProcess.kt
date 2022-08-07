package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.config.WPSOutputDefinition
import org.n.riesgos.asyncwrapper.process.*
import org.n.riesgos.asyncwrapper.process.Process
import org.n52.geoprocessing.wps.client.ExecuteRequestBuilder
import org.n52.geoprocessing.wps.client.WPSClientSession
import org.n52.geoprocessing.wps.client.model.*
import org.n52.geoprocessing.wps.client.model.execution.Data
import java.sql.Ref


class WPSProcess(private val wpsClient : WPSClientSession, private val url: String, private val processID: String, private val wpsVersion: String, private val expectedOutputs : List<WPSOutputDefinition>) : Process {

    override fun runProcess(input: ProcessInput): ProcessOutput {

        // take a look at the process description
        val processDescription = wpsClient.getProcessDescription(url, processID, wpsVersion)

        // create the request, add literal input
        val executeBuilder = ExecuteRequestBuilder(processDescription)
        processDescription.inputs.forEach {
            val parameterIn = it.id
            if(!input.inlineParameters.containsKey(parameterIn)){
                return@forEach
            }
            if(it is ComplexInputDescription){
                executeBuilder.addComplexData(parameterIn, input.inlineParameters[parameterIn]!!.value, wpsVersion, "", input.inlineParameters[parameterIn]!!.mimeType)
            }else if(it is LiteralInputDescription){
                executeBuilder.addLiteralData(parameterIn, input.inlineParameters[parameterIn]!!.value, wpsVersion, "", input.inlineParameters[parameterIn]!!.mimeType)
            }else if(it is BoundingBoxInputDescription){
                executeBuilder.addBoundingBoxData(parameterIn, input.bboxParameters[parameterIn]!!.bbox, wpsVersion, "", input.inlineParameters[parameterIn]!!.mimeType)
            }
        }

        for (parameterOut in expectedOutputs) { //set expected output parameters
            executeBuilder.setResponseDocument(parameterOut.identifier, null, null, parameterOut.mimeType) //schema and encoding necessary?
        }

        // build and send the request document

        try {
            val executeRequest = executeBuilder.execute

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
            var refOutputs = HashMap<String, List<ReferenceParameter>>()

            for(expectedOutput in expectedOutputs){
                val output = getOutputById(expectedOutput.identifier, outputs)
                if(output != null){
                    val complexOutput = output.asComplexReferenceData()
                    val outputParam = ReferenceParameter(complexOutput.id, complexOutput.reference.href.toString(), complexOutput.format.mimeType, complexOutput.format.encoding, complexOutput.format.schema)
                    if(!refOutputs.containsKey(complexOutput.id)){
                        refOutputs[complexOutput.id] = mutableListOf(outputParam)
                    }else{
                        //TODO add to list
                    }
                }else{
                    println("did not find expected outut parameter ${expectedOutput.identifier} in wps result")
                    throw java.lang.IllegalArgumentException("unknown wps output parameter ${expectedOutput.identifier}")
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
}