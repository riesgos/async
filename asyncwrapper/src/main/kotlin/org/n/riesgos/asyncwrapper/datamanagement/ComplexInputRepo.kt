package org.n.riesgos.asyncwrapper.datamanagement

import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class ComplexInputRepo (val jdbcTemplate: JdbcTemplate) {
    fun findByProcessWpsIdentifierInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding (
            wpsProcessIdentifier: String,
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
                wpsInputIdentifier,
                link,
                mimetype,
                xmlschema,
                encoding
        )
    }

    fun persist (complexInput: ComplexInput) {
        if (complexInput.id == null) {
            val sqlInsert = """
                insert into complex_inputs (job_id, wps_identifier, link, mime_type, xmlschema, encoding) values (?, ?, ?, ?, ?, ?)
            """.trimIndent()
            jdbcTemplate.update(sqlInsert, complexInput.jobId, complexInput.wpsIdentifier,
                    complexInput.link, complexInput.mimeType, complexInput.xmlschema, complexInput.encoding)
            // Doesn't extract the id at the moment.
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
        }
    }
}