package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexOutputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

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
    ): List<ComplexInput> {
        val sqlComplexInputsAsValues = """
            select
                complex_outputs_as_inputs.id,
                complex_outputs_as_inputs.job_id,
                complex_outputs_as_inputs.wps_identifier
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
                // we query the exact same fields (but we will
                // link, mimeType, xmlschema and encoding from
                // the exsting output).
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
}