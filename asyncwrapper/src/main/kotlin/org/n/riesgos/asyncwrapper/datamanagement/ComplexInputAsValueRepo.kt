package org.n.riesgos.asyncwrapper.datamanagement

import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexInputAsValueRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputAsValue
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class ComplexInputAsValueRepo (val jdbcTemplate: JdbcTemplate) {
    fun findByProcessWpsIdentifierInputWpsIdentifierInputValueMimetypeXmlSchemaAndEncoding (
            wpsProcessIdentifier: String,
            wpsInputIdentifier: String,
            inputValue: String?,
            mimetype: String,
            xmlschema: String,
            encoding: String
    ): List<ComplexInputAsValue> {
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
        return jdbcTemplate.query(
                sqlComplexInputsAsValues,
                ComplexInputAsValueRowMapper(),
                wpsProcessIdentifier,
                wpsInputIdentifier,
                inputValue,
                mimetype,
                xmlschema,
                encoding
        )
    }
}