package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.config.FilestorageConfig
import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.config.WPSOutputDefinition
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.BBoxInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.JobConstraints
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import java.util.*
import kotlin.collections.ArrayList

class TsunamiWrapper (val datamanagementRepo: DatamanagementRepo, wpsConfig: WPSConfiguration,
                      publisher: PulsarPublisher, filestorageConfig: FilestorageConfig
) : AbstractWrapper (publisher, wpsConfig, filestorageConfig)  {

    private val wpsURL = wpsConfig.wpsURL
    private val wpsProcessIdentifier = wpsConfig.process
    private val wpsDialect = wpsConfig.dialect

    companion object {
        val WPS_PROCESS_INPUT_IDENTIFIER_LONGITUDE = "lon"
        val WPS_PROCESS_INPUT_IDENTIFIER_LATITUDE = "lat"
        val WPS_PROCESS_INPUT_IDENTIFIER_MAGNITUDE = "mag"
        val WPS_PROCESS_OUTPUT_IDENTIFIER_INUNDATION_SHAKEMAP = "tsunamap"
        val WRAPPER_NAME = "tsunami"
    }

    override fun datamanagementRepo(): DatamanagementRepo {
        return this.datamanagementRepo
    }

    override fun getWrapperName(): String {
        return WRAPPER_NAME
    }

    override fun getWpsIdentifier(): String {
        return wpsProcessIdentifier
    }

    override fun getWpsUrl(): String {
        return wpsURL
    }

    override fun getWpsDialect(): String {
        return wpsDialect
    }


    override fun getDefaultLiteralConstraints(): Map<String, List<String>> {
        return HashMap<String, MutableList<String>>()
    }

    override fun getDefaultComplexConstraints(orderId: Long): Map<String, List<ComplexInputConstraint>> {
        return  HashMap<String, MutableList<ComplexInputConstraint>>()
    }

    override fun getDefaultBBoxConstraints(orderId: Long): Map<String, List<BBoxInputConstraint>> {
        return HashMap<String, MutableList<BBoxInputConstraint>>()
    }

    override fun getJobInputs(
        literalInputs: Map<String, List<String>>,
        complexInputs: Map<String, List<ComplexInputConstraint>>,
        bboxInputs: Map<String, List<BBoxInputConstraint>>
    ): List<JobConstraints> {
        val latConstraints = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_LATITUDE, ArrayList())
        val lonConstraints = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_LONGITUDE, ArrayList())
        val magConstraints = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_MAGNITUDE, ArrayList())

        //order contains one triple of mag, lat, lon
        val literalInputValues = HashMap<String, String>()
        literalInputValues[WPS_PROCESS_INPUT_IDENTIFIER_LONGITUDE] = lonConstraints[0]
        literalInputValues[WPS_PROCESS_INPUT_IDENTIFIER_LATITUDE] = latConstraints[0]
        literalInputValues[WPS_PROCESS_INPUT_IDENTIFIER_MAGNITUDE] = magConstraints[0]

       return listOf(JobConstraints(literalInputValues,  HashMap<String, ComplexInputConstraint>(), HashMap<String, BBoxInputConstraint>()))
    }


    override fun getRequestedOutputs(): List<WPSOutputDefinition> {
        return listOf(
            WPSOutputDefinition(WPS_PROCESS_OUTPUT_IDENTIFIER_INUNDATION_SHAKEMAP, "application/xml", "http://earthquake.usgs.gov/eqcenter/shakemap", "UTF-8")
        )
    }

}