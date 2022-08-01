package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputAsValue
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.n.riesgos.asyncwrapper.process.ProcessInput

abstract class InputMapper (private val complexRefInputs: List<ComplexInput>, private val complexValInputs: List<ComplexInputAsValue>, private val literalInputs: List<LiteralInput>) {

    abstract fun mapInputs() : List<ProcessInput>

    protected fun getComplexRefInputByName(name : String) : ComplexInput?{
        for (input in complexRefInputs) {
            if (input.wpsIdentifier == name) {
                return input
            }
        }
        return null;
    }

    protected fun getComplexValInputByName(name : String) : ComplexInputAsValue?{
        for (input in complexValInputs) {
            if (input.wpsIdentifier == name) {
                return input
            }
        }
        return null;
    }

    protected fun getLiteralInputByName(name : String) : LiteralInput?{
        for (input in literalInputs) {
            if (input.wpsIdentifier == name) {
                return input
            }
        }
        return null;
    }
}