package org.n.riesgos.asyncwrapper.pulsar

import org.apache.pulsar.client.api.Consumer
import org.apache.pulsar.client.api.Message
import org.apache.pulsar.client.api.PulsarClientException
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
        // This construct makes the creation of the consumer a little bit more
        // robust, as it is possible that the broker has some problems
        // on getting out of sync. Then it throws an exception.
        // If we would not catch that (& then retry) the thread would
        // just die.
        var connected = false
        var consumer: Consumer<ByteArray>? = null
        while (!connected) {
            try {
                consumer = clientService.createPulsarConnection().newConsumer()
                        .topic(topic)
                        .subscriptionType(SubscriptionType.Exclusive)
                        .subscriptionName(subscription)
                        .subscribe()

                connected = consumer.isConnected
                if (!connected) {
                    consumer.close()
                }
            } catch (ex: PulsarClientException) {
                ex.printStackTrace()
                println("Retry the consumer creation in 5 seconds")
                Thread.sleep(5_000)
            }
        }
        return consumer
    }

}