package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.config.WPSOutputDefinition
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.BBoxInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.JobConstraints
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import org.n52.geoprocessing.wps.client.model.Format
import org.n52.geoprocessing.wps.client.model.execution.Data
import java.util.*

class AssetmasterWrapper (val datamanagementRepo: DatamanagementRepo, wpsConfig : WPSConfiguration,
                          publisher: PulsarPublisher
): AbstractWrapper(publisher, wpsConfig) {

    private val wpsURL = wpsConfig.wpsURL
    private val wpsProcessIdentifier = wpsConfig.process

    companion object {

        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LONMIN = "lonmin"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LONMAX = "lonmax"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LATMIN = "latmin"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LATMAX = "latmax"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA = "schema"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_ASSETTYPE = "assettype"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_QUERYMODE = "querymode"
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_MODEL = "model"

        val WPS_PROCESS_OUTPUT_IDENTIFIER_ASSETMASTER_SELECTEDROWSGEOJSON = "selectedRowsGeoJson"

        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS = Arrays.asList("SARA_v1.0") //, "HAZUS_v1.0")
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_ASSETTYPE_OPTIONS = Arrays.asList("res")
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_QUERYMODE_OPTIONS = Arrays.asList("intersects") // we could allow within as well, but that would not not that much of a difference
        val WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_MODEL_OPTIONS = Arrays.asList(
                // Chile
                "ValpCVTBayesian", "ValpCommuna", "ValpRegularOriginal", "ValpRegularGrid",
                // Lima
                "LimaCVT1_PD30_TI70_5000", "LimaCVT2_PD30_TI70_10000", "LimaCVT3_PD30_TI70_50000", "LimaCVT4_PD40_TI60_5000", "LimaCVT5_PD40_TI60_10000", "LimaCVT6_PD40_TI60_50000"
                // Please note: We don't include "LimaBlocks" for the moment, as this could make a lot of computing time necessary
                // we also don't include the models for ecuador for now.

        )


        // Wrapper name is different from the wps process identifier, as it could
        // be that we use the same process for different tasks.
        // Exp. Damage computation for earthquake damage (eqdeus) vs tsunami damage
        // (tsdeus).
        val WRAPPER_NAME_ASSETMASTER = "assetmaster"
    }

    override fun datamanagementRepo(): DatamanagementRepo {
        return this.datamanagementRepo
    }

    override fun getWrapperName(): String {
        return WRAPPER_NAME_ASSETMASTER
    }

    override fun getWpsIdentifier(): String {
        return wpsProcessIdentifier
    }

    override fun getWpsUrl(): String {
        return wpsURL
    }

    override fun getDefaultLiteralConstraints(): Map<String, List<String>> {
        val defaultConstraints = HashMap<String, List<String>>()
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA, WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS)
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_ASSETTYPE, WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_ASSETTYPE_OPTIONS)
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_QUERYMODE, WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_QUERYMODE_OPTIONS)
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_MODEL, WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_MODEL_OPTIONS)
        return defaultConstraints
    }

    override fun getDefaultComplexConstraints(orderId: Long): Map<String, MutableList<ComplexInputConstraint>> {
        return HashMap<String, MutableList<ComplexInputConstraint>>()
    }

    override fun getDefaultBBoxConstraints (orderId: Long): Map<String, List<BBoxInputConstraint>> {
        // We don't use bbox inputs for assetmaster (we use several literal inputs instead)
        return HashMap<String, MutableList<BBoxInputConstraint>>()
    }

    override fun getJobInputs(literalInputs: Map<String, List<String>>, complexInputs: Map<String, List<ComplexInputConstraint>>, bboxInputs: Map<String, List<BBoxInputConstraint>>): List<JobConstraints> {
        val result = ArrayList<JobConstraints>()
        val lonmins = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LONMIN, ArrayList())
        val lonmaxs = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LONMAX, ArrayList())
        val latmins = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LATMIN, ArrayList())
        val latmaxs = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LATMAX, ArrayList())

        LOGGER.info("Lonmins: " + lonmins.toString())
        LOGGER.info("Lonmaxs: " + lonmaxs.toString())
        LOGGER.info("Latmins: " + latmins.toString())
        LOGGER.info("Latmaxs: " + latmaxs.toString())

        val minLength = Math.min(lonmins.size, Math.min(lonmaxs.size, Math.min(latmins.size, latmaxs.size)))



        val schemas = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA, ArrayList())
        val assetTypes = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_ASSETTYPE, ArrayList())
        val queryModes = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_QUERYMODE, ArrayList())
        val models = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_MODEL, ArrayList())

        LOGGER.info("schemas: " + schemas.toString())
        LOGGER.info("assetTypes: " + assetTypes.toString())
        LOGGER.info("queryModes: " + queryModes.toString())
        LOGGER.info("models: " + models.toString())

        for (i in 0 until minLength) {
            val lonminConstraint = lonmins.get(i)
            val lonmaxConstraint = lonmaxs.get(i)
            val latminConstraint = latmins.get(i)
            val latmaxConstraint = latmaxs.get(i)

            for (schemaConstraint in schemas) {
                for (assetTypeConstraint in assetTypes) {
                    for (queryModeConstraint in queryModes) {
                        for (modelConstraint in models) {
                            val literalInputValues = HashMap<String, String>()
                            literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA, schemaConstraint)
                            literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_ASSETTYPE, assetTypeConstraint)
                            literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_QUERYMODE, queryModeConstraint)
                            literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_MODEL, modelConstraint)
                            literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LATMAX, latmaxConstraint)
                            literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LATMIN, latminConstraint)
                            literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LONMAX, lonmaxConstraint)
                            literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_LONMIN, lonminConstraint)

                            // stays empty
                            val complexInputValues = HashMap<String, ComplexInputConstraint>()
                            val bboxInputValues = HashMap<String, BBoxInputConstraint>()

                            LOGGER.info("Added concrete parameterized job")
                            result.add(JobConstraints(literalInputValues, complexInputValues, bboxInputValues))
                        }
                    }
                }
            }
        }


        LOGGER.info("" + result.size.toString() + " parameterized jobs extracted")
        return result
    }

    override fun getRequestedOutputs(): List<WPSOutputDefinition> {
        return Arrays.asList(
                WPSOutputDefinition(WPS_PROCESS_OUTPUT_IDENTIFIER_ASSETMASTER_SELECTEDROWSGEOJSON, "application/json", "", "UTF-8")
        )
    }
}
