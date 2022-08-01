package org.n.riesgos.asyncwrapper.datamanagement.models

data class LiteralInput (
        val id: Long,
        val jobId: Long,
        val wpsIdentifier: String,
        val inputValue: String
)