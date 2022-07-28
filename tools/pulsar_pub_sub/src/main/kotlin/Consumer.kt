import org.apache.pulsar.client.api.*

fun main(args: Array<String>){
    val client = PulsarClient.builder()
        .serviceUrl("pulsar://localhost:6650")
        .build()

    val consumer = client.newConsumer()
        .topic("output-topic")
        .subscriptionType(SubscriptionType.Exclusive)
        .subscriptionName("Test-Subscription")
        .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
        .subscribe()

    println("waiting for messages")

    while (true) {
        // Wait for a message
        val msg: Message<ByteArray> = consumer.receive()

        try {
            // Do something with the message
            println("Message received: " + String(msg.value))
            consumer.acknowledge(msg)
        } catch (e: Exception) {
            println(e)
        }
    }
}