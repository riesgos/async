package org.n.riesgos.asyncwrapper.pulsar

import org.apache.pulsar.client.api.Consumer
import org.apache.pulsar.client.api.Message
import org.apache.pulsar.client.api.SubscriptionType

class PulsarConsumer (val topic : String, val subscription : String,  private val clientService: PulsarClientService, private val messageHandler: PulsarMessageHandler): Runnable{

    override fun run() {
        val consumer = createConsumer()
        if (consumer != null) {
            receiveMessages(consumer)
        }
    }

     private fun receiveMessages(consumer : Consumer<ByteArray>){
        while (!Thread.interrupted()) {
            // Wait for a message
            val msg: Message<ByteArray> = consumer.receive()
            try {
                messageHandler.handleMessage(this, String(msg.value))
                //acknowledge message
                consumer.acknowledge(msg)
                println("acknowledge message")
            }catch (e: InterruptedException){
                Thread.currentThread().interrupt()
            }
            catch (e: Exception) {
                println(e)
            }
        }
         println("end thread for subscription $subscription")
         consumer.unsubscribe()
    }

    private fun createConsumer  () : Consumer<ByteArray>? {
        val consumer = clientService.createPulsarConnection().newConsumer()
            .topic(topic)
            .subscriptionType(SubscriptionType.Exclusive)
            .subscriptionName(subscription)
            .subscribe()
        return consumer
    }

}