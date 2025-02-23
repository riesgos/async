package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.config.FilestorageConfig
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

class QuakeledgerWrapper (val datamanagementRepo: DatamanagementRepo, wpsConfig: WPSConfiguration,
                          publisher: PulsarPublisher, filestorageConfig: FilestorageConfig
): AbstractWrapper(publisher, wpsConfig, filestorageConfig) {

    private val wpsURL = wpsConfig.wpsURL
    private val wpsProcessIdentifier = wpsConfig.process
    companion object {
        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_INPUTBOUDINGBOX = "input-boundingbox"
        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_MMIN = "mmin"
        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_MMAX = "mmax"
        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ZMIN = "zmin"
        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ZMAX = "zmax"
        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_P = "p"
        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ETYPE = "etype"
        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_TLON = "tlon"
        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_TLAT = "tlat"

        val WPS_PROCESS_OUTPUT_IDENTIFIER_QUAKELEDGER_QUAKEML = "selectedRows"

        val WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ETYPE_OPTIONS = Arrays.asList("observed","deaggregation","stochastic","expert")


        // Wrapper name is different from the wps process identifier, as it could
        // be that we use the same process for different tasks.
        // Exp. Damage computation for earthquake damage (eqdeus) vs tsunami damage
        // (tsdeus).
        val WRAPPER_NAME_QUAKELEDGER= "quakeledger"
    }

    override fun datamanagementRepo(): DatamanagementRepo {
        return this.datamanagementRepo
    }

    override fun getWrapperName(): String {
        return WRAPPER_NAME_QUAKELEDGER
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

    override fun getDefaultLiteralConstraints(): Map<String, List<String>> {
        return HashMap<String, List<String>>().with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ETYPE, WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ETYPE_OPTIONS)
    }

    override fun getDefaultComplexConstraints(orderId: Long): Map<String, MutableList<ComplexInputConstraint>> {
        return HashMap<String, MutableList<ComplexInputConstraint>>()
    }


    override fun getDefaultBBoxConstraints (orderId: Long): Map<String, List<BBoxInputConstraint>> {
        return HashMap<String, MutableList<BBoxInputConstraint>>()
    }

    override fun getJobInputs(literalInputs: Map<String, List<String>>, complexInputs: Map<String, List<ComplexInputConstraint>>, bboxInputs: Map<String, List<BBoxInputConstraint>>): List<JobConstraints> {
        val result = ArrayList<JobConstraints>()

        val zminConstraints = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ZMIN, ArrayList())
        val zmaxConstraints = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ZMAX, ArrayList())
        val minLenthZConstraints = Math.min(zminConstraints.size, zmaxConstraints.size)

        val mminConstraints = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_MMIN, ArrayList())
        val mmaxConstraints = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_MMAX, ArrayList())
        val minLengthMConstraints = Math.min(mminConstraints.size, mmaxConstraints.size)

        val tlatConstraints = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_TLAT, ArrayList())
        val tlonConstraints = literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_TLON, ArrayList())
        val minLengthTConstraints = Math.min(tlatConstraints.size, tlonConstraints.size)


        for (iZConstraints in 0 until minLenthZConstraints) {
            val zMinConstraint = zminConstraints.get(iZConstraints)
            val zMaxConstraint = zmaxConstraints.get(iZConstraints)

            for (iMConstraints in 0 until minLengthMConstraints) {
                val mMinConstraint = mminConstraints.get(iMConstraints)
                val mMaxConstraint = mmaxConstraints.get(iMConstraints)

                for (iTConstraints in 0 until minLengthTConstraints) {
                    val tlatConstraint = tlatConstraints.get(iTConstraints)
                    val tlonConstraint = tlonConstraints.get(iTConstraints)

                    for (bboxConstraint in bboxInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_INPUTBOUDINGBOX, ArrayList())) {
                        for (pConstraint in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_P, ArrayList())) {
                            for (eTypeConstraint in literalInputs.getOrDefault(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ETYPE, ArrayList())) {
                                val literalInputValues = HashMap<String, String>()
                                        .with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ETYPE, eTypeConstraint)
                                        .with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_P, pConstraint)
                                        .with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_TLON, tlonConstraint)
                                        .with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_TLAT, tlatConstraint)
                                        .with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_MMAX, mMaxConstraint)
                                        .with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_MMIN, mMinConstraint)
                                        .with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ZMAX, zMaxConstraint)
                                        .with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_ZMIN, zMinConstraint)

                                val complexInputValues = HashMap<String, ComplexInputConstraint>()
                                val bboxInputValues = HashMap<String, BBoxInputConstraint>()
                                        .with(WPS_PROCESS_INPUT_IDENTIFIER_QUAKELEDGER_INPUTBOUDINGBOX, bboxConstraint)

                                result.add(JobConstraints(literalInputValues, complexInputValues, bboxInputValues))
                            }
                        }
                    }
                }
            }
        }

        return result
    }

    override fun getRequestedOutputs(): List<WPSOutputDefinition> {
        return Arrays.asList(
                WPSOutputDefinition(WPS_PROCESS_OUTPUT_IDENTIFIER_QUAKELEDGER_QUAKEML, "text/xml", "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd", "UTF-8"),
                WPSOutputDefinition(WPS_PROCESS_OUTPUT_IDENTIFIER_QUAKELEDGER_QUAKEML, "application/vnd.geo+json", "", "UTF-8")
        )
    }
}

fun <K, V> HashMap<K, V>.with(key: K,value: V): HashMap<K, V> {
    this.put(key, value)
    return this
}