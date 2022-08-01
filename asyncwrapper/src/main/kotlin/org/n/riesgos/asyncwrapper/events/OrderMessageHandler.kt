package org.n.riesgos.asyncwrapper.events

import org.n.riesgos.asyncwrapper.pulsar.PulsarMessageHandler
import org.springframework.stereotype.Component

@Component
class OrderMessageHandler : PulsarMessageHandler {
    override fun handleMessage(source: Any, payload: String) {
        println("order message handler receiver $payload")
        TODO("Not yet implemented")
    }
}