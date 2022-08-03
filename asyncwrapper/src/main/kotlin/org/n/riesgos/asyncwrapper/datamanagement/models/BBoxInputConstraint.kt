package org.n.riesgos.asyncwrapper.datamanagement.models

data class BBoxInputConstraint (
        val lowerCornerX: Double,
        val lowerCornerY: Double,
        val upperCornerX: Double,
        val upperCornerY: Double,
        val crs: String
)