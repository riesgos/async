package org.n.riesgos.asyncwrapper.pulsar

import org.apache.pulsar.client.api.CompressionType
import org.apache.pulsar.client.api.Producer
import org.apache.pulsar.client.api.TypedMessageBuilder
import org.n.riesgos.asyncwrapper.config.PulsarConfiguration
import org.springframework.stereotype.Component

@Component
class PulsarPublisher(var clientService: PulsarClientService, val config: PulsarConfiguration) {

    val successTopic: String = config.outputTopic
    val failureTopic: String = config.failureTopic

    private val producerSuccess: Producer<ByteArray> by lazy {
        clientService.createPulsarConnection().newProducer()
        .topic(successTopic)
        .compressionType(CompressionType.LZ4)
        .create()
    }

    private val producerFailure: Producer<ByteArray> by lazy {
        clientService.createPulsarConnection().newProducer()
            .topic(failureTopic)
            .compressionType(CompressionType.LZ4)
            .create()
    }


    fun publishSuccessMessage(content: String){
        val msg: TypedMessageBuilder<ByteArray> = producerSuccess.newMessage();
        msg.value(content.toByteArray())
        //send message
        msg.send()
    }

    fun publishFailureMessage(content: String){
        val msg: TypedMessageBuilder<ByteArray> = producerFailure.newMessage();
        msg.value(content.toByteArray())
        //send message
        msg.send()
    }
}