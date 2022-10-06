package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Statement

@Component
class ComplexInputRepo (val jdbcTemplate: JdbcTemplate) {
    fun findByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding (
            wpsProcessIdentifier: String,
            jobStatus: String,
            wpsInputIdentifier: String,
            link: String?,
            mimetype: String,
            xmlschema: String,
            encoding: String
    ): List<ComplexInput> {
        val sqlLiteralInputs = """
            select complex_inputs.*
            from complex_inputs
            join jobs on jobs.id = complex_inputs.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and jobs.status = ?
            and complex_inputs.wps_identifier = ?
            and complex_inputs.link = ?
            and complex_inputs.mime_type = ?
            and complex_inputs.xmlschema = ?
            and complex_inputs.encoding = ?
       """.trimIndent()
        return jdbcTemplate.query(
                sqlLiteralInputs,
                ComplexInputRowMapper(),
                wpsProcessIdentifier,
                jobStatus,
                wpsInputIdentifier,
                link,
                mimetype,
                xmlschema,
                encoding
        )
    }

    fun persist (complexInput: ComplexInput): ComplexInput {
        if (complexInput.id == null) {
            val sqlInsert = """
                insert into complex_inputs (job_id, wps_identifier, link, mime_type, xmlschema, encoding) values (?, ?, ?, ?, ?, ?)
                returning id
            """.trimIndent()

            val key = GeneratedKeyHolder()

            val preparedStatementCreator = PreparedStatementCreator { con: Connection ->
                val ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)
                ps.setLong(1, complexInput.jobId)
                ps.setString(2, complexInput.wpsIdentifier)
                ps.setString(3, complexInput.link)
                ps.setString(4, complexInput.mimeType)
                ps.setString(5, complexInput.xmlschema)
                ps.setString(6, complexInput.encoding)
                ps
            }

            jdbcTemplate.update(preparedStatementCreator, key)

            val newId = key.getKey()!!.toLong()

            return ComplexInput(newId, complexInput.jobId, complexInput.wpsIdentifier, complexInput.link, complexInput.mimeType, complexInput.xmlschema, complexInput.encoding)
        } else {
            val sqlUpdate = """
                update complex_inputs set 
                job_id = ?,
                wps_identifier = ?,
                link = ?,
                mime_type = ?,
                xmlschema = ?,
                encoding = ?
                where id = ?
            """.trimIndent()
            jdbcTemplate.update(sqlUpdate, complexInput.jobId, complexInput.wpsIdentifier, complexInput.link, complexInput.mimeType, complexInput.xmlschema, complexInput.encoding, complexInput.id)
            return complexInput
        }
    }
}