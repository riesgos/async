package org.n.riesgos.asyncwrapper.datamanagement

import org.json.JSONObject
import org.n.riesgos.asyncwrapper.datamanagement.mapper.*
import org.n.riesgos.asyncwrapper.datamanagement.models.*
import org.n.riesgos.asyncwrapper.datamanagement.repos.*
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Statement
import java.util.*
import java.util.stream.Collectors


@Component
class DatamanagementRepo (
        val jdbcTemplate: JdbcTemplate,
        val literalInputRepo: LiteralInputRepo,
        val complexInputRepo: ComplexInputRepo,
        val complexOutputAsInputRepo: ComplexOutputAsInputRepo,
        val complexInputAsValueRepo: ComplexInputAsValueRepo,
        val bboxInputRepo: BboxInputRepo,
        val orderJobRefRepo: OrderJobRefRepo,
        val complexOutputRepo: ComplexOutputRepo,
        val orderRepo: OrderRepo,
        val storedLinkRepo: StoredLinkRepo
) {
    /**
     * Extracts the constraints of the order as json object.
     */
    fun orderConstraints(orderId: Long): JSONObject? {
        val order = orderRepo.getOptionalById(orderId)
        if (order == null) {
            return order
        }
        return order.orderConstraints
    }

    /**
     * Return true if we have an job in the database for the job id.
     */
    fun hasJob(jobId: Long) : Boolean {
        val sql = """
            select id
            from jobs
            where id=?
        """.trimIndent()
        try {
            jdbcTemplate.queryForObject(sql, Int::class.javaObjectType, jobId)
            return true;
        } catch (e: EmptyResultDataAccessException) {
            return false
        }
    }

    /**
     * Add an relationship between the job and the order.
     *
     * Later processes can look up for the current order.
     */
    fun addJobToOrder(jobId: Long, orderId: Long) {
        orderJobRefRepo.persist(OrderJobRef(null, orderId, jobId))
    }


    fun findComplexOutputsByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType (orderId: Long, processWpsIdentifier: String, outputWpsIdentifier: String, mimeType: String): List<ComplexOutput> {
        return complexOutputRepo.findByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(orderId, processWpsIdentifier, outputWpsIdentifier, mimeType)
    }
    /**
     * Return the list of complex outputs for a given order, a wps process identifier (for example shakyground)
     * and an wps output identifier (for example shakemap).
     */
    fun complexOutputs(orderId: Long, processWpsIdentifier: String, outputWpsIdentifier: String): List<ComplexOutput> {
        val sql = """
            select distinct complex_outputs.*
            from complex_outputs
            join jobs on jobs.id = complex_outputs.job_id
            join order_job_refs on order_job_refs.job_id = jobs.id
            join processes on processes.id = jobs.process_id
            where order_job_refs.order_id = ?
            and processes.wps_identifier = ?
            and complex_outputs.wps_identifier = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, ComplexOutputRowMapper(), orderId, processWpsIdentifier, outputWpsIdentifier)
    }

    /**
     * Return a job id if the process with the exact input parameters
     * already has an job id in the database.
     *
     * Considers complex inputs (ref or value, or even reused outputs),
     * literal inputs, and bbox inputs.
     */
    // TODO maybe this should be synchronized (but how to do that in Kotlin?)
    fun jobIdHasAlreadyProcessed(
            processIdentifier: String,
            jobStatus: String,
            complexInputs: Map<String, ComplexInputConstraint>,
            literalInputs: Map<String, String>,
            bboxInputs: Map<String, BBoxInputConstraint>
    ): Optional<Long> {

        val jobIdSet = HashSet<Long>()
        var jobIdSetNotSetYet = true
        for (complexInputKey in complexInputs.keys) {
            val complexInput = complexInputs.get(complexInputKey) as ComplexInputConstraint
            val searchForThisComplexInput = complexInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding(
                    processIdentifier,
                    jobStatus,
                    complexInputKey,
                    complexInput.link,
                    complexInput.mimeType,
                    complexInput.xmlschema,
                    complexInput.encoding
            )

            val searchForThisComplexOutputAsInput = complexOutputAsInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding(
                    processIdentifier,
                    jobStatus,
                    complexInputKey,
                    complexInput.link,
                    complexInput.mimeType,
                    complexInput.xmlschema,
                    complexInput.encoding
                    )

            val searchForThisComplexInputAsValue = complexInputAsValueRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierInputValueMimetypeXmlSchemaAndEncoding(
                    processIdentifier,
                    jobStatus,
                    complexInputKey,
                    complexInput.inputValue,
                    complexInput.mimeType,
                    complexInput.xmlschema,
                    complexInput.encoding
            )
            // if we all of them are empty, we never used that complex input
            // so there is no way a job has already processed those
            if (searchForThisComplexInput.isEmpty() && searchForThisComplexInputAsValue.isEmpty() && searchForThisComplexOutputAsInput.isEmpty()) {
                return Optional.empty()
            }
            // If we have outputs, we want to extract the job ids, so that
            // we can check if we have a common job with the other inputs.
            val referenceJobIds = searchForThisComplexInput.stream().map({ x -> x.jobId}).distinct().collect(Collectors.toSet())
            val referenceWithExistingOutputsJobIds =  searchForThisComplexOutputAsInput.stream().map({ x -> x.jobId}).distinct().collect(Collectors.toSet())
            val valueJobIds = searchForThisComplexInputAsValue.stream().map({ x -> x.jobId}).distinct().collect(Collectors.toSet())
            // for the complex inputs we don't want to differ, either one is as good as the other
            referenceJobIds.addAll(referenceWithExistingOutputsJobIds)
            referenceJobIds.addAll(valueJobIds)
            if (jobIdSetNotSetYet) {
                jobIdSet.addAll(referenceJobIds)
                jobIdSetNotSetYet = false
            } else {
                jobIdSet.retainAll(referenceJobIds)
            }

            // if we realized we don't have a common base set with the other inputs
            // there is no way that this exact parameter set was already executed.
            if (jobIdSet.isEmpty()) {
                return Optional.empty()
            }
        }
        // same question for the literal inputs
        for (literalInputKey in literalInputs.keys) {
            val literalInput = literalInputs.get(literalInputKey)
            val searchForThisLiteralInput = literalInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierAndValue(processIdentifier, jobStatus, literalInputKey, literalInput!!)

            if (searchForThisLiteralInput.isEmpty()) {
                return Optional.empty()
            }
            val literalInputJobIds = searchForThisLiteralInput.stream().map({ x -> x.jobId}).distinct().collect(Collectors.toSet())
            if (jobIdSetNotSetYet) {
                jobIdSet.addAll(literalInputJobIds)
                jobIdSetNotSetYet = false
            } else {
                jobIdSet.retainAll(literalInputJobIds)
            }
            if (jobIdSet.isEmpty()) {
                return Optional.empty()
            }
        }
        // same for the bbox inputs
        for (bboxInputKey in bboxInputs.keys) {
            val bboxInput = bboxInputs.get(bboxInputKey)
            val searchForThisBboxInput = bboxInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierCornersAndCrs(
                    processIdentifier,
                    jobStatus,
                    bboxInputKey,
                    bboxInput!!.lowerCornerY,
                    bboxInput.lowerCornerY,
                    bboxInput.upperCornerY,
                    bboxInput.upperCornerY,
                    bboxInput.crs
            )
            if (searchForThisBboxInput.isEmpty()) {
                return Optional.empty()
            }
            val literalInputJobIds = searchForThisBboxInput.stream().map { x -> x.jobId }.distinct().collect(Collectors.toSet())
            if (jobIdSetNotSetYet) {
                jobIdSet.addAll(literalInputJobIds)
            } else {
                jobIdSet.retainAll(literalInputJobIds)
            }
            if (jobIdSet.isEmpty()) {
                return Optional.empty()
            }
        }

        // ok we found one or more job ids that have all the parameters
        // now it is the task to check if there were additional parameters that we don't checked yet.
        // TODO: This here doesn't consider the count of arguments. However for the moment we don't need that in RIESGOS.
        val sqlJobWpsIdentifier = """
            with cte_job as (
                select id
                from jobs
                where id = ?
            ),
            cte_input_identifier as (
                select complex_inputs.wps_identifier
                from complex_inputs
                join cte_job on cte_job.id = complex_inputs.job_id
                
                union all
                
                select complex_inputs_as_values.wps_identifier
                from complex_inputs_as_values
                join cte_job on cte_job.id = complex_inputs_as_values.job_id
                
                union all
                
                select literal_inputs.wps_identifier
                from literal_inputs
                join cte_job on cte_job.id = literal_inputs.job_id
                
                union all
                
                select complex_outputs_as_inputs.wps_identifier
                from complex_outputs_as_inputs
                join cte_job on cte_job.id = complex_outputs_as_inputs.job_id
                
                union all
                
                select bbox_inputs.wps_identifier
                from bbox_inputs
                join cte_job on cte_job.id = bbox_inputs.job_id
            )
            select distinct wps_identifier
            from cte_input_identifier
        """.trimIndent()

        // Ok all the jobIds had all of our parameters as we got them as
        // method parameters.
        // Now we are going to check if those used other additional parameters
        // that would have influence on the result
        for (jobId in jobIdSet) {
            // todo check that the job itself was sucessful.
            // now we extract the input identifiers from the query
            val usedWpsIdentifiersInThatJob = HashSet(jdbcTemplate.query(sqlJobWpsIdentifier, StringRowMapper("wps_identifier"), jobId) as List<String>)
            // and remove the input identifiers we got from the method call.
            for (complexInputKey in complexInputs.keys) {
                usedWpsIdentifiersInThatJob.remove(complexInputKey)
            }
            for (literalInputKey in literalInputs.keys) {
                usedWpsIdentifiersInThatJob.remove(literalInputKey)
            }
            for (bboxInputKey in bboxInputs.keys) {
                usedWpsIdentifiersInThatJob.remove(bboxInputKey)
            }
            if (usedWpsIdentifiersInThatJob.isEmpty()) {
                // Now we have found a job id that had the same input parameters (and no others!)
                return Optional.of(jobId)
            }
        }

        // either we haven't found an job that has all the identifiers, or the job had more input parameters
        return Optional.empty()
    }

    /**
     * Finds a process id or creates a process if not already in the database.
     */
    fun findProcessIdOrInsert(wpsUrl: String, wpsIdentifier: String): Long {
        val sqlLookup = """
            select id
            from processes
            where wps_url = ?
            and wps_identifier = ?
        """.trimIndent()

        try {
            return jdbcTemplate.queryForObject(sqlLookup, Long::class.javaObjectType, wpsUrl, wpsIdentifier)
        } catch (e: EmptyResultDataAccessException) {
            val sqlInsert = """
                insert into processes (wps_url, wps_identifier) values (?, ?)
            """.trimIndent()
            jdbcTemplate.update(sqlInsert, wpsUrl, wpsIdentifier)

            return jdbcTemplate.queryForObject(sqlLookup, Long::class.javaObjectType, wpsUrl, wpsIdentifier)
        }
    }

    /**
     * Create a job in the database. Return the id of the new job.
     */
    fun createJob (processId: Long, status: String): Long {
        // to get an id by the insert statement see
        // https://www.developinjava.com/spring/retrieve-auto-generated-key/
        val sqlInsert = """
            insert into jobs (process_id, status) values (?, ?)
            returning id
        """.trimIndent()

        val key = GeneratedKeyHolder()

        val preparedStatementCreator = PreparedStatementCreator { con: Connection ->
            val ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)
            ps.setLong(1, processId)
            ps.setString(2, status)
            ps
        }


        jdbcTemplate.update(preparedStatementCreator, key)

        return key.getKey()!!.toLong()
    }

    /**
     * Insert the literal input data into the database.
     */
    fun insertLiteralInput(jobId: Long, wpsIdentifier: String, inputValue: String): LiteralInput {
        return literalInputRepo.persist(LiteralInput(null, jobId, wpsIdentifier, inputValue))
    }

    /**
     * Insert the complex input data (with link) into the database.
     */
    fun insertComplexInput(jobId: Long, wpsIdentifier: String, complexInputConstraint: ComplexInputConstraint): ComplexInput {
        return complexInputRepo.persist(ComplexInput(null, jobId, wpsIdentifier, complexInputConstraint.link!!, complexInputConstraint.mimeType, complexInputConstraint.xmlschema, complexInputConstraint.encoding))
    }

    /**
     * Insert the bbox input data into the database.
     */
    fun insertBboxInput(jobId: Long, wpsIdentifier: String, bBoxInputConstraint: BBoxInputConstraint): BboxInput {
        return bboxInputRepo.persist(BboxInput(
                null, jobId, wpsIdentifier, bBoxInputConstraint.lowerCornerX, bBoxInputConstraint.lowerCornerY,
                bBoxInputConstraint.upperCornerX, bBoxInputConstraint.upperCornerY, bBoxInputConstraint.crs
        ))
    }

    /**
     * Insert the complex input data (with value) into the database.
     */
    fun insertComplexInputAsValue(jobId: Long, wpsIdentifier: String, complexInputConstraint: ComplexInputConstraint): ComplexInputAsValue {
        val sqlInsert = """
            insert into complex_inputs_as_values (job_id, wps_identifier, input_value, mime_type, xmlschema, encoding) values (?, ?, ?, ?, ?, ?)
            returning id
        """.trimIndent()

        val key = GeneratedKeyHolder()

        val preparedStatementCreator = PreparedStatementCreator { con: Connection ->
            val ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)
            ps.setLong(1, jobId)
            ps.setString(2, wpsIdentifier)
            ps.setString(3, complexInputConstraint.inputValue)
            ps.setString(4, complexInputConstraint.mimeType)
            ps.setString(5, complexInputConstraint.xmlschema)
            ps.setString(6, complexInputConstraint.encoding)
            ps
        }

        jdbcTemplate.update(preparedStatementCreator, key)

        val newId = key.getKey()!!.toLong()

        return ComplexInputAsValue(newId, jobId, wpsIdentifier, complexInputConstraint.inputValue!!, complexInputConstraint.mimeType, complexInputConstraint.xmlschema, complexInputConstraint.encoding)

    }

    /**
     * Search for an complex output that matches the link and format of
     * the input that we want to work with.
     *
     * The idea here is to reuse existing outputs (with the same link
     * and format) later in the input tables.
     *
     * Returns the id of the complex output or null.
     */
    fun findOptionalExistingComplexOutputToUseAsInput (complexInputConstraint: ComplexInputConstraint): ComplexOutput? {
        return complexOutputRepo.findOptionalFirstByLinkMimetypeXmlschemaAndEncoding(complexInputConstraint.link, complexInputConstraint.mimeType, complexInputConstraint.xmlschema, complexInputConstraint.encoding)
    }

    /**
     * Insert a complex input data with reference to the existing complex output for link & format in the database.
     * This reuses the link & format but also provides information about which
     * existing products where used to calculate new products.
     */
    fun insertComplexOutputAsInput (jobId: Long, complexOutput: ComplexOutput, wpsIdentifier: String): ComplexOutputAsInput {
        return complexOutputAsInputRepo.persist(ComplexOutputAsInput(null, jobId, wpsIdentifier, complexOutput))
    }

    /**
     * Update the status of the job.
     */
    fun updateJobStatus (jobId: Long, status: String) {
        val sql = """
            update jobs
            set status = ?
            where id = ?
        """.trimIndent()

        jdbcTemplate.update(sql, status, jobId)
    }

    /**
     * Insert a complex output data in the database.
     */
    fun insertComplexOutput (jobId: Long, wpsIdentifier: String, link: String, mimeType: String, xmlschema: String, encoding: String) {
        complexOutputRepo.persist(ComplexOutput(null, jobId, wpsIdentifier, link, mimeType, xmlschema, encoding))
    }

    fun findLiteralInputsForComplexOutput (complexInputConstraint: ComplexInputConstraint, wpsProcessIdentifier: String, wpsInputIdentifier: String): List<LiteralInput> {
        val sqlLiteralInputs = """
            select literal_inputs.*
            from literal_inputs
            join jobs on jobs.id = literal_inputs.job_id
            join processes on processes.id = libs.process_id
            join complex_outputs on complex_outputs.job_id = jobs.id
            where complex_outputs.link = ?
            and complex_outputs.mime_type = ?
            and complex_outputs.xmlschema = ?
            and complex_outputs.encoding = ?
            and literal_inputs.wps_identifier = ?
            and processes.wps_identifier = ?
       """.trimIndent()
        return jdbcTemplate.query(
                sqlLiteralInputs,
                LiteralInputRowMapper(),
                complexInputConstraint.link,
                complexInputConstraint.mimeType,
                complexInputConstraint.xmlschema,
                complexInputConstraint.encoding,
                wpsInputIdentifier,
                wpsProcessIdentifier
        )
    }
}