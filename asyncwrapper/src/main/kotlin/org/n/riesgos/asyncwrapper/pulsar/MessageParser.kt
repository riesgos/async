package org.n.riesgos.asyncwrapper.pulsar

import org.json.JSONObject

class MessageParser {

    private val orderIdKey = "orderId";

    fun  parseOrderId(jsonMsg : String) : Long{
        val jsonObj = JSONObject(jsonMsg)
        val orderId = jsonObj.getLong(orderIdKey)

        return orderId
    }

}