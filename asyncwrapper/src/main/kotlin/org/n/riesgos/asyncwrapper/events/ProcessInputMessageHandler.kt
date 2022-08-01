package org.n.riesgos.asyncwrapper.events


import org.apache.pulsar.client.api.Message
import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.process.InlineParameter
import org.n.riesgos.asyncwrapper.process.ProcessInput
import org.n.riesgos.asyncwrapper.process.ReferenceParameter
import org.n.riesgos.asyncwrapper.process.wps.WPSClientService
import org.n.riesgos.asyncwrapper.process.wps.WPSProcess
import org.n.riesgos.asyncwrapper.pulsar.PulsarMessageHandler
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class ProcessInputMessageHandler(var publisher: PulsarPublisher, val clientService : WPSClientService, val config : WPSConfiguration) : PulsarMessageHandler {
    override fun handleMessage(source: Any, payload: String){
        println("received message: $payload")
        val wpsClient = clientService.establishWPSConnection()
        val process = WPSProcess(wpsClient, config.wpsURL, config.process, config.wpsVersion)
        val inputParam = InlineParameter("literalInput", payload, "text/xml");
        val input = ProcessInput("", mapOf("literalInput" to inputParam), HashMap<String, ReferenceParameter>())
        val output = process.runProcess(input)
        println("publish process output: $output")
        publisher.publishMessage(output.toString())
    }
}