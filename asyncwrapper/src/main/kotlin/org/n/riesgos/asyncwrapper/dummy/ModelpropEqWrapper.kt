package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.BBoxInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.JobConstraints
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import org.n52.geoprocessing.wps.client.model.Format
import org.n52.geoprocessing.wps.client.model.execution.Data
import java.util.*

class ModelpropEqWrapper (val datamanagementRepo: DatamanagementRepo, wpsConfig : WPSConfiguration,
                          publisher: PulsarPublisher
): AbstractWrapper(publisher, wpsConfig) {

    private val wpsURL = wpsConfig.wpsURL
    private val wpsProcessIdentifier = wpsConfig.process

    companion object {
        val WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA = "schema"
        val WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_ASSETCATEGORY = "assetcategory"
        val WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_LOSSCATEGORY = "losscategory"
        val WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_TAXONOMIES = "taxonomies"

        val WPS_PROCESS_OUTPUT_IDENTIFIER_MODELPROP_SELECTEDROWS = "selectedRows"

        val WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA_OPTIONS = Arrays.asList("SARA_v1.0" ) //, "HAZUS_v1.0")
        val WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_ASSETCATEGORY_OPTIONS = Arrays.asList("buildings")
        val WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_LOSSCATEGORY_OPTIONS = Arrays.asList("structural")
        val WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_TAXONOMIES_OPTIONS = Arrays.asList("none")


        // Wrapper name is different from the wps process identifier, as it could
        // be that we use the same process for different tasks.
        // Exp. Damage computation for earthquake damage (eqdeus) vs tsunami damage
        // (tsdeus).
        val WRAPPER_NAME_MODELPROP = "eq-modelprop"
    }

    override fun datamanagementRepo(): DatamanagementRepo {
        return this.datamanagementRepo
    }

    override fun getWrapperName(): String {
        return WRAPPER_NAME_MODELPROP
    }

    override fun getWpsIdentifier(): String {
        return wpsProcessIdentifier
    }

    override fun getWpsUrl(): String {
        return wpsURL
    }

    override fun getDefaultLiteralConstraints (): Map<String, List<String>> {
        val defaultConstraints = HashMap<String, List<String>>()
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA, WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA_OPTIONS)
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_ASSETCATEGORY, WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_ASSETCATEGORY_OPTIONS)
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_LOSSCATEGORY, WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_LOSSCATEGORY_OPTIONS)
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_TAXONOMIES, WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_TAXONOMIES_OPTIONS)
        return defaultConstraints
    }

    override fun getDefaultComplexConstraints(orderId: Long): Map<String, MutableList<ComplexInputConstraint>> {
        return HashMap<String, MutableList<ComplexInputConstraint>>()
    }


    override fun getDefaultBBoxConstraints (orderId: Long): Map<String, List<BBoxInputConstraint>> {
        return HashMap<String, MutableList<BBoxInputConstraint>>()
    }

    override fun getJobInputs (literalInputs: Map<String, List<String>>, complexInputs: Map<String, List<ComplexInputConstraint>>, bboxInputs: Map<String, List<BBoxInputConstraint>>): List<JobConstraints> {
        val result = ArrayList<JobConstraints>()
        for (schemaConstraint in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA, ArrayList())) {
            for (assetCategoryConstraint in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_ASSETCATEGORY, ArrayList())) {
                for (lossCategoryConstraint in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_LOSSCATEGORY, ArrayList())) {
                    for (taxonomyConstraint in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_TAXONOMIES, ArrayList())) {
                        val literalInputValues = HashMap<String, String>()
                        literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA, schemaConstraint)
                        literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_ASSETCATEGORY, assetCategoryConstraint)
                        literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_LOSSCATEGORY, lossCategoryConstraint)
                        literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_TAXONOMIES, taxonomyConstraint)

                        // stays empty
                        val complexInputValues = HashMap<String, ComplexInputConstraint>()
                        val bboxInputValues = HashMap<String, BBoxInputConstraint>()

                        result.add(JobConstraints(literalInputValues, complexInputValues, bboxInputValues))
                    }

                }
            }
        }
        return result
    }

    override fun runWpsItself(): List<Data> {
        fun createFakeData(id: String, mimeType: String, schema: String, encoding: String, link: String): Data {
            val data = Data()
            data.id = id
            val format = Format()
            format.mimeType = mimeType
            format.schema = schema
            format.encoding = encoding
            data.format = format
            data.value = link
            return data

        }
        val outputs = Arrays.asList(
                createFakeData(WPS_PROCESS_OUTPUT_IDENTIFIER_MODELPROP_SELECTEDROWS, "application/json", "", "UTF-8", "https://somewhere/modelprop")
        )
        return outputs
    }
}