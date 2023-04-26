package org.n.riesgos.asyncwrapper.dummy.utils

import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.dummy.AssetmasterWrapper
import org.n.riesgos.asyncwrapper.dummy.DeusTsWrapper

class DeusUtils (val datamanagementRepo: DatamanagementRepo) {
    fun createdWithLiteralInput (complexOutput: ComplexOutput, wpsProcessIdentifier: String, wpsInputIdentifier: String, options: List<String>): Boolean {
        val asInput = ComplexInputConstraint(complexOutput.link, null, complexOutput.mimeType, complexOutput.xmlschema, complexOutput.encoding)
        val literalInputs = datamanagementRepo.findLiteralInputsForComplexOutput(asInput, wpsProcessIdentifier, wpsInputIdentifier)
        if (literalInputs.isEmpty()) {
            return false
        }
        return literalInputs.stream().allMatch { x ->
            options.contains(x.inputValue)
        }
    }

    fun isDeusEqOutput (deusOutput: ComplexOutput): Boolean {
        // Ok, we search for the inputs that we used to create the deus output.
        // We search here for the complex inputs.
        // And we expect them to be the output of another process.
        // So we check the complexOutputsAsInputs.
        val complexOutputsOfOtherProcessesAsInputsForDeusOutput = this.datamanagementRepo.complexOutputAsInputRepo.findInputsByJobId(deusOutput.jobId)
        for (input in complexOutputsOfOtherProcessesAsInputsForDeusOutput) {
            // Ok, we have in input for deus that was already a complex output of another process.
            val output = input.complexOutput
            // We then want to check if this was created by assetmaster. If so,
            if (
                    createdWithLiteralInput(
                            output,
                            DeusTsWrapper.WPS_PROCESS_IDENTIFIER_ASSETMASTER,
                            DeusTsWrapper.WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA,
                            AssetmasterWrapper.WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS)
            ) {
                // Now we are sure that the deus output that we have here used the
                // assetmaster value directly. It has only one processing of the deus
                // run. And as it also checked for the schema values for assetmaster
                // (and those include only the earthquake schemas for the moment),
                // we can be sure that this is the earthquake deus output that
                // we want to put into the tsunami damage computation.
                return true
            }
        }

        return false
    }
}