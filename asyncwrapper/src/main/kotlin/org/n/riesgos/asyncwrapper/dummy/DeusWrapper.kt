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
import org.n.riesgos.asyncwrapper.dummy.ModelpropEqWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA
import org.n.riesgos.asyncwrapper.dummy.ModelpropEqWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA_OPTIONS
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import java.util.*
import java.util.stream.Collectors


class DeusWrapper (val datamanagementRepo: DatamanagementRepo, wpsConfig : WPSConfiguration, publisher: PulsarPublisher, filestorageConfig: FilestorageConfig) : AbstractWrapper(
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


        val WPS_PROCESS_IDENTIFIER_SHAKEMAP_RESAMPLER = "org.n52.gfz.riesgos.algorithm.impl.shakemap_sampler"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKEMAP_RESAMPLER_SHAKEMAP_FILE = "intensity_output_file"

        val WPS_PROCESS_IDENTIFIER_ASSETMASTER = "org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA = "schema"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_ASSETMASTER_SELECTEDROWSGEOJSON = "selectedRowsGeoJson"

        val WPS_PROCESS_IDENTIFIER_MODELPROP = "org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_MODELPROP_SELECTEDROWS = "selectedRows"




        // Wrapper name is different from the wps process identifier, as it could
        // be that we use the same process for different tasks.
        // Exp. Damage computation for earthquake damage (eqdeus) vs tsunami damage
        // (tsdeus).
        val WRAPPER_NAME_DEUS = "eq-deus"
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
        val existingShakemapOutputs = datamanagementRepo.findComplexOutputsByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(
                orderId,
                WPS_PROCESS_IDENTIFIER_SHAKEMAP_RESAMPLER,
                WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKEMAP_RESAMPLER_SHAKEMAP_FILE,
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
                        WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA_OPTIONS
                    )
                }
            .collect(Collectors.toList())
        val existingAssetmasterOutputs = datamanagementRepo.findComplexOutputsByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(
                orderId,
                WPS_PROCESS_IDENTIFIER_ASSETMASTER,
                WPS_PROCESS_OUTPUT_IDENTIFIER_ASSETMASTER_SELECTEDROWSGEOJSON,
                "application/json"
        )
                .stream()
                .filter { x ->
                    createdWithLiteralInput(
                        x,
                        WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA,
                        WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS
                    )
                }
            .collect(Collectors.toList())


        result[WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY] = toComplexInputConstraints(existingShakemapOutputs)
        result[WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY] = toComplexInputConstraints(existingModelpropOutputs)
        result[WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE] = toComplexInputConstraints(existingAssetmasterOutputs)

        return result
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
                    val extractedSchemas = datamanagementRepo.findLiteralInputsForComplexOutput(exposureConstraint, WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA)
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