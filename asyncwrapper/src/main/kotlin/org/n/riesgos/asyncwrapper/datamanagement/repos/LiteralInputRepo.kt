package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Statement

@Component
class LiteralInputRepo (val jdbcTemplate: JdbcTemplate) {
    fun findByProcessWpsIdentifierJobStatusInputWpsIdentifierAndValue (wpsProcessIdentifier: String, jobStatus: String, wpsInputIdentifier: String, inputValue: String): List<LiteralInput> {
        val sqlLiteralInputs = """
            select literal_inputs.*
            from literal_inputs
            join jobs on jobs.id = literal_inputs.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and jobs.status = ?
            and literal_inputs.wps_identifier = ?
            and literal_inputs.input_value = ?
       """.trimIndent()
        return jdbcTemplate.query(
                sqlLiteralInputs,
                LiteralInputRowMapper(),
                wpsProcessIdentifier,
                jobStatus,
                wpsInputIdentifier,
                inputValue
        )
    }

    fun persist (literalInput: LiteralInput) : LiteralInput {
        if (literalInput.id == null) {
            val sqlInsert = """
                insert into literal_inputs (job_id, wps_identifier, input_value) values (?, ?, ?)
                returning id
            """.trimIndent()

            val key = GeneratedKeyHolder()

            val preparedStatementCreator = PreparedStatementCreator { con: Connection ->
                val ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)
                ps.setLong(1, literalInput.jobId)
                ps.setString(2, literalInput.wpsIdentifier)
                ps.setString(3, literalInput.inputValue)
                ps
            }


            jdbcTemplate.update(preparedStatementCreator, key)

            val newId = key.getKey()!!.toLong()

            return LiteralInput(newId, literalInput.jobId, literalInput.wpsIdentifier, literalInput.inputValue)

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
            return literalInput
        }
    }
}