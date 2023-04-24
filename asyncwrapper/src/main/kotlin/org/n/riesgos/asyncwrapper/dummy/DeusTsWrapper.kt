package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.config.FilestorageConfig
import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.config.WPSOutputDefinition
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.BBoxInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.datamanagement.models.JobConstraints
import org.n.riesgos.asyncwrapper.dummy.AssetmasterWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS
import org.n.riesgos.asyncwrapper.dummy.ModelpropTsWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA
import org.n.riesgos.asyncwrapper.dummy.ModelpropTsWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_TS_SCHEMA_OPTIONS
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import java.util.*
import java.util.stream.Collectors


class DeusTsWrapper (val datamanagementRepo: DatamanagementRepo, wpsConfig : WPSConfiguration, publisher: PulsarPublisher, filestorageConfig: FilestorageConfig) : AbstractWrapper(
    publisher, wpsConfig, filestorageConfig
) {


    private val wpsURL = wpsConfig.wpsURL
    private val wpsProcessIdentifier = wpsConfig.process

    companion object {
        val WPS_PROCESS_INPUT_IDENTIFIER_DEUS_SCHEMA = "schema"
        val WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY = "intensity"
        val WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE = "exposure"
        val WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY = "fragility"

        val WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_MERGEDOUTPUT = "merged_output"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_SHAPEFILESUMMARY = "shapefile_summary"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_METASUMMARY = "meta_summary"


        val WPS_PROCESS_IDENTIFIER_TSUNAMI_RESAMPLER = "get_tsunamap"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_TSUNAMI_FILE = "inundation-shakemap"

        val WPS_PROCESS_IDENTIFIER_DEUS = "org.n52.gfz.riesgos.algorithm.impl.DeusProcess"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA = "schema"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_ASSETMASTER_SELECTEDROWSGEOJSON = "selectedRowsGeoJson"

        val WPS_PROCESS_IDENTIFIER_MODELPROP = "org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_MODELPROP_SELECTEDROWS = "selectedRows"


        // Different to earthquake deus.
        val WRAPPER_NAME_DEUS = "ts-deus"
    }


    override fun datamanagementRepo(): DatamanagementRepo {
        return this.datamanagementRepo
    }

    override fun getWrapperName(): String {
        return WRAPPER_NAME_DEUS
    }

    override fun getWpsIdentifier(): String {
        return wpsProcessIdentifier
    }

    override fun getWpsUrl(): String {
        return wpsURL
    }

    override fun getWpsDialect(): String {
        return wpsConfiguration.dialect
    }

    override fun getDefaultLiteralConstraints (): Map<String, List<String>> {
        val defaultConstraints = HashMap<String, List<String>>()
        // While we could have literal inputs here, we are not going to use them.
        // (The only one would be the schema, but those must be extracted from the assetmaster call that
        // made the exposure model).
        return defaultConstraints

    }

    override fun getDefaultComplexConstraints(orderId: Long): Map<String, MutableList<ComplexInputConstraint>> {

        val result = HashMap<String, MutableList<ComplexInputConstraint>>()
        // We can only handle those that give us the xml output
        val existingIntensityOutputs = datamanagementRepo.findComplexOutputsByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(
                orderId,
                WPS_PROCESS_IDENTIFIER_TSUNAMI_RESAMPLER,
                WPS_PROCESS_OUTPUT_IDENTIFIER_TSUNAMI_FILE,
                "text/xml"
        )
        val existingModelpropOutputs = datamanagementRepo.findComplexOutputsByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(
                orderId,
                WPS_PROCESS_IDENTIFIER_MODELPROP,
                WPS_PROCESS_OUTPUT_IDENTIFIER_MODELPROP_SELECTEDROWS,
                "application/json"
        )
                .stream()
                // We want to run this deus wrapper only for exposure models that
                // were created for the earthquake setting.
                .filter { x ->
                    createdWithLiteralInput(
                        x,
                        WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA,
                        WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_TS_SCHEMA_OPTIONS
                    )
                }
            .collect(Collectors.toList())
        val existingExposureModelOutputs = datamanagementRepo.findComplexOutputsByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(
                orderId,
                WPS_PROCESS_IDENTIFIER_DEUS,
                WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_MERGEDOUTPUT,
                "application/json"
        )
                .stream()
                .filter { x ->
                    isDeusEqOutput(x)

                }
            .collect(Collectors.toList())


        result[WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY] = toComplexInputConstraints(existingIntensityOutputs)
        result[WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY] = toComplexInputConstraints(existingModelpropOutputs)
        result[WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE] = toComplexInputConstraints(existingExposureModelOutputs)

        return result
    }

    private fun isDeusEqOutput (deusOutput: ComplexOutput): Boolean {
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
                            WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA,
                            WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS)
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

    override fun getDefaultBBoxConstraints (orderId: Long): Map<String, List<BBoxInputConstraint>> {
        return HashMap<String, MutableList<BBoxInputConstraint>>()
    }

    fun createdWithLiteralInput (complexOutput: ComplexOutput, wpsInputIdentifier: String, options: List<String>) : Boolean {
        val asInput = ComplexInputConstraint(complexOutput.link, null, complexOutput.mimeType, complexOutput.xmlschema, complexOutput.encoding)
        val literalInputs = datamanagementRepo.findLiteralInputsForComplexOutput(asInput, wpsInputIdentifier)
        return literalInputs.stream().allMatch { x ->
            options.contains(x.inputValue)
        }
    }

    override fun getJobInputs (literalInputs: Map<String, List<String>>, complexInputs: Map<String, List<ComplexInputConstraint>>, bboxInputs: Map<String, List<BBoxInputConstraint>>): List<JobConstraints> {
        val result = ArrayList<JobConstraints>()
        for (intensityConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY, ArrayList())) {
            for (fragilityConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY, ArrayList())) {
                for (exposureConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE, ArrayList())) {
                    // Ok, we got the exposure model.
                    // We are sure that it is in an schema for earthquakes.
                    // In general we would be check back in the time line what the fragility model schema was
                    // that was applied to the first run of deus, but this is rather complex.

                    // For now we do something more simple: We check the schema value that we had for the previous call
                    // of deus.
                    // Important to know is that this parameter specifies the input schema, it is not the value
                    // of the output schema.
                    // The only trick here is that there is no schema mapping done in this first step.
                    // The input & output schemas for the earthquake deus call are identical.
                    val extractedSchemas = datamanagementRepo.findLiteralInputsForComplexOutput(exposureConstraint, WPS_PROCESS_INPUT_IDENTIFIER_DEUS_SCHEMA)
                            .stream()
                            .map { x -> x.inputValue }
                        .collect(Collectors.toList())


                    for (schemaConstraint in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_SCHEMA, extractedSchemas)) {
                        val literalInputValues = HashMap<String, String>()
                        literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_SCHEMA, schemaConstraint)

                        val complexInputValues = HashMap<String, ComplexInputConstraint>()
                        complexInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE, exposureConstraint)
                        complexInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY, fragilityConstraint)
                        complexInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY, intensityConstraint)

                        val bboxInputValues = HashMap<String, BBoxInputConstraint>()

                        result.add(JobConstraints(literalInputValues, complexInputValues, bboxInputValues))
                    }

                }
            }
        }
        return result
    }

    override fun getRequestedOutputs(): List<WPSOutputDefinition> {
        return Arrays.asList(
                WPSOutputDefinition(WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_MERGEDOUTPUT, "application/json", "", "UTF-8"),
                WPSOutputDefinition(WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_SHAPEFILESUMMARY, "application/WMS", "", "UTF-8"),
                WPSOutputDefinition(WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_METASUMMARY, "application/json", "", "UTF-8")
        )
    }
}