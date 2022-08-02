package org.n.riesgos.asyncwrapper.datamanagement

import org.json.JSONObject
import org.n.riesgos.asyncwrapper.datamanagement.mapper.*
import org.n.riesgos.asyncwrapper.datamanagement.models.BBoxInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
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
class DatamanagementRepo (val jdbcTemplate: JdbcTemplate) {
    /**
     * Extracts the constraints of the order as json object.
     */
    fun orderConstraints(orderId: Long): JSONObject? {
        val sql = """
            select order_constraints
            from orders
            where id=?
        """.trimIndent()

        try {
            return jdbcTemplate.queryForObject(sql, OrderConstraintsRowMapper(), orderId)
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
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
        val sql = """
            insert into order_job_refs (job_id, order_id) values (?, ?)
        """.trimIndent()

        jdbcTemplate.update(sql, jobId, orderId)
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
     * Return true if the process with the exact input parameters
     * already has an job id in the database.
     *
     * Considers complex inputs (ref or value, or even reused outputs),
     * literal inputs, and bbox inputs.
     */
    // TODO maybe this should be synchronized (but how to do that in Kotlin?)
    fun hasAlreadyProcessed(
            processIdentifier: String,
            complexInputs: Map<String, ComplexInputConstraint>,
            literalInputs: Map<String, String>,
            bboxInputs: Map<String, BBoxInputConstraint>
    ): Boolean {
        // first search for the complex inputs that were there
        val sqlComplexInputs = """
            select complex_inputs.*
            from complex_inputs
            join jobs on jobs.id = complex_inputs.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and complex_inputs.wps_identifier = ?
            and complex_inputs.link = ?
            and complex_inputs.mime_type = ?
            and complex_inputs.xmlschema = ?
            and complex_inputs.encoding = ?
        """.trimIndent()

        val sqlComplexInputsAsValues = """
            select complex_inputs_as_values.*
            from complex_inputs_as_values
            join jobs on jobs.id = complex_inputs_as_values.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and complex_inputs_as_values.wps_identifier = ?
            and complex_inputs_as_values.input_value = ?
            and complex_inputs_as_values.mime_type = ?
            and complex_inputs_as_values.xmlschema = ?
            and complex_inputs_as_values.encoding = ?
        """.trimIndent()

        val sqlComplexOutputAsInputs = """
            select
                complex_outputs_as_inputs.id,
                complex_outputs_as_inputs.job_id,
                complex_outputs_as_inputs.wps_identifier
            from complex_outputs_as_inputs
            join complex_outputs on complex_outputs_as_inputs.complex_output_id = complex_outputs.id
            join jobs on jobs.id = complex_outputs_as_inputs.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and complex_outputs_as_inputs.wps_identifier = ?
            and complex_outputs.link = ?
            and complex_outputs.mime_type = ?
            and complex_outputs.xmlschema = ?
            and complex_outputs.encoding = ?
        """.trimIndent()

       val sqlLiteralInputs = """
           select literal_inputs.*
            from literal_inputs
            join jobs on jobs.id = literal_inputs.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and literal_inputs.wps_identifier = ?
            and literal_inputs.input_value = ?
       """.trimIndent()

        val sqlBboxInputs = """
            select bbox_inputs.*
            from bbox_inputs
            join jobs on jobs.id = bbox_inputs.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and bbox_inputs.wps_identifier = ?
            and bbox_inputs.lower_corner_x = ?
            and bbox_inputs.lower_corner_y = ?
            and bbox_inputs.upper_corner_x = ?
            and bbox_inputs.upper_corner_y = ?
            and bbox_inputs.crs = ?
        """.trimIndent()

        val jobIdSet = HashSet<Long>()
        var jobIdSetNotSetYet = true
        for (complexInputKey in complexInputs.keys) {
            val complexInput = complexInputs.get(complexInputKey) as ComplexInputConstraint
            val searchForThisComplexInput =
                jdbcTemplate.query(
                        sqlComplexInputs,
                        ComplexInputRowMapper(),
                        processIdentifier,
                        complexInputKey,
                        complexInput.link,
                        complexInput.mimeType,
                        complexInput.xmlschema,
                        complexInput.encoding
                )

            val searchForThisComplexOutputAsInput =
                    jdbcTemplate.query(
                            sqlComplexOutputAsInputs,
                            // we query the exact same fields (but we will
                            // link, mimeType, xmlschema and encoding from
                            // the exsting output).
                            ComplexInputRowMapper(),
                            processIdentifier,
                            complexInputKey,
                            complexInput.link,
                            complexInput.mimeType,
                            complexInput.xmlschema,
                            complexInput.encoding
                    )

            val searchForThisComplexInputAsValue =
                    jdbcTemplate.query(
                            sqlComplexInputsAsValues,
                            ComplexInputAsValueRowMapper(),
                            processIdentifier,
                            complexInputKey,
                            complexInput.inputValue,
                            complexInput.mimeType,
                            complexInput.xmlschema,
                            complexInput.encoding
                    )
            // if we all of them are empty, we never used that complex input
            // so there is no way a job has already processed those
            if (searchForThisComplexInput.isEmpty() && searchForThisComplexInputAsValue.isEmpty() && searchForThisComplexOutputAsInput.isEmpty()) {
                return false
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
                return false
            }
        }
        // same question for the literal inputs
        for (literalInputKey in literalInputs.keys) {
            val literalInput = literalInputs.get(literalInputKey)
            val searchForThisLiteralInput =
                    jdbcTemplate.query(
                            sqlLiteralInputs,
                            LiteralInputRowMapper(),
                            processIdentifier,
                            literalInputKey,
                            literalInput
                    )

            if (searchForThisLiteralInput.isEmpty()) {
                return false
            }
            val literalInputJobIds = searchForThisLiteralInput.stream().map({ x -> x.jobId}).distinct().collect(Collectors.toSet())
            if (jobIdSetNotSetYet) {
                jobIdSet.addAll(literalInputJobIds)
                jobIdSetNotSetYet = false
            } else {
                jobIdSet.retainAll(literalInputJobIds)
            }
            if (jobIdSet.isEmpty()) {
                return false
            }
        }
        // same for the bbox inputs
        for (bboxInputKey in bboxInputs.keys) {
            val bboxInput = bboxInputs.get(bboxInputKey)
            val searchForTthisBboxInput =
                    jdbcTemplate.query(
                            sqlBboxInputs,
                            BboxInputRowMapper(),
                            processIdentifier,
                            bboxInputKey,
                            bboxInput!!.lowerCornerY,
                            bboxInput.lowerCornerY,
                            bboxInput.upperCornerY,
                            bboxInput.upperCornerY,
                            bboxInput.crs
                    )
            if (searchForTthisBboxInput.isEmpty()) {
                return false
            }
            // TODO
            val literalInputJobIds = searchForTthisBboxInput.stream().map { x -> x.jobId }.distinct().collect(Collectors.toSet())
            if (jobIdSetNotSetYet) {
                jobIdSet.addAll(literalInputJobIds)
            } else {
                jobIdSet.retainAll(literalInputJobIds)
            }
            if (jobIdSet.isEmpty()) {
                return false
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
                join cte_job on cte_job.id = complex_inputs.job_id
                
                union all
                
                select complex_inputs_as_values.wps_identifier
                join cte_job on cte_job.id = complex_inputs_as_values.job_id
                
                union all
                
                select literal_inputs.wps_identifier
                join cte_job on cte_job.id = literal_inputs.job_id
                
                union all
                
                select complex_outputs_as_inputs.wps_identifier
                join cte_job on cte_job.id = complex_outputs_as_inputs.job_id
                
                select bbox_inputs.wps_identifier
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
                return true
            }
        }

        // either we haven't found an job that has all the identifiers, or the job had more input parameters
        return false
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
    fun insertLiteralInput(jobId: Long, wpsIdentifier: String, inputValue: String) {
        val sqlInsert = """
            insert into literal_inputs (job_id, wps_identifier, input_value) values (?, ?, ?)
        """.trimIndent()
        jdbcTemplate.update(sqlInsert, jobId, wpsIdentifier, inputValue)
    }

    /**
     * Insert the complex input data (with link) into the database.
     */
    fun insertComplexInput(jobId: Long, wpsIdentifier: String, complexInputConstraint: ComplexInputConstraint) {
        val sqlInsert = """
            insert into complex_inputs (job_id, wps_identifier, link, mime_type, xmlschema, encoding) values (?, ?, ?, ?, ?, ?)
        """.trimIndent()
        jdbcTemplate.update(sqlInsert, jobId, wpsIdentifier, complexInputConstraint.link, complexInputConstraint.mimeType, complexInputConstraint.xmlschema, complexInputConstraint.encoding)
    }

    /**
     * Insert the bbox input data into the database.
     */
    fun insertBboxInput(jobId: Long, wpsIdentifier: String, bBoxInputConstraint: BBoxInputConstraint) {
        val sqlInsert = """
            insert into bbox_inputs (job_id, wps_identifier, lower_corner_x, lower_corner_y, upper_corner_x, upper_corner_y, crs)
            values (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        jdbcTemplate.update(sqlInsert, jobId, wpsIdentifier, bBoxInputConstraint.lowerCornerX, bBoxInputConstraint.lowerCornerY, bBoxInputConstraint.upperCornerX, bBoxInputConstraint.upperCornerY, bBoxInputConstraint.crs)
    }

    /**
     * Insert the complex input data (with value) into the database.
     */
    fun insertComplexInputAsValue(jobId: Long, wpsIdentifier: String, complexInputConstraint: ComplexInputConstraint) {
        val sqlInsert = """
            insert into complex_inputs_as_values (job_id, wps_identifier, input_value, mime_type, xmlschema, encoding) values (?, ?, ?, ?, ?, ?)
        """.trimIndent()
        jdbcTemplate.update(sqlInsert, jobId, wpsIdentifier, complexInputConstraint.inputValue, complexInputConstraint.mimeType, complexInputConstraint.xmlschema, complexInputConstraint.encoding)
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
    fun findOptionalExistingComplexOutputToUseAsInput (complexInputConstraint: ComplexInputConstraint): Long? {
        val sql = """
            select complex_output_id
            from complex_outputs
            join jobs on jobs.id = complex_outputs.job_id
            join processes on processes.id = jobs.process_id
            where complex_outputs.link = ?
            and complex_outputs.mime_type = ?
            and complex_outputs.xmlschema = ?
            and complex_outputs.encoding = ?
        """.trimIndent()
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    Long::class.javaObjectType,
                    complexInputConstraint.link,
                    complexInputConstraint.mimeType,
                    complexInputConstraint.xmlschema,
                    complexInputConstraint.encoding
            )
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    /**
     * Insert a complex input data with reference to the existing complex output for link & format in the database.
     * This reuses the link & format but also provides information about which
     * existing products where used to calculate new products.
     */
    fun insertComplexOutputAsInput (jobId: Long, complexOutputId: Long, wpsIdentifier: String) {
        val sql = """
            insert into complex_outputs_as_inputs (job_id, complex_output_id, wps_identifier)
            values (?, ?, ?)
        """.trimIndent()
        jdbcTemplate.update(sql, jobId, complexOutputId, wpsIdentifier)
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
        val sql = """
            insert into complex_outputs (job_id, wps_identifier, link, mime_type, xmlschema, encoding)
            values (?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(sql, jobId, wpsIdentifier, link, mimeType, xmlschema, encoding)
    }

    fun findLiteralInputsForComplexOutput (complexInputConstraint: ComplexInputConstraint, wpsInputIdentifier: String): List<LiteralInput> {
        val sqlLiteralInputs = """
            select literal_inputs.*
            from literal_inputs
            join complex_outputs complex_outputs on complex_outputs.job_id = literal_inputs.job_id
            where complex_outputs.link = ?
            and complex_outputs.mime_type = ?
            and complex_outputs.xmlschema = ?
            and complex_outputs.encoding = ?
            and literal_inputs.wps_identifier = ?
            and literal_inputs.input_value = ?
       """.trimIndent()
        return jdbcTemplate.query(
                sqlLiteralInputs,
                LiteralInputRowMapper(),
                complexInputConstraint.link,
                complexInputConstraint.mimeType,
                complexInputConstraint.xmlschema,
                complexInputConstraint.encoding,
                wpsInputIdentifier
        )
    }
}