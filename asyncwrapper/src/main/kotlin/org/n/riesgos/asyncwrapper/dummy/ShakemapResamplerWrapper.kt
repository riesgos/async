package org.n.riesgos.asyncwrapper.dummy

import com.scurrilous.circe.Hash
import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.config.WPSOutputDefinition
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.BBoxInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.JobConstraints
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import java.util.*
import java.util.logging.Logger
import kotlin.collections.HashMap

class ShakemapResamplerWrapper(private val datamanagementRepo: DatamanagementRepo, wpsConfig : WPSConfiguration, publisher: PulsarPublisher) : AbstractWrapper(publisher, wpsConfig) {

    private val wpsURL = wpsConfig.wpsURL
    private val wpsShakemapResamplerProcessIdentifier = wpsConfig.process

    companion object {
        //input ids
        val WPS_PROCESS_INPUT_IDENTIFIER_SHAKEMAPRESAMPLER_SHAKEMAP_FILE = "intensity_file";
        val WPS_PROCESS_INPUT_IDENTIFIER_SHAKEMAPRESAMPLER_RANDOM_SEED = "random_seed";
        //output ids
        val WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKEMAPRESAMPLER_SHAKEMAP_FILE = "intensity_output_file";


        // Wrapper name is different from the wps process identifier, as it could
        // be that we use the same process for different tasks.
        val WRAPPER_NAME_SHAKEMAPRESAMPLER = "shakemapresampler"
        val LOGGER = Logger.getLogger("ShakemapResampler")
    }


    override fun datamanagementRepo(): DatamanagementRepo {
        return this.datamanagementRepo
    }

    override fun getWrapperName(): String {
        return WRAPPER_NAME_SHAKEMAPRESAMPLER
    }

    override fun getWpsIdentifier(): String {
        return wpsShakemapResamplerProcessIdentifier
    }

    override fun getWpsUrl(): String {
        return wpsURL
    }

    override fun getDefaultLiteralConstraints(): Map<String, List<String>> {
        return HashMap<String, List<String>>()
    }

    override fun getDefaultComplexConstraints(orderId: Long): Map<String, List<ComplexInputConstraint>> {
        return HashMap<String, List<ComplexInputConstraint>>()
    }

    override fun getDefaultBBoxConstraints(orderId: Long): Map<String, List<BBoxInputConstraint>> {
        return HashMap<String, List<BBoxInputConstraint>>()
    }

    override fun getJobInputs(
        literalInputs: Map<String, List<String>>,
        complexInputs: Map<String, List<ComplexInputConstraint>>,
        bboxInputs: Map<String, List<BBoxInputConstraint>>
    ): List<JobConstraints> {
        LOGGER.info("get inputs for $WRAPPER_NAME_SHAKEMAPRESAMPLER")

        val inputs = ArrayList<JobConstraints>()

        for (randomSeed in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_SHAKEMAPRESAMPLER_RANDOM_SEED, ArrayList())){
            for(shakemap in complexInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_SHAKEMAPRESAMPLER_SHAKEMAP_FILE, ArrayList())){
                val literalInputValues = HashMap<String, String>()
                val complexInputValues = HashMap<String, ComplexInputConstraint>()
                val bboxInputConstraints = HashMap<String, BBoxInputConstraint>()

                literalInputValues[WPS_PROCESS_INPUT_IDENTIFIER_SHAKEMAPRESAMPLER_RANDOM_SEED] = randomSeed
                complexInputValues[ShakygroundWrapper.WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_QUAKEML_FILE] = shakemap

                inputs.add(JobConstraints(literalInputValues, complexInputValues, bboxInputConstraints))
            }
        }

        return inputs
    }

    override fun getRequestedOutputs(): List<WPSOutputDefinition> {
        return listOf(
            WPSOutputDefinition(
                WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKEMAPRESAMPLER_SHAKEMAP_FILE,
                "text/xml",
                "http://earthquake.usgs.gov/eqcenter/shakemap",
                "UTF-8"
            )
        )
    }
}