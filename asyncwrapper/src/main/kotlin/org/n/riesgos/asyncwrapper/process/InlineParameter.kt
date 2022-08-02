package org.n.riesgos.asyncwrapper.process

import org.n.riesgos.asyncwrapper.process.wps.InputType

data class InlineParameter(var id : String, var value: String, var mimeType: String , var encoding: String = "", var schema: String = "", var type: InputType = InputType.LITERAL)

