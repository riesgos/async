package org.n.riesgos.asyncwrapper.process

import org.n.riesgos.asyncwrapper.process.InlineParameter
import org.n.riesgos.asyncwrapper.process.ReferenceParameter
import org.n52.geoprocessing.wps.client.model.execution.BoundingBox

data class ProcessInput(var processId: String, var inlineParameters: Map<String, InlineParameter> = HashMap<String, InlineParameter>(), var referenceParameters: Map<String, ReferenceParameter> = HashMap<String, ReferenceParameter>(), var bboxParameters : Map<String, BboxParameter> = HashMap<String, BboxParameter>())
