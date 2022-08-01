package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.process.ProcessInput
import org.n.riesgos.asyncwrapper.process.ProcessOutput
import java.net.PortUnreachableException

abstract class OutputMapper(private val outputs : List<ProcessOutput>) {

    abstract fun mapInputs() : List<ComplexOutput>

}