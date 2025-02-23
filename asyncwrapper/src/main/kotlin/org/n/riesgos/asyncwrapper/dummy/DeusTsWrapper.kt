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
import org.n.riesgos.asyncwrapper.dummy.utils.DeusUtils
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
                    DeusUtils(datamanagementRepo).createdWithLiteralInput(
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
                    DeusUtils(datamanagementRepo).isDeusEqOutput(x)

                }
            .collect(Collectors.toList())


        result[WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY] = toComplexInputConstraints(existingIntensityOutputs)
        result[WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY] = toComplexInputConstraints(existingModelpropOutputs)
        result[WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE] = toComplexInputConstraints(existingExposureModelOutputs)

        return result
    }


    override fun getDefaultBBoxConstraints (orderId: Long): Map<String, List<BBoxInputConstraint>> {
        return HashMap<String, MutableList<BBoxInputConstraint>>()
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
                    // -> Deus mapped to hazus, and we need the hazus schema for our tsunami deus input.
                    var extractedSchemas: List<String> = ArrayList<String>()
                    if (exposureConstraint.link != null) {
                        // See more details in the findLiteralInputsForParentProcessOfComplexOutput method
                        val modelpropLiteralInputs = datamanagementRepo.findLiteralInputsForParentProcessOfComplexOutput(WPS_PROCESS_IDENTIFIER_MODELPROP, exposureConstraint.link)
                        extractedSchemas = modelpropLiteralInputs.filter { it.wpsIdentifier == WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA }.map { it.inputValue }.distinct()
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
