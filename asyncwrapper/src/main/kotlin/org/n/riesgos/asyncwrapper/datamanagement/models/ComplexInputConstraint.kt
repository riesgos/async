package org.n.riesgos.asyncwrapper.datamanagement.models

// link & inputValue are nullable
// we have one or the other
data class ComplexInputConstraint (val link: String?, val inputValue: String?, val mimeType: String, val xmlschema: String, val encoding: String)