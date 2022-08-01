package org.n.riesgos.asyncwrapper.datamanagement.models

sealed class ParsedConstraintsResult

data class JobIdConstraintResult (val jobId: Long): ParsedConstraintsResult()
data class OrderConstraintsResult (val literalConstraints: Map<String, List<String>>, val complexConstraints: Map<String, MutableList<ComplexInputConstraint>>): ParsedConstraintsResult()
