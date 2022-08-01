package org.n.riesgos.asyncwrapper.pulsar

import org.apache.pulsar.client.api.Consumer
import org.n.riesgos.asyncwrapper.config.AppConfiguration
import org.n.riesgos.asyncwrapper.config.PulsarConfiguration
import org.n.riesgos.asyncwrapper.events.OrderMessageHandler
import org.n.riesgos.asyncwrapper.events.ProcessInputMessageHandler
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class PulsarSubscriptionInitializer(val appConfig: AppConfiguration, val pulsarConfig: PulsarConfiguration, val clientService: PulsarClientService, private val inputMessageHandler: ProcessInputMessageHandler, private val orderMessageHandler: OrderMessageHandler) {

    private var consumerThreads :  List<Thread> = ArrayList<Thread>()

    @PostConstruct
    fun initSubscriptions(){
        println("create consumer threads for input messages")
        for(topic in pulsarConfig.inputTopics) {
            val subscriptionName = "${appConfig.appID}_${topic}_subscription"
            val consumer = PulsarConsumer(topic, subscriptionName, clientService, inputMessageHandler)
            startConsumerThread(consumer, subscriptionName)
        }

        if(pulsarConfig.orderTopic != null) {
            println("create consumer thread for order messages")
            val subscriptionName = "${appConfig.appID}_${pulsarConfig.orderTopic}_subscription"
            val consumer = PulsarConsumer(pulsarConfig.orderTopic!!, subscriptionName, clientService, orderMessageHandler)
            startConsumerThread(consumer, subscriptionName)
        }
    }

    @PreDestroy
    fun clearSubscriptions(){
        for(consumerThread in consumerThreads){
            if(!consumerThread.isInterrupted || !consumerThread.isAlive){
                consumerThread.interrupt()
                consumerThreads -= consumerThread
            }
        }
    }

    private fun startConsumerThread(consumer: PulsarConsumer, name: String){
        println("start new thread for subscription $name")
        val consumerThread = Thread(consumer, name)
        consumerThreads += consumerThread
        consumerThread.start()
    }
}