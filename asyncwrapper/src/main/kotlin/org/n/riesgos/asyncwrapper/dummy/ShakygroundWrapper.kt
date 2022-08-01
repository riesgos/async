package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.JobConstraints
import org.n52.geoprocessing.wps.client.model.Format
import org.n52.geoprocessing.wps.client.model.execution.Data
import java.util.*


class ShakygroundWrapper (val datamanagementRepo: DatamanagementRepo) : AbstractWrapper() {

    companion object {
        val WPS_URL = "https://rz-vm140.gfz-potsdam.de/wps/WebProcessingService"

        val WPS_PROCESS_IDENTIFIER_QUAKELEDGER = "org.n52.gfz.riesgos.algorithm.impl.QuakeledgerProcess"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_QUAKELEDGER_QUAKEML = "selectedRows"

        val WPS_PROCESS_IDENTIFIER_SHAKYGROUND = "org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess"

        val WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE = "gmpe"
        val WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID = "vsgrid"
        val WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_QUAKEML_FILE = "quakeMLFile"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKYGROUND_SHAKEMAP_FILE = "shakeMapFile"

        val WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE_OPTIONS = Arrays.asList("MontalvaEtAl2016SInter","GhofraniAtkinson2014","AbrahamsonEtAl2015SInter","YoungsEtAl1997SInterNSHMP2008")
        val WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID_OPTIONS = Arrays.asList("USGSSlopeBasedTopographyProxy","FromSeismogeotechnicsMicrozonation")


        // Wrapper name is different from the wps process identifier, as it could
        // be that we use the same process for different tasks.
        // Exp. Damage computation for earthquake damage (eqdeus) vs tsunami damage
        // (tsdeus).
        val WRAPPER_NAME_SHAKYGROUND = "shakyground"
    }


    override fun datamanagementRepo(): DatamanagementRepo {
        return this.datamanagementRepo
    }

    override fun getWrapperName(): String {
        return WRAPPER_NAME_SHAKYGROUND
    }

    override fun getWpsIdentifier(): String {
        return WPS_PROCESS_IDENTIFIER_SHAKYGROUND
    }

    override fun getWpsUrl(): String {
        return WPS_URL
    }

    override fun getDefaultLiteralConstraints (): Map<String, List<String>> {
        val defaultConstraints = HashMap<String, List<String>>()
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE, WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE_OPTIONS)
        defaultConstraints.put(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID, WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID_OPTIONS)
        return defaultConstraints

    }

    override fun getDefaultComplexConstraints(orderId: Long): Map<String, MutableList<ComplexInputConstraint>> {

        val result = HashMap<String, MutableList<ComplexInputConstraint>>()
        // We search for existing quakeML outputs that are already in our database for our order.
        val existingQuakeMLOutputs = datamanagementRepo.complexOutputs(orderId, WPS_PROCESS_IDENTIFIER_QUAKELEDGER, WPS_PROCESS_OUTPUT_IDENTIFIER_QUAKELEDGER_QUAKEML)
        for (existingQuakeMLOutput in existingQuakeMLOutputs) {
            val innerList = result.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_QUAKEML_FILE, ArrayList())
            innerList.add(
                ComplexInputConstraint(
                    existingQuakeMLOutput.link,
                    null,
                    existingQuakeMLOutput.mimeType,
                    existingQuakeMLOutput.xmlschema,
                    existingQuakeMLOutput.encoding
                )
            )
        }
        return result
    }


    override fun getJobInputs (literalInputs: Map<String, List<String>>, complexInputs: Map<String, List<ComplexInputConstraint>>): List<JobConstraints> {
        val result = ArrayList<JobConstraints>()
        for (gmpeConstraint in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE, ArrayList())) {
            for (vsgridConstraint in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID, ArrayList())) {
                for (quakeMlConstraint in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_QUAKEML_FILE, ArrayList())) {

                    val literalInputValues = HashMap<String, String>()
                    literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE, gmpeConstraint)
                    literalInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID, vsgridConstraint)

                    val complexInputValues = HashMap<String, ComplexInputConstraint>()
                    complexInputValues.put(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_QUAKEML_FILE, quakeMlConstraint)

                    result.add(JobConstraints(literalInputValues, complexInputValues))
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
                createFakeData(WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKYGROUND_SHAKEMAP_FILE, "text/xml", "http://earthquake.usgs.gov/eqcenter/shakemap", "UTF-8", "https://somewhere"),
                createFakeData(WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKYGROUND_SHAKEMAP_FILE, "appliation/WMS", "", "UTF-8", "https://somewhere/else")
        )
        return outputs
    }
}