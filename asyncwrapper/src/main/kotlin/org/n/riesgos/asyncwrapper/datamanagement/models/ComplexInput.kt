package org.n.riesgos.asyncwrapper.datamanagement.models

data class ComplexInput (
        val id: Long,
        val jobId: Long,
        val wpsIdentifier: String,
        val link: String,
        val mimeType: String,
        val xmlschema: String,
        val encoding: String
)