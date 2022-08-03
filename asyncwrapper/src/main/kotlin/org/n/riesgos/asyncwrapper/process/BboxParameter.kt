package org.n.riesgos.asyncwrapper.process

import org.n52.geoprocessing.wps.client.model.execution.BoundingBox

data class BboxParameter(var id : String, var bbox : BoundingBox)
