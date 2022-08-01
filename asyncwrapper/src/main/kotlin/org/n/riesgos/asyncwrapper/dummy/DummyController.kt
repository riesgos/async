package org.n.riesgos.asyncwrapper.dummy

import org.json.JSONObject
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.utils.NamedInput
import org.n52.geoprocessing.wps.client.model.Format
import org.n52.geoprocessing.wps.client.model.execution.Data
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class DummyController (val jdbcTemplate: JdbcTemplate, val datamanagementRepo: DatamanagementRepo) {

    companion object {
        val WPS_URL = "https://rz-vm140.gfz-potsdam.de/wps/WebProcessingService"
        val WPS_JOB_STATUS_ACCEPTED = "Accepted"
        val WPS_JOB_STATUS_RUNNING = "Running"
        val WPS_JOB_STATUS_SUCCEEDED = "Succeeded"

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



    @GetMapping("/run/shakyground/{orderId}")
    fun runShakyground (@PathVariable(value="orderId") orderId: Long): String {

        /**
         * Overall process:
         * - extract the constraints
         * - if we have an existing job id => fine (not much more to do)
         * - extract constraints for our literal inputs
         * - check what we can extract as information about complex inputs
         *   (either as constraints or by using the order id)
         * - for each possible combination of the constraints, run the wps or
         *   reuse an existing result
         * - if we need to run the wps process itself, we save what inputs
         *   we used, and what outputs we got.
         * - every time we reuse a result from an earlier job, we add an
         *   relationship of this job to our order, so that later
         *   processes can find their input data when working with the
         *   same order.
         */

        val jsonObject = datamanagementRepo.orderConstraints(orderId)
        if (jsonObject == null) {
            return "no order found"
        }

        /*
        The constraints could look like this:

        {"shakyground": {"complex_inputs": {"quakeMLFile": [{"link": "https://bla", "encoding": "UTF-8", "mime_type": "application/json", "xmlschema": ""}]}, "literal_inputs": {"gmpe": ["Abrahamson", "Montalval"], "vsgrid": ["usgs", "micro"]}}}

         */

        if (!jsonObject.has(WRAPPER_NAME_SHAKYGROUND)) {
            // We don't have constraints at all.
            return "no order constraints found for the process"
        }

        val wrapperConstraints = jsonObject.getJSONObject(WRAPPER_NAME_SHAKYGROUND)

        if (wrapperConstraints.has("job_id")) {
            val jobId = wrapperConstraints.getLong("job_id")

            // now we have the job id
            // all that we want to do now, is that we associate our new
            // order with the old job

            if (! datamanagementRepo.hasJob(jobId)) {
                return "job does not exists"
            }

            datamanagementRepo.addJobToOrder(jobId, orderId)

            return "success"
        }

        // TODO: Use loop for the extraction of the literal inputs
        // extract the values from the constraints
        var gmpeConstraints = WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE_OPTIONS
        var vsgridConstraints = WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID_OPTIONS

        if (wrapperConstraints.has("literal_inputs")) {
            val literalInputConstraints = wrapperConstraints.getJSONObject("literal_inputs")
            if (literalInputConstraints.has(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE)) {
                gmpeConstraints = ArrayList<String>()
                val gmpeConstraintsArray = literalInputConstraints.getJSONArray(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE)
                for (gmpeConstraintObject in gmpeConstraintsArray) {
                    gmpeConstraints.add(gmpeConstraintObject as String)
                }
            }
            if (literalInputConstraints.has(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID)) {
                vsgridConstraints = ArrayList<String>()
                val vsGridConstaintsArray = literalInputConstraints.getJSONArray(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID)
                for (vsGridConstraintObject in vsGridConstaintsArray) {
                    vsgridConstraints.add(vsGridConstraintObject as String)
                }
            }
        }


        // Ok now we have the literal constraints.
        // But we still want to check for a constraint for our quakeml file
        // TODO: Use loop for the extraction of the complex inputs
        var quakeMlConstraints = ArrayList<ComplexInputConstraint>()

        if (wrapperConstraints.has("complex_inputs")) {
            val complexInputConstraints = wrapperConstraints.getJSONObject("complex_inputs")
            if (complexInputConstraints.has(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_QUAKEML_FILE)) {
                quakeMlConstraints = ArrayList<ComplexInputConstraint>()
                val quakeMlFileContraintsArray = complexInputConstraints.getJSONArray(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_QUAKEML_FILE)
                for (complexInputConstraintRawObject in quakeMlFileContraintsArray) {
                    val complexInputConstraintObject = complexInputConstraintRawObject as JSONObject
                    quakeMlConstraints.add(
                            ComplexInputConstraint(
                                    complexInputConstraintObject.getString("link"), // or null
                                    complexInputConstraintObject.getString("input_value"), // or null
                                    complexInputConstraintObject.getString("mime_type"),
                                    complexInputConstraintObject.getString("xmlschema"), // or empty
                                    complexInputConstraintObject.getString("encoding")
                            )
                    )
                }
            }
        }

        if (quakeMlConstraints.isEmpty()) {
            // now we need to search in the database for inputs that we could use
            // what kind of quakeml inputs does we have already for our order id?
            val existingQuakeMLOutputs = datamanagementRepo.complexOutputs(orderId, WPS_PROCESS_IDENTIFIER_QUAKELEDGER, WPS_PROCESS_OUTPUT_IDENTIFIER_QUAKELEDGER_QUAKEML)
            for (existingQuakeMLOutput in existingQuakeMLOutputs) {
                quakeMlConstraints.add(ComplexInputConstraint(
                        existingQuakeMLOutput.link,
                        null,
                        existingQuakeMLOutput.mimeType,
                        existingQuakeMLOutput.xmlschema,
                        existingQuakeMLOutput.encoding
                ))
            }
        }

        // Add some information about which input data could be found as another processes output data
        // so that we can add relationships that we outputs based on the existing input.
        // However, here we are not bound to the order id, we can reuse any existing product.
        val lookupForExistingOutputsAsInputs = HashMap<String, LookupForExistingOutputs>()
        lookupForExistingOutputsAsInputs.put(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_QUAKEML_FILE, LookupForExistingOutputs(WPS_PROCESS_IDENTIFIER_QUAKELEDGER, WPS_PROCESS_OUTPUT_IDENTIFIER_QUAKELEDGER_QUAKEML))


        // now we have all the constraints.
        // now it the point to make all possible combinations out of it.
        for (gmpeConstraint in gmpeConstraints) {
            for (vsgridConstraint in vsgridConstraints) {
                for (quakeMLConstraint in quakeMlConstraints) {

                    // if one of the lists is empty, we are not going to process anything

                    val namedLiteralInputs =  Arrays.asList(
                            NamedInput(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_GMPE, gmpeConstraint),
                            NamedInput(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_VSGRID, vsgridConstraint)
                    )

                    val namedComplexInputs = Arrays.asList(
                            NamedInput(WPS_PROCESS_INPUT_IDENTIFIER_SHAKYGROUND_QUAKEML_FILE, quakeMLConstraint)
                    )

                    //
                    // test if we already used this combo for one job.
                    // if so, just send the success & test the next
                    // if not, start the processing

                    // TODO: Synchronize? Maybe needed if we run multiple threads with the same request?

                    if (datamanagementRepo.hasAlreadyProcessed(
                                    WPS_PROCESS_IDENTIFIER_SHAKYGROUND,
                                    namedComplexInputs,
                                    namedLiteralInputs
                            )
                    ) {
                        // send success
                        // no need to recalculate it
                    } else {
                        // now we have all the data that we need
                        // and we now that we need to process it ourselves.
                        // We have to

                        // Create the job
                        val processId = datamanagementRepo.findProcessIdOrInsert(WPS_URL, WPS_PROCESS_IDENTIFIER_SHAKYGROUND)
                        val jobId = datamanagementRepo.createJob(processId, WPS_JOB_STATUS_ACCEPTED)


                        // insert the inputs in the db
                        for (input in namedLiteralInputs) {
                            datamanagementRepo.insertLiteralInput(jobId, input.name, input.input)
                        }
                        for (input in namedComplexInputs) {
                            if (input.input.link != null) {
                                var optionalComplexOutputId: Long? = null
                                if (lookupForExistingOutputsAsInputs.containsKey(input.name)) {
                                    val lookup = lookupForExistingOutputsAsInputs.get(input.name)
                                    optionalComplexOutputId = datamanagementRepo.findOptionalExistingComplexOutputToUseAsInput(
                                            lookup!!.processWpsIdentifier,
                                            lookup.outputWpsIndentifier,
                                            input.input
                                    )
                                }
                                if (optionalComplexOutputId != null ) {
                                    datamanagementRepo.insertComplexOutputAsInput(jobId, optionalComplexOutputId, input.name)
                                } else {
                                    // nothing fould, insert as we got
                                    datamanagementRepo.insertComplexInput(jobId, input.name, input.input)
                                }

                            } else {
                                // input as value, insert as we got it
                                datamanagementRepo.insertComplexInputAsValue(jobId, input.name, input.input)
                            }
                        }

                        // add the reference of the job to that order
                        datamanagementRepo.addJobToOrder(jobId, orderId)




                        // TODO Run the wps client with our input data (we fake it here)
                        datamanagementRepo.updateJobStatus(jobId, WPS_JOB_STATUS_RUNNING) // maybe not needed to set it to running
                        //  Wait the process to end
                        val outputs = Arrays.asList(
                            createFakeData(WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKYGROUND_SHAKEMAP_FILE, "text/xml", "http://earthquake.usgs.gov/eqcenter/shakemap", "UTF-8", "https://somewhere"),
                            createFakeData(WPS_PROCESS_OUTPUT_IDENTIFIER_SHAKYGROUND_SHAKEMAP_FILE, "appliation/WMS", "", "UTF-8", "https://somewhere/else")
                        )
                        datamanagementRepo.updateJobStatus(jobId, WPS_JOB_STATUS_SUCCEEDED)
                        // TODO: What if failed?
                        for (output in outputs) {
                            val complexReferenceData = output.asComplexReferenceData()
                            datamanagementRepo.insertComplexOutput(
                                    jobId,
                                    complexReferenceData.id,
                                    complexReferenceData.reference.href.toString(),
                                    complexReferenceData.format.mimeType,
                                    complexReferenceData.format.schema,
                                    complexReferenceData.format.encoding
                            )
                        }
                        // TODO send a success
                    }
                }
            }
        }


        return gmpeConstraints.toString()
    }

    // Some other helper endpoints to inspect the content & to make sure that
    // my repo & service methods are working properly.
    @GetMapping("/ordersConstraints/{orderId}")
    fun showOrderConstraints (@PathVariable(value="orderId") orderId: Long): String {
        val jsonObject = datamanagementRepo.orderConstraints(orderId)
        if (jsonObject == null) {
            return "not found"
        }
        return jsonObject.toString()
    }
}


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

data class LookupForExistingOutputs (val processWpsIdentifier: String, val outputWpsIndentifier: String)
