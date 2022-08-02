package org.n.riesgos.asyncwrapper.datamanagement.mapper

import org.n.riesgos.asyncwrapper.datamanagement.models.BboxInput
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class BboxInputRowMapper : RowMapper<BboxInput> {
    override fun mapRow(rs: ResultSet, rowNum: Int): BboxInput? {
        return BboxInput(
                rs.getLong("id"),
                rs.getLong("job_id"),
                rs.getString("wps_identifier"),
                rs.getDouble("lower_corner_x"),
                rs.getDouble("lower_corner_y"),
                rs.getDouble("upper_corner_x"),
                rs.getDouble("upper_corner_y"),
                rs.getString("crs")
        )
    }
}