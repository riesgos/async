package org.n.riesgos.asyncwrapper.datamanagement

import org.n.riesgos.asyncwrapper.datamanagement.mapper.BboxInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.BboxInput
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class BboxInputRepo (val jdbcTemplate: JdbcTemplate) {
    fun findByProcessWpsIdentifierInputWpsIdentifierCornersAndCrs (
            wpsProcessIdentifier: String, wpsInputIdentifier: String,
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
                wpsInputIdentifier,
                lowerCornerX,
                lowerCornerY,
                upperCornerX,
                upperCornerY,
                crs
        )
    }

    fun persist (bboxInput: BboxInput) {
        if (bboxInput.id == null) {
            val sqlInsert = """
                insert into bbox_inputs (job_id, wps_identifier, lower_corner_x, lower_corner_y, upper_corner_x, upper_corner_y, crs)
                values (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
            jdbcTemplate.update(sqlInsert, bboxInput.jobId, bboxInput.wpsIdentifier,
                    bboxInput.lowerCornerX,
                    bboxInput.lowerCornerY,
                    bboxInput.upperCornerX,
                    bboxInput.upperCornerY,
                    bboxInput.crs
            )
            // Doesn't extract the id at the moment.
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
        }
    }
}