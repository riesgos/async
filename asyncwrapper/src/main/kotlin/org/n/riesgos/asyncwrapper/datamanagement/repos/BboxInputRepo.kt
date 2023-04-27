package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.BboxInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.BboxInput
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Statement

@Component
class BboxInputRepo (val jdbcTemplate: JdbcTemplate) {
    fun findByProcessWpsIdentifierJobStatusInputWpsIdentifierCornersAndCrs (
            wpsProcessIdentifier: String, jobStatus: String,
            wpsInputIdentifier: String,
            lowerCornerX: Double,
            lowerCornerY: Double,
            upperCornerX: Double,
            upperCornerY: Double,
            crs: String
    ): List<BboxInput> {
        val sqlBboxInputs = """
            select bbox_inputs.*
            from bbox_inputs
            join jobs on jobs.id = bbox_inputs.job_id
            join processes on processes.id = jobs.process_id
            where processes.wps_identifier = ?
            and jobs.status = ?
            and bbox_inputs.wps_identifier = ?
            and bbox_inputs.lower_corner_x = ?
            and bbox_inputs.lower_corner_y = ?
            and bbox_inputs.upper_corner_x = ?
            and bbox_inputs.upper_corner_y = ?
            and bbox_inputs.crs = ?
        """.trimIndent()
        return jdbcTemplate.query(
                sqlBboxInputs,
                BboxInputRowMapper(),
                wpsProcessIdentifier,
                jobStatus,
                wpsInputIdentifier,
                lowerCornerX,
                lowerCornerY,
                upperCornerX,
                upperCornerY,
                crs
        )
    }

    fun persist (bboxInput: BboxInput): BboxInput {
        if (bboxInput.id == null) {
            val sqlInsert = """
                insert into bbox_inputs (job_id, wps_identifier, lower_corner_x, lower_corner_y, upper_corner_x, upper_corner_y, crs)
                values (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
            val key = GeneratedKeyHolder()
            val preparedStatementCreator = PreparedStatementCreator { con: Connection ->
                val ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)
                ps.setLong(1, bboxInput.jobId)
                ps.setString(2, bboxInput.wpsIdentifier)
                ps.setDouble(3, bboxInput.lowerCornerX)
                ps.setDouble(4, bboxInput.lowerCornerY)
                ps.setDouble(5, bboxInput.upperCornerX)
                ps.setDouble(6, bboxInput.upperCornerY)
                ps.setString(7, bboxInput.crs)
                ps
            }
            jdbcTemplate.update(preparedStatementCreator, key)

            val newId = (key.getKeyList().get(0).get("id") as Integer).toLong()
            return BboxInput(
                    newId, bboxInput.jobId, bboxInput.wpsIdentifier, bboxInput.lowerCornerX, bboxInput.lowerCornerY,
                    bboxInput.upperCornerX, bboxInput.upperCornerY, bboxInput.crs
            )
        } else {
            val sqlUpdate = """
                update bbox_inputs set 
                job_id = ?, 
                wps_identifier = ?,
                lower_corner_x = ?, 
                lower_corner_y = ?,
                upper_corner_x = ?, 
                upper_corner_y = ?, 
                crs = ?
                where id = ?
            """.trimIndent()

            jdbcTemplate.update(sqlUpdate, bboxInput.jobId, bboxInput.wpsIdentifier,
                    bboxInput.lowerCornerX,
                    bboxInput.lowerCornerY,
                    bboxInput.upperCornerX,
                    bboxInput.upperCornerY,
                    bboxInput.crs,
                    bboxInput.id)
            return bboxInput
        }
    }
}