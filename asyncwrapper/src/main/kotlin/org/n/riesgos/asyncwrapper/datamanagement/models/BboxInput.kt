package org.n.riesgos.asyncwrapper.datamanagement.models

data class BboxInput (
        val id: Long,
        val jobId: Long,
        val wpsIdentifier: String,
        val lowerCornerX: Double,
        val lowerCornerY: Double,
        val upperCornerX: Double,
        val upperCornerY: Double,
        val crs: String
)