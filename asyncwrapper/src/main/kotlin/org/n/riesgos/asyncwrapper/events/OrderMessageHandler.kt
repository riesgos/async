package org.n.riesgos.asyncwrapper.events

import org.n.riesgos.asyncwrapper.dummy.AbstractWrapper
import org.n.riesgos.asyncwrapper.dummy.WrapperFactory
import org.n.riesgos.asyncwrapper.pulsar.MessageParser
import org.n.riesgos.asyncwrapper.pulsar.PulsarMessageHandler
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import org.springframework.stereotype.Component

@Component
class OrderMessageHandler(private val wrapperService : WrapperFactory, private val publisher: PulsarPublisher) : PulsarMessageHandler {

    protected val wrapperInstance : AbstractWrapper
    protected val msgParser = MessageParser()
    init {
        wrapperInstance = wrapperService.createWrapper()
    }
    override fun handleMessage(source: Any, payload: String) {
        try {
            println("order message handler receiver: $payload")
            //extract order id from paylod
            val orderId = msgParser.parseOrderId(payload)
            //query database and call wps execute
            wrapperInstance.run(orderId)

            //send success message?
            //var successMsg = createMessage(orderId)
            //publisher.publishSuccessMessage(successMsg)

        }catch (e : Exception){
            println(e.message)
            println(e.stackTrace)
            //send failure message?
        }
    }


    private fun createMessage(orderId : Long) : String{
        return msgParser.buildMessageForOrderId(orderId)
    }
}