package org.n.riesgos.asyncwrapper.pulsar

interface PulsarMessageHandler {
    fun handleMessage(source: Any, payload: String)
}