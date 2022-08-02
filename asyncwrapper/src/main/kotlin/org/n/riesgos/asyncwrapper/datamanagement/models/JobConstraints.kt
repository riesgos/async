package org.n.riesgos.asyncwrapper.datamanagement.models

class JobConstraints (val literalConstraints: Map<String, String>, val complexConstraints: Map<String, ComplexInputConstraint>, val bboxConstraints: Map<String, BBoxInputConstraint>)