package org.n.riesgos.asyncwrapper.datamanagement.utils

import org.json.JSONObject

fun JSONObject.getStringOrDefault (key: String, defaultValue: String?): String? {
    if (this.has(key)) {
        return this.getString(key)
    }
    return defaultValue
}