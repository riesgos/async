package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.BBoxInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.datamanagement.models.JobConstraints
import org.n.riesgos.asyncwrapper.dummy.AssetmasterWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS
import org.n.riesgos.asyncwrapper.dummy.ModelpropEqWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA
import org.n.riesgos.asyncwrapper.dummy.ModelpropEqWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA_OPTIONS
import org.n52.geoprocessing.wps.client.model.Format
import org.n52.geoprocessing.wps.client.model.execution.Data
import java.util.*
import java.util.stream.Collectors


class DeusWrapper (val datamanagementRepo: DatamanagementRepo) : AbstractWrapper() {

    companion object {
        val WPS_URL = "https://rz-vm140.gfz-potsdam.de/wps/WebProcessingService"

        val WPS_PROCESS_IDENTIFIER_DEUS = "org.n52.gfz.riesgos.algorithm.impl.DeusProcess"
        val WPS_PROCESS_INPUT_IDENTIFIER_DEUS_SCHEMA = "schema"
        val WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY = "intensity"
        val WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE = "exposure"
        val WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY = "fragility"

        val WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_MERGEDOUTPUT = "merged_output"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_SHAPEFILESUMMARY = "shapefile_summary"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_METASUMMARY = "meta_summary"


        val WPS_PROCESS_IDENTIFIER_SHAKYGROUND = "org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKYGROUND_SHAKEMAP_FILE = "shakeMapFile"

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
        return WPS_PROCESS_IDENTIFIER_DEUS
    }

    override fun getWpsUrl(): String {
        return WPS_URL
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
        val existingShakemapOutputs = datamanagementRepo.complexOutputs(
                orderId,
                WPS_PROCESS_IDENTIFIER_SHAKYGROUND,
                WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKYGROUND_SHAKEMAP_FILE
        ).stream().filter({x -> x.mimeType == "text/xml"}).collect(Collectors.toList())
        val existingModelpropOutputs = datamanagementRepo.complexOutputs(
                orderId,
                WPS_PROCESS_IDENTIFIER_MODELPROP,
                WPS_PROCESS_OUTPUT_IDENTIFIER_MODELPROP_SELECTEDROWS
        ).stream()
                .filter({ x -> createdWithLiteralInput(x,
                        WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA,
                        WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS)
                }).collect(Collectors.toList())
        val existingAssetmasterOutputs = datamanagementRepo.complexOutputs(orderId, WPS_PROCESS_IDENTIFIER_ASSETMASTER, WPS_PROCESS_OUTPUT_IDENTIFIER_ASSETMASTER_SELECTEDROWSGEOJSON).stream().filter({x -> x.mimeType == "application/json"}).filter({ x -> createdWithLiteralInput(x, WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA, WPS_PROCESS_INPUT_IDENTIFIER_MODELPROP_SCHEMA_OPTIONS)}).collect(Collectors.toList())


        result.put(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY, existingShakemapOutputs.stream().map { x -> ComplexInputConstraint(x.link, null, x.mimeType, x.xmlschema, x.encoding) }.collect(Collectors.toList()))
        result.put(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY, existingModelpropOutputs.stream().map { x -> ComplexInputConstraint(x.link, null, x.mimeType, x.xmlschema, x.encoding) }.collect(Collectors.toList()))
        result.put(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE, existingAssetmasterOutputs.stream().map { x -> ComplexInputConstraint(x.link, null, x.mimeType, x.xmlschema, x.encoding) }.collect(Collectors.toList()))

        return result
    }


    override fun getDefaultBBoxConstraints (orderId: Long): Map<String, List<BBoxInputConstraint>> {
        return HashMap<String, MutableList<BBoxInputConstraint>>()
    }

    fun createdWithLiteralInput (complexOutput: ComplexOutput, wpsInputIdentifier: String, options: List<String>) : Boolean {
        val asInput = ComplexInputConstraint(complexOutput.link, null, complexOutput.mimeType, complexOutput.xmlschema, complexOutput.encoding)
        val literalInputs = datamanagementRepo.findLiteralInputsForComplexOutput(asInput, wpsInputIdentifier)
        return literalInputs.stream().allMatch({
            x -> options.contains(x.inputValue)
        })
    }


    override fun getJobInputs (literalInputs: Map<String, List<String>>, complexInputs: Map<String, List<ComplexInputConstraint>>, bboxInputs: Map<String, List<BBoxInputConstraint>>): List<JobConstraints> {
        val result = ArrayList<JobConstraints>()
        for (intensityConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_INTENSITY, ArrayList())) {
            for (fragilityConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_FRAGILITY, ArrayList())) {
                for (exposureConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_DEUS_EXPOSURE, ArrayList())) {
                    val extractedSchemas = datamanagementRepo.findLiteralInputsForComplexOutput(exposureConstraint, WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA)
                            .stream()
                            .map({ x -> x.inputValue })
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
                createFakeData(WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_MERGEDOUTPUT, "application/json", "", "UTF-8", "https://somewhere/deus/mergedoutput"),
                createFakeData(WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_SHAPEFILESUMMARY, "appliation/WMS", "", "UTF-8", "https://somewhere/deus/shapefile"),
                createFakeData(WPS_PROCESS_OUTPUT_IDENTIFIER_DEUS_METASUMMARY, "appliation/json", "", "UTF-8", "https://somewhere/deus/metasummary")
        )
        return outputs
    }
}