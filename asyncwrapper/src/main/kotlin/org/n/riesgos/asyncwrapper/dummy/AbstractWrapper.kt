package org.n.riesgos.asyncwrapper.dummy

import org.json.JSONObject
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.*
import org.n.riesgos.asyncwrapper.datamanagement.utils.getStringOrDefault
import org.n52.geoprocessing.wps.client.model.execution.Data
import java.util.*

/**
 * This is an abstract wrapper. We are going to overwrite some of the abstract
 * functions in order to make it work with an concrete wps process.
 */
abstract class AbstractWrapper {

    /**
     * Some of the constants that we can reuse.
     */
    companion object {
        val WPS_JOB_STATUS_ACCEPTED = "Accepted"
        val WPS_JOB_STATUS_RUNNING = "Running"
        val WPS_JOB_STATUS_SUCCEEDED = "Succeeded"
    }


    /**
     * Parse the contraints & run what is possible & necessary.
     */
    fun run (orderId: Long) {
        val constraints = getOrderConstraints(orderId)

        when (constraints) {
            is JobIdConstraintResult -> return addJobIdToOrderAndSendSuccess(constraints.jobId, orderId)
            is OrderConstraintsResult -> return fillConstraintsAndRun(constraints, orderId)
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

    /**
     * Transformation of the inputs lists (with all of what we can do) to concrete job constraints (one concrete parametrization).
     */
    abstract fun getJobInputs (literalInputs: Map<String, List<String>>, complexInputs: Map<String, List<ComplexInputConstraint>>): List<JobConstraints>

    /**
     * The wps identifier fo the process itself.
     */
    abstract fun getWpsIdentifier(): String

    /**
     * The wps url for the process itself.
     */
    abstract fun getWpsUrl(): String

    /**
     * Method to make the wps call itself.
     * TODO Replace with generic version. (Currently mocked)
     */
    abstract fun runWpsItself (): List<Data>

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
            return null
        }
        val wrapperRawConstraints = jsonObject.getJSONObject(wrapperName)

        if (wrapperRawConstraints.has("job_id")) {
            val jobId = wrapperRawConstraints.getLong("job_id")
            return JobIdConstraintResult(jobId)
        }

        val literalConstraints = HashMap<String, MutableList<String>>()
        val complexConstraints = HashMap<String, MutableList<ComplexInputConstraint>>()

        if (wrapperRawConstraints.has("literal_inputs")) {
            val literalInputConstraints = wrapperRawConstraints.getJSONObject("literal_inputs")
            for (key in literalInputConstraints.keySet()) {
                val constraintArray = literalInputConstraints.getJSONArray(key)
                for (constraintValue in constraintArray) {
                    literalConstraints.getOrDefault(key, ArrayList()).add(constraintValue as String)
                }
            }
        }
        if (wrapperRawConstraints.has("complex_inputs")) {
            val complexInputConstraints = wrapperRawConstraints.getJSONObject("complex_inputs")
            for (key in complexInputConstraints.keySet()) {
                val constraintArray = complexInputConstraints.getJSONArray(key)
                for (constraintValue in constraintArray) {
                    val constraintObject = constraintValue as JSONObject
                    complexConstraints.getOrDefault(key, ArrayList()).add(
                            ComplexInputConstraint(
                                constraintObject.getStringOrDefault("link", null),
                                constraintObject.getStringOrDefault("inputValue", null),
                                constraintObject.getString("mime_type"),
                                constraintObject.getString("xmlschema"),
                                constraintObject.getString("encoding")
                            )
                        )
                }
            }

        }
        return OrderConstraintsResult(literalConstraints, complexConstraints)
    }


    /**
     * If there is an existing job that should be reused, just add this job
     * to the current order (so that later services can find the output)
     * and send a success.
     */
    private fun addJobIdToOrderAndSendSuccess (jobId: Long, orderId: Long) {
        datamanagementRepo().addJobToOrder(jobId, orderId)
        sendSuccess()
    }


    /**
     * Just send a success out.
     */
    private fun sendSuccess () {
        // TODO
    }


    /**
     * Work with the constraints that we got & run the processes with our parametrizations.
     */
    private fun fillConstraintsAndRun(orderConstraints: OrderConstraintsResult, orderId: Long) {
        val filledLiteralConstraints = mergeConstraintsWithDefaults(orderConstraints.literalConstraints, getDefaultLiteralConstraints())
        val filledComplexConstraints = mergeConstraintsWithDefaults(orderConstraints.complexConstraints, getDefaultComplexConstraints(orderId))

        for (jobInput in getJobInputs(filledLiteralConstraints, filledComplexConstraints)) {
            if (datamanagementRepo().hasAlreadyProcessed(getWpsIdentifier(), jobInput.complexConstraints, jobInput.literalConstraints)) {
                sendSuccess()
            } else {
                runOneJob(jobInput, orderId)
            }
        }
    }

    /**
     * Run one job with one exact set of parametrization.
     */
    fun runOneJob (jobInput: JobConstraints, orderId: Long) {
        val processId = datamanagementRepo().findProcessIdOrInsert(getWpsUrl(), getWpsIdentifier())
        val jobId = datamanagementRepo().createJob(processId, WPS_JOB_STATUS_ACCEPTED)

        for (inputKey in jobInput.literalConstraints.keys) {
            datamanagementRepo().insertLiteralInput(jobId, inputKey, jobInput.literalConstraints.get(inputKey)!!)
        }

        for (inputKey in jobInput.complexConstraints.keys) {
            val inputValue = jobInput.complexConstraints.get(inputKey)!!


            if (inputValue.link != null) {
                val optionalComplexOutputId = datamanagementRepo().findOptionalExistingComplexOutputToUseAsInput(inputValue)
                if (optionalComplexOutputId != null ) {
                    datamanagementRepo().insertComplexOutputAsInput(jobId, optionalComplexOutputId, inputKey)
                } else {
                    // nothing found, insert as we got
                    datamanagementRepo().insertComplexInput(jobId, inputKey, inputValue)
                }

            } else {
                // input as value, insert as we got it
                datamanagementRepo().insertComplexInputAsValue(jobId, inputKey, inputValue)
            }

            // add the reference of the job to that order
            datamanagementRepo().addJobToOrder(jobId, orderId)

            datamanagementRepo().updateJobStatus(jobId, WPS_JOB_STATUS_RUNNING) // maybe not needed to set it to running
            val outputs = runWpsItself()

            datamanagementRepo().updateJobStatus(jobId, WPS_JOB_STATUS_SUCCEEDED)
            // TODO: What if failed?
            for (output in outputs) {
                val complexReferenceData = output.asComplexReferenceData()
                datamanagementRepo().insertComplexOutput(
                        jobId,
                        complexReferenceData.id,
                        complexReferenceData.reference.href.toString(),
                        complexReferenceData.format.mimeType,
                        complexReferenceData.format.schema,
                        complexReferenceData.format.encoding
                )
            }
            sendSuccess()
        }

    }

    /**
     * Helper method to merge maps of constraints.
     */
    private fun <T> mergeConstraintsWithDefaults (orderConstraints: Map<String, List<T>>, defaultConstraints: Map<String, List<T>>): Map<String, List<T>> {
        val literalInputKeys = HashSet<String>(orderConstraints.keys)
        literalInputKeys.addAll(defaultConstraints.keys)



        val filledLiteralConstraints = HashMap<String, List<T>>()
        for (literalInputKey in literalInputKeys) {
            filledLiteralConstraints.put(literalInputKey, orderConstraints.getOrDefault(literalInputKey, defaultConstraints.getOrDefault(literalInputKey, ArrayList())))
        }
        return filledLiteralConstraints
    }
}

