package org.n.riesgos.asyncwrapper.dummy

import org.json.JSONObject
import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.config.WPSOutputDefinition
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.*
import org.n.riesgos.asyncwrapper.datamanagement.utils.getStringOrDefault
import org.n.riesgos.asyncwrapper.process.wps.InputMapper
import org.n.riesgos.asyncwrapper.process.wps.OutputMapper
import org.n.riesgos.asyncwrapper.process.wps.WPSClientService
import org.n.riesgos.asyncwrapper.process.wps.WPSProcess
import org.n.riesgos.asyncwrapper.pulsar.MessageParser
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import org.n52.geoprocessing.wps.client.model.execution.Data
import java.util.*
import java.util.logging.Logger

/**
 * This is an abstract wrapper. We are going to overwrite some of the abstract
 * functions in order to make it work with an concrete wps process.
 */
abstract class AbstractWrapper(val publisher : PulsarPublisher, val wpsConfiguration: WPSConfiguration) {

    /**
     * Some of the constants that we can reuse.
     */
    companion object {
        val WPS_JOB_STATUS_ACCEPTED = "Accepted"
        val WPS_JOB_STATUS_RUNNING = "Running"
        val WPS_JOB_STATUS_SUCCEEDED = "Succeeded"
        val WPS_JOB_STATUS_FAILED = "Failed"

        val LOGGER = Logger.getLogger("AbstractWrapper")
    }


    /**
     * Parse the contraints & run what is possible & necessary.
     */
    fun run (orderId: Long) {
        LOGGER.info("Got new order to handle with wrapper " + orderId.toString())
        val constraints = getOrderConstraints(orderId)

        LOGGER.info("Parsed constraints")

        when (constraints) {
            is JobIdConstraintResult -> return addJobIdToOrderAndSendSuccess(constraints.jobId, orderId)
            is OrderConstraintsResult -> return fillConstraintsAndRun(constraints, orderId)
            else -> {
                LOGGER.info("No valid constraints object")
                return
            }
        }

    }

    /**
     * Most of our methods to define the concrete wrapper.
     */
    /**
     * Access to our data management repo (where we store existing results).
     */
    abstract fun datamanagementRepo() : DatamanagementRepo

    /**
     * Name of the wrapper. Could be "shakyground" for example.
     * But could also be specific for some use of one service (eqdeus, tsdeus).
     */
    abstract fun getWrapperName(): String

    /**
     * The default literal constraints. (List of gmpes for example).
     */
    abstract fun getDefaultLiteralConstraints (): Map<String, List<String>>

    /**
     * The default constraints for complex outputs. Existing quakeML files for example.
     */
    abstract fun getDefaultComplexConstraints (orderId: Long): Map<String, List<ComplexInputConstraint>>

    abstract fun getDefaultBBoxConstraints (orderId: Long): Map<String, List<BBoxInputConstraint>>

    /**
     * Transformation of the inputs lists (with all of what we can do) to concrete job constraints (one concrete parametrization).
     */
    abstract fun getJobInputs (literalInputs: Map<String, List<String>>, complexInputs: Map<String, List<ComplexInputConstraint>>, bboxInputs: Map<String, List<BBoxInputConstraint>>): List<JobConstraints>

    /**
     * The wps identifier fo the process itself.
     */
    abstract fun getWpsIdentifier(): String

    /**
     * The wps url for the process itself.
     */
    abstract fun getWpsUrl(): String


    abstract fun getRequestedOutputs(): List<WPSOutputDefinition>

    /**
     * Parse the constraints.
     */
    private fun getOrderConstraints(orderId: Long): ParsedConstraintsResult ? {
        val jsonObject = datamanagementRepo().orderConstraints(orderId)
        if (jsonObject == null) {
            return null
        }
        val wrapperName = getWrapperName()
        if (!jsonObject.has(wrapperName)) {
            return OrderConstraintsResult(HashMap(), HashMap(), HashMap())
        }
        val wrapperRawConstraints = jsonObject.getJSONObject(wrapperName)

        if (wrapperRawConstraints.has("job_id")) {
            val jobId = wrapperRawConstraints.getLong("job_id")
            return JobIdConstraintResult(jobId)
        }

        val literalConstraints = HashMap<String, MutableList<String>>()
        val complexConstraints = HashMap<String, MutableList<ComplexInputConstraint>>()
        val bboxConstraints = HashMap<String, MutableList<BBoxInputConstraint>>()

        if (wrapperRawConstraints.has("literal_inputs")) {
            val literalInputConstraints = wrapperRawConstraints.getJSONObject("literal_inputs")
            for (key in literalInputConstraints.keySet()) {
                val constraintArray = literalInputConstraints.getJSONArray(key)
                for (constraintValue in constraintArray) {
                    val existingList = literalConstraints.getOrDefault(key, ArrayList())
                    existingList.add(constraintValue as String)
                    literalConstraints.put(key, existingList)
                    LOGGER.info("Added literal input constraint for " + wrapperName + " " + key + ": " + constraintValue.toString())
                }
            }
        }
        if (wrapperRawConstraints.has("complex_inputs")) {
            val complexInputConstraints = wrapperRawConstraints.getJSONObject("complex_inputs")
            for (key in complexInputConstraints.keySet()) {
                val constraintArray = complexInputConstraints.getJSONArray(key)
                for (constraintValue in constraintArray) {
                    val constraintObject = constraintValue as JSONObject
                    val existingList = complexConstraints.getOrDefault(key, ArrayList())
                    existingList.add(
                        ComplexInputConstraint(
                            constraintObject.getStringOrDefault("link", null),
                            constraintObject.getStringOrDefault("input_value", null),
                            constraintObject.getString("mime_type"),
                            constraintObject.getString("xmlschema"),
                            constraintObject.getString("encoding")
                        )
                    )
                    complexConstraints.put(key, existingList)
                    LOGGER.info("Added complex input constraint for " + wrapperName + " " + key)
                }
            }
        }
        if (wrapperRawConstraints.has("bbox_inputs")) {
            val bboxInputConstraints = wrapperRawConstraints.getJSONObject("bbox_inputs")
            for (key in bboxInputConstraints.keySet()) {
                val constraintArray = bboxInputConstraints.getJSONArray(key)
                for (constraintValue in constraintArray) {
                    val constraintObject = constraintValue as JSONObject
                    val existingList = bboxConstraints.getOrDefault(key, ArrayList())
                    existingList.add(
                            BBoxInputConstraint(
                                    constraintObject.getDouble("lower_corner_x"),
                                    constraintObject.getDouble("lower_corner_y"),
                                    constraintObject.getDouble("upper_corner_x"),
                                    constraintObject.getDouble("upper_corner_y"),
                                    constraintObject.getString("crs")
                            )
                    )
                    bboxConstraints.put(key, existingList)
                    LOGGER.info("Added bbox input constraint for " + wrapperName + " " + key)
                }
            }
        }

        return OrderConstraintsResult(literalConstraints, complexConstraints, bboxConstraints)
    }


    /**
     * If there is an existing job that should be reused, just add this job
     * to the current order (so that later services can find the output)
     * and send a success.
     */
    private fun addJobIdToOrderAndSendSuccess (jobId: Long, orderId: Long) {
        LOGGER.info("We found a jobId "+ jobId.toString())
        datamanagementRepo().addJobToOrder(jobId, orderId)
        LOGGER.info("We added it to the order")
        sendSuccess(orderId)
    }


    /**
     * Just send a success out.
     */
    private fun sendSuccess (orderId : Long) {
        val msgParser = MessageParser()
        val msg = msgParser.buildMessageForOrderId(orderId)

        LOGGER.info("Start sending success")
        publisher.publishSuccessMessage(msg)
        LOGGER.info("Finished sending success")

    }


    /**
     * Work with the constraints that we got & run the processes with our parametrizations.
     */
    private fun fillConstraintsAndRun(orderConstraints: OrderConstraintsResult, orderId: Long) {
        LOGGER.info("Define the literal constraints for the jobs")
        val filledLiteralConstraints = mergeConstraintsWithDefaults(orderConstraints.literalConstraints, getDefaultLiteralConstraints())
        LOGGER.info("Define the complex constraints for the jobs")
        val filledComplexConstraints = mergeConstraintsWithDefaults(orderConstraints.complexConstraints, getDefaultComplexConstraints(orderId))
        LOGGER.info("Define the bbox constraints for the jobs")
        val filledBBoxConstraints = mergeConstraintsWithDefaults(orderConstraints.bboxConstraints, getDefaultBBoxConstraints(orderId))

        var jobInputs: List<JobConstraints> = ArrayList<JobConstraints>()
        try {
            jobInputs = getJobInputs(filledLiteralConstraints, filledComplexConstraints, filledBBoxConstraints)
        } catch (e: Exception) {
            LOGGER.info("Problem on parameterization for concrete jobs")
            e.printStackTrace()
            throw e
        }
        var hasAtLeastOneRun = false
        for (jobInput in jobInputs) {
            hasAtLeastOneRun = true
            LOGGER.info("Process job")
            // TODO: Extract job id or filter for a process that is not failed.
            if (datamanagementRepo().hasAlreadyProcessed(getWpsIdentifier(), WPS_JOB_STATUS_SUCCEEDED, jobInput.complexConstraints, jobInput.literalConstraints, jobInput.bboxConstraints)) {
                LOGGER.info("Inputs already processed")
                sendSuccess(orderId)
            } else {
                runOneJob(jobInput, orderId)
            }
        }

        if (!hasAtLeastOneRun) {
            LOGGER.info("No job executed")
        }
    }

    /**
     * Run one job with one exact set of parametrization.
     */
    fun runOneJob (jobInput: JobConstraints, orderId: Long) {
        LOGGER.info("Lookup the process id")
        val processId = datamanagementRepo().findProcessIdOrInsert(getWpsUrl(), getWpsIdentifier())
        LOGGER.info("Got the procss id "+ processId.toString())
        val jobId = datamanagementRepo().createJob(processId, WPS_JOB_STATUS_ACCEPTED)
        LOGGER.info("Created one job")

        val literalInputs = ArrayList<LiteralInput>()
        for (inputKey in jobInput.literalConstraints.keys) {
            literalInputs.add(datamanagementRepo().insertLiteralInput(jobId, inputKey, jobInput.literalConstraints.get(inputKey)!!))
            LOGGER.info("Added literal input for "+ inputKey + ": " + jobInput.literalConstraints.get(inputKey))
        }
        val bboxInputs = ArrayList<BboxInput>()
        for (innerKey in jobInput.bboxConstraints.keys) {
            bboxInputs.add(datamanagementRepo().insertBboxInput(jobId, innerKey, jobInput.bboxConstraints.get(innerKey)!!))
            LOGGER.info("Added bbox input for " + innerKey)
        }

        val complexInputs = ArrayList<ComplexInput>()
        val complexOutputsAsInputs = ArrayList<ComplexOutputAsInput>()
        val complexInputsAsValues = ArrayList<ComplexInputAsValue>()
        for (inputKey in jobInput.complexConstraints.keys) {
            val inputValue = jobInput.complexConstraints.get(inputKey)!!


            if (inputValue.link != null) {
                val optionalComplexOutput = datamanagementRepo().findOptionalExistingComplexOutputToUseAsInput(inputValue)
                if (optionalComplexOutput != null ) {
                    complexOutputsAsInputs.add(datamanagementRepo().insertComplexOutputAsInput(jobId, optionalComplexOutput, inputKey))
                    LOGGER.info("Added complex input (as reference to existing output) for " + inputKey)
                } else {
                    // nothing found, insert as we got
                    complexInputs.add(datamanagementRepo().insertComplexInput(jobId, inputKey, inputValue))
                    LOGGER.info("Added complex input (as reference link) for " + inputKey)
                }

            } else {
                // input as value, insert as we got it
                complexInputsAsValues.add(datamanagementRepo().insertComplexInputAsValue(jobId, inputKey, inputValue))
                LOGGER.info("Added complex input (as value) for " + inputKey)
            }
        }

        // add the reference of the job to that order
        datamanagementRepo().addJobToOrder(jobId, orderId)
        LOGGER.info("Added job to order")

        datamanagementRepo().updateJobStatus(jobId, WPS_JOB_STATUS_RUNNING) // maybe not needed to set it to running

        LOGGER.info("Start mapping to wps inputs")
        val wpsInputMapper = InputMapper(getWpsIdentifier())
        val wpsInputs = wpsInputMapper.mapInputs(complexInputs, complexInputsAsValues, complexOutputsAsInputs, literalInputs, bboxInputs)
        // TODO: Extract the version from the implementations themselves
        try {
            val wpsClientService = WPSClientService(wpsConfiguration)
            val wpsProcess = WPSProcess(wpsClientService.establishWPSConnection(), getWpsUrl(), getWpsIdentifier(), "2.0.0", getRequestedOutputs())
            LOGGER.info("Start calling the wps itself")
            val wpsOutputs = wpsProcess.runProcess(wpsInputs)
            LOGGER.info("Finished calling the wps itself")
            val wpsOutputMapper = OutputMapper(jobId, wpsOutputs)
            val complexOutputs = wpsOutputMapper.mapOutputs()
            LOGGER.info("Finished mapping the outputs")


            for (output in complexOutputs) {

                datamanagementRepo().insertComplexOutput(
                        output.jobId,
                        output.wpsIdentifier,
                        output.link,
                        output.mimeType,
                        output.xmlschema,
                        output.encoding
                )
                LOGGER.info("Stored output for " + output.wpsIdentifier)
            }

            datamanagementRepo().updateJobStatus(jobId, WPS_JOB_STATUS_SUCCEEDED)
            LOGGER.info("Job succeeded")

            sendSuccess(orderId)
        } catch (e: Exception) {
            LOGGER.info("WPS call failed")
            e.printStackTrace()
            datamanagementRepo().updateJobStatus(jobId, WPS_JOB_STATUS_FAILED)
            // => without the re-raise we would be able to run the loop & don't
            // have to stop when one call has an exception from the WPS itself.
        }
    }

    /**
     * Helper method to merge maps of constraints.
     */
    private fun <T> mergeConstraintsWithDefaults (orderConstraints: Map<String, List<T>>, defaultConstraints: Map<String, List<T>>): Map<String, List<T>> {
        val filledLiteralConstraints = HashMap<String, List<T>>()
        for (defaultConstraintKey in defaultConstraints.keys) {
            filledLiteralConstraints.put(defaultConstraintKey, defaultConstraints.get(defaultConstraintKey)!!)
        }
        for (orderConstraintKey in orderConstraints.keys) {
            filledLiteralConstraints.put(orderConstraintKey, orderConstraints.get(orderConstraintKey)!!)
        }
        return filledLiteralConstraints
    }
}

