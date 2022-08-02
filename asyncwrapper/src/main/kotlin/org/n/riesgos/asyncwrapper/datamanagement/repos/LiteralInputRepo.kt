package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class LiteralInputRepo (val jdbcTemplate: JdbcTemplate) {
    fun findByProcessWpsIdentifierInputWpsIdentifierAndValue (wpsProcessIdentifier: String, wpsInputIdentifier: String, inputValue: String): List<LiteralInput> {
        val sqlLiteralInputs = """
            select literal_inputs.*
            from literal_inputs
            join jobs on jobs.id = literal_inputs.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and literal_inputs.wps_identifier = ?
            and literal_inputs.input_value = ?
       """.trimIndent()
        return jdbcTemplate.query(
                sqlLiteralInputs,
                LiteralInputRowMapper(),
                wpsProcessIdentifier,
                wpsInputIdentifier,
                inputValue
        )
    }

    fun persist (literalInput: LiteralInput) {
        if (literalInput.id == null) {
            val sqlInsert = """
                insert into literal_inputs (job_id, wps_identifier, input_value) values (?, ?, ?)
            """.trimIndent()
            jdbcTemplate.update(sqlInsert, literalInput.jobId, literalInput.wpsIdentifier, literalInput.inputValue)
            // Doesn't extract the id at the moment.
        } else {
            val sqlUpdate = """
                update literal_inputs set 
                job_id = ?,
                wps_identifier = ?,
                input_value = ?
                where id = ?
            """.trimIndent()
            jdbcTemplate.update(sqlUpdate, literalInput.jobId, literalInput.wpsIdentifier, literalInput.inputValue, literalInput.id)
        }
    }
}