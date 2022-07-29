package org.n.riesgos.asyncwrapper.pulsar

import org.apache.pulsar.client.api.CompressionType
import org.apache.pulsar.client.api.Producer
import org.apache.pulsar.client.api.TypedMessageBuilder
import org.n.riesgos.asyncwrapper.config.PulsarConfiguration
import org.springframework.stereotype.Component

@Component
class PulsarPublisher(var clientService: PulsarClientService, val config: PulsarConfiguration) {

    val topic: String = config.outputTopic

    private val producer: Producer<ByteArray> by lazy {
        clientService.createPulsarConnection().newProducer()
        .topic(topic)
        .compressionType(CompressionType.LZ4)
        .create()
    }


    fun publishMessage(content: String){
        val msg: TypedMessageBuilder<ByteArray> = producer.newMessage();
        msg.value(content.toByteArray())
        //send message
        msg.send()
    }
}