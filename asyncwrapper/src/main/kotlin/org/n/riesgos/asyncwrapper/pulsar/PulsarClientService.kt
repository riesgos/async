package org.n.riesgos.asyncwrapper.pulsar

import org.apache.pulsar.client.api.PulsarClient
import org.n.riesgos.asyncwrapper.config.PulsarConfiguration
import org.springframework.stereotype.Service

@Service
class PulsarClientService(val config: PulsarConfiguration) {

    val pulsarURL : String = config.pulsarURL

    fun createPulsarConnection() : PulsarClient{
        println("connect to pulsar at $pulsarURL")
        val pulsarClient = PulsarClient.builder()
            .serviceUrl(pulsarURL)
            .build() //build already establishs connection
        return pulsarClient
    }

}