package org.n.riesgos.asyncwrapper.datamanagement.mapper


import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class LiteralInputRowMapper : RowMapper<LiteralInput> {
    @Override
    override fun mapRow(rs: ResultSet, rowNum: Int): LiteralInput? {
        return LiteralInput(
                rs.getLong("id"),
                rs.getLong("job_id"),
                rs.getString("wps_identifier"),
                rs.getString("inputValue")
        )
    }
}