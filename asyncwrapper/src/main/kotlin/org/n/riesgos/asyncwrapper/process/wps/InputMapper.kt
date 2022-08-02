package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputAsValue
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.n.riesgos.asyncwrapper.process.InlineParameter
import org.n.riesgos.asyncwrapper.process.ProcessInput
import org.n.riesgos.asyncwrapper.process.ReferenceParameter

class InputMapper (var wpsProcessIdentifier : String) {

    private val defaultTextMimeType = "text/xml"

    fun mapInputs(complexRefInputs: List<ComplexInput>, complexValInputs: List<ComplexInputAsValue>, literalInputs: List<LiteralInput>) : ProcessInput {
        val referenceInputParams = HashMap<String, ReferenceParameter>()
        val inlineInputParams = HashMap<String, InlineParameter>()

        for(refInput in complexRefInputs){
            referenceInputParams[refInput.wpsIdentifier] =
                ReferenceParameter(refInput.wpsIdentifier, refInput.link, refInput.mimeType, refInput.encoding, refInput.xmlschema)
        }

        for(litInput in literalInputs){
            inlineInputParams[litInput.wpsIdentifier] = InlineParameter(litInput.wpsIdentifier, litInput.inputValue, defaultTextMimeType)
        }

        for(compValInput in complexValInputs){
            inlineInputParams[compValInput.inputValue] = InlineParameter(compValInput.wpsIdentifier, compValInput.inputValue, compValInput.mimeType, compValInput.encoding, compValInput.xmlschema, InputType.COMPLEX)
        }

        return ProcessInput(wpsProcessIdentifier, inlineInputParams, referenceInputParams)
    }

}