package org.n.riesgos.asyncwrapper.datamanagement.models

import org.json.JSONObject

data class Order (val id: Long, val orderConstraints: JSONObject?, val userId: Long)