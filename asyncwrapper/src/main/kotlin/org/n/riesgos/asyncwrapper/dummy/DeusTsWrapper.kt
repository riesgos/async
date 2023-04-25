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
import org.n.riesgos.asyncwrapper.dummy.ModelpropEqWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_EQ_SCHEMA_OPTIONS
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


        val WPS_PROCESS_IDENTIFIER_TSUNAMI = "get_tsunamap"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_TSUNAMI_TSNUMAMAP = "tsunamap"

        val WPS_PROCESS_IDENTIFIER_DEUS = "org.n52.gfz.riesgos.algorithm.impl.DeusProcess"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA = "schema"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_ASSETMASTER_SELECTEDROWSGEOJSON = "selectedRowsGeoJson"

        val WPS_PROCESS_IDENTIFIER_MODELPROP = "org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_MODELPROP_SELECTEDROWS = "selectedRows"
        val WPS_PROCESS_IDENTIFIER_ASSETMASTER =  "org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess"


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
                WPS_PROCESS_IDENTIFIER_TSUNAMI,
                WPS_PROCESS_OUTPUT_IDENTIFIER_TSUNAMI_TSNUMAMAP,
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
                        WPS_PROCESS_IDENTIFIER_MODELPROP,
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
                            WPS_PROCESS_IDENTIFIER_ASSETMASTER,
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

    fun createdWithLiteralInput (complexOutput: ComplexOutput, wpsProcessIndentifier: String, wpsInputIdentifier: String, options: List<String>) : Boolean {
        val asInput = ComplexInputConstraint(complexOutput.link, null, complexOutput.mimeType, complexOutput.xmlschema, complexOutput.encoding)
        val literalInputs = datamanagementRepo.findLiteralInputsForComplexOutput(asInput, wpsProcessIndentifier, wpsInputIdentifier)
        return literalInputs.stream().allMatch { x ->
            options.contains(x.inputValue)
        }
    }

    override fun getJobInputs (literalInputs: Map<String, List<String>>, complexInputs: Map<String, List<ComplexInputConstraint>>, bboxInputs: Map<String, List<BBoxInputConstraint>>): List<JobConstraints> {
        val result = ArrayList<JobConstraints>()
        for (intensityConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY, ArrayList())) {
            for (fragilityConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY, ArrayList())) {
                for (exposureConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE, ArrayList())) {
                    // Ok, extracting the schema is a little bit hard.
                    //
                    // The schema value is the one that describes in which the schema currently is.
                    //
                    // For the first deus run (eq case) this is defined by the schema of the assetmaster call that was
                    // used to extract the exposure model.
                    //
                    // For the second one, it is defined by the modelprop schema that was used for the first deus
                    // run - as this defines the possible schema mapping.
                    //
                    // Example:
                    // 1. Assetmaster for eq used sara
                    //    Modelprop for eq used sara
                    //    Deus for eq got sara & returned sara
                    //
                    // -> in this case use sara as input schema for the tsunami deus.
                    //
                    // 2. Assetmaster for eq used sara
                    //    Modelprop for eq used hazus
                    //    Deus for eq got sara & returned hazus
                    //
                    // -> Deus mapped to hazus, and we get the hazus schema for our tsunami deus.
                    val extractedSchemas = ArrayList<String>()
                    if (exposureConstraint.link != null) {
                        // Transform the constraint to an complex output.
                        val deusComplexOutput = datamanagementRepo.complexOutputRepo.findOptionalFirstByLinkMimetypeXmlschemaAndEncoding(exposureConstraint.link, exposureConstraint.mimeType, exposureConstraint.xmlschema, exposureConstraint.encoding)
                        // If we have one, then we want to check all the outputs of other processes that
                        // were used to create the deus output.
                        // If we don't have one, then we stay without extracted schemas - there is no way to
                        // extract them.
                        if (deusComplexOutput != null) {
                            // We then check all the complex outputs of other processes that were used as complex inputs
                            // to create the deus output.
                            val deusInputs = datamanagementRepo.complexOutputAsInputRepo.findInputsByJobId(deusComplexOutput.jobId)
                            for (deusInput in deusInputs) {
                                // We convert those outputs into a constraint that - maybe - was used to create
                                // the complex output that was later used to create deus.
                                val inputConstraint = ComplexInputConstraint(
                                        deusInput.complexOutput.link,
                                        null,
                                        deusInput.complexOutput.mimeType,
                                        deusInput.complexOutput.xmlschema,
                                        deusInput.complexOutput.encoding
                                )
                                for (
                                    // And then we search for the modelprop literal inputs.
                                    literalInput in datamanagementRepo.findLiteralInputsForComplexOutput(
                                        inputConstraint,
                                        WPS_PROCESS_IDENTIFIER_MODELPROP,
                                        WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA)
                                ) {
                                    val value = literalInput.inputValue
                                    if (WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_EQ_SCHEMA_OPTIONS.contains(value)) {
                                        extractedSchemas.add(value)
                                    }
                                }

                            }
                        }
                    }


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