package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexOutputAsInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexOutputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutputAsInput
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Statement

@Component
class ComplexOutputAsInputRepo (val jdbcTemplate: JdbcTemplate) {

    fun findByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding (
            wpsProcessIdentifier: String,
            jobStatus: String,
            wpsInputIdentifier: String,
            link: String?,
            mimetype: String,
            xmlschema: String,
            encoding: String
    ): List<ComplexOutputAsInput> {
        val sqlComplexInputsAsValues = """
            select
                complex_outputs_as_inputs.id,
                complex_outputs_as_inputs.job_id,
                complex_outputs_as_inputs.wps_identifier,
                complex_outputs_as_inputs.complex_output_id,
                complex_outputs.job_id as output_job_id,
                complex_outputs.wps_identifier as output_wps_identifier,
                complex_outputs.link as output_link,
                complex_outputs.mime_type as output_mime_type,
                complex_outputs.xmlschema as output_xmlschema,
                complex_outputs.encoding as output_encoding
            from complex_outputs_as_inputs
            join complex_outputs on complex_outputs_as_inputs.complex_output_id = complex_outputs.id
            join jobs on jobs.id = complex_outputs_as_inputs.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and jobs.status = ?
            and complex_outputs_as_inputs.wps_identifier = ?
            and complex_outputs.link = ?
            and complex_outputs.mime_type = ?
            and complex_outputs.xmlschema = ?
            and complex_outputs.encoding = ?
       """.trimIndent()
        return jdbcTemplate.query(
                sqlComplexInputsAsValues,
                ComplexOutputAsInputRowMapper(),
                wpsProcessIdentifier,
                jobStatus,
                wpsInputIdentifier,
                link,
                mimetype,
                xmlschema,
                encoding
        )
    }

    fun persist (complexOutputAsInput: ComplexOutputAsInput): ComplexOutputAsInput {
        if (complexOutputAsInput.id == null) {
            val sqlInsert = """
                insert into complex_outputs_as_inputs (job_id, complex_output_id, wps_identifier)
                values (?, ?, ?)
                returning id
            """.trimIndent()

            val key = GeneratedKeyHolder()

            val preparedStatementCreator = PreparedStatementCreator { con: Connection ->
                val ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)
                ps.setLong(1, complexOutputAsInput.jobId)
                ps.setLong(2, complexOutputAsInput.complexOutput.id!!)
                ps.setString(3, complexOutputAsInput.wpsIdentifier)
                ps
            }

            jdbcTemplate.update(preparedStatementCreator, key)

            val newId = key.getKey()!!.toLong()
            return ComplexOutputAsInput(newId, complexOutputAsInput.jobId, complexOutputAsInput.wpsIdentifier, complexOutputAsInput.complexOutput)
        } else {
            val sqlUpdate = """
                update complex_outputs_as_inputs set 
                job_id = ?,
                wps_identifier = ?,
                complex_output_id = ?,
                where id = ?
            """.trimIndent()
            jdbcTemplate.update(sqlUpdate, complexOutputAsInput.jobId, complexOutputAsInput.wpsIdentifier, complexOutputAsInput.complexOutput.id!!, complexOutputAsInput.id)
            return complexOutputAsInput
        }
    }

    fun findInputsByJobId (jobId: Long): List<ComplexOutputAsInput> {
        val sql = """
            select
                complex_outputs_as_inputs.id,
                complex_outputs_as_inputs.job_id,
                complex_outputs_as_inputs.wps_identifier,
                complex_outputs_as_inputs.complex_output_id,
                complex_outputs.job_id as output_job_id,
                complex_outputs.wps_identifier as output_wps_identifier,
                complex_outputs.link as output_link,
                complex_outputs.mime_type as output_mime_type,
                complex_outputs.xmlschema as output_xmlschema,
                complex_outputs.encoding as output_encoding
            from complex_outputs_as_inputs
            where complex_outputs_as_inputs.job_id = ?
        """.trimIndent()
        return jdbcTemplate.query(
                sql,
                ComplexOutputAsInputRowMapper(),
                jobId
        )
    }
}