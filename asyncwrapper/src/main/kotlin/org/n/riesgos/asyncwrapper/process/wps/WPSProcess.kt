package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.process.*
import org.n52.geoprocessing.wps.client.ExecuteRequestBuilder
import org.n52.geoprocessing.wps.client.WPSClientSession
import org.n52.geoprocessing.wps.client.model.ResponseMode
import org.n52.geoprocessing.wps.client.model.StatusInfo
import org.n52.geoprocessing.wps.client.model.TransmissionMode
import org.n52.geoprocessing.wps.client.model.execution.Data
import org.n52.geoprocessing.wps.client.model.execution.ExecutionMode


class WPSProcess(private val wpsClient : WPSClientSession, private val url: String, private val processID: String, private val wpsVersion: String) : Process {

    override fun runProcess(input: ProcessInput): ProcessOutput {

        // take a look at the process description
        val processDescription = wpsClient.getProcessDescription(url, processID, wpsVersion)

        // create the request, add literal input
        val executeBuilder = ExecuteRequestBuilder(processDescription)
        val parameterIn = "literalInput"

        executeBuilder.addLiteralData(parameterIn, input.inlineParameters[parameterIn]!!.value, wpsVersion, "", input.inlineParameters[parameterIn]!!.mimeType)
        val parameterOut = "literalOutput"
        executeBuilder.setResponseDocument(parameterOut, null, null, "text/xml")

        // build and send the request document

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
            println(outputs[0])
            val stringOutput = outputs[0].asLiteralData()
            var literalOutput = InlineParameter(parameterOut, stringOutput.value.toString(), "text/xml")
            var processOut = ProcessOutput(processID, mapOf(parameterOut to literalOutput), HashMap<String, List<ReferenceParameter>>())
            return processOut
        }catch (e : Exception){
            println(e.message)
            print(e.stackTrace)
            throw e
        }
    }
}