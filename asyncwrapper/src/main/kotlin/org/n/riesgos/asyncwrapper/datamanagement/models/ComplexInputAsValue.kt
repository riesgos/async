package org.n.riesgos.asyncwrapper.datamanagement.models

data class ComplexInputAsValue (
        val id: Long,
        val jobId: Long,
        val wpsIdentifier: String,
        val inputValue: String,
        val mimeType: String,
        val xmlschema: String,
        val encoding: String
)