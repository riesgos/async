package org.n.riesgos.asyncwrapper.process

import org.n.riesgos.asyncwrapper.process.InlineParameter
import org.n.riesgos.asyncwrapper.process.ReferenceParameter

data class ProcessInput(var processId: String, var inlineParameters: Map<String, InlineParameter>, var referenceParameters: Map<String, ReferenceParameter>)
