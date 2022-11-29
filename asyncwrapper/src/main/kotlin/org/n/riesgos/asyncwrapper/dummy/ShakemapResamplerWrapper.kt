package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.config.WPSOutputDefinition
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.BBoxInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.JobConstraints
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import java.util.logging.Logger

class ShakemapResamplerWrapper(private val datamanagementRepo: DatamanagementRepo, wpsConfig : WPSConfiguration, publisher: PulsarPublisher) : AbstractWrapper(publisher, wpsConfig) {

    private val wpsURL = wpsConfig.wpsURL
    private val wpsShakemapResamplerProcessIdentifier = wpsConfig.process

    companion object {
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
        TODO("Not yet implemented")
    }

    override fun getDefaultComplexConstraints(orderId: Long): Map<String, List<ComplexInputConstraint>> {
        TODO("Not yet implemented")
    }

    override fun getDefaultBBoxConstraints(orderId: Long): Map<String, List<BBoxInputConstraint>> {
        TODO("Not yet implemented")
    }

    override fun getJobInputs(
        literalInputs: Map<String, List<String>>,
        complexInputs: Map<String, List<ComplexInputConstraint>>,
        bboxInputs: Map<String, List<BBoxInputConstraint>>
    ): List<JobConstraints> {
        TODO("Not yet implemented")
    }

    override fun getRequestedOutputs(): List<WPSOutputDefinition> {
        TODO("Not yet implemented")
    }
}