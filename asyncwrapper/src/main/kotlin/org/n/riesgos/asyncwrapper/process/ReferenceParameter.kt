package org.n.riesgos.asyncwrapper.process

import org.n.riesgos.asyncwrapper.process.wps.InputType

data class ReferenceParameter(var id : String, var link: String,var mimeType: String, var encoding: String  = "" , var schema: String = "", var type : InputType = InputType.COMPLEX)
