import org.apache.pulsar.client.api.*
import java.util.stream.IntStream


fun main(args: Array<String>) {
    //create pulsar client
    val client = PulsarClient.builder()
        .serviceUrl("pulsar://localhost:6650")
        .build()

    //create producer
    val producer: Producer<ByteArray> = client.newProducer()
        .topic("input-topic1")
        .compressionType(CompressionType.LZ4)
        .create()



    //create and send message messages
   IntStream.range(1, 5).forEach { i: Int ->
        val content = String.format("hi-pulsar-%d", i)
        val msg: TypedMessageBuilder<ByteArray> = producer.newMessage();
        msg.value(content.toByteArray())
        //send message
        msg.send()
       println("sent message $i")
    }




}