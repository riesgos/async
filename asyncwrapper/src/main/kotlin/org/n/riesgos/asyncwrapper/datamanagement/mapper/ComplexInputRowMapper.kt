package org.n.riesgos.asyncwrapper.datamanagement.mapper

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class ComplexInputRowMapper : RowMapper<ComplexInput> {
    @Override
    override fun mapRow(rs: ResultSet, rowNum: Int): ComplexInput? {
        return ComplexInput(
                rs.getLong("id"),
                rs.getLong("job_id"),
                rs.getString("wps_identifier"),
                rs.getString("link"),
                rs.getString("mime_type"),
                rs.getString("xmlschema"),
                rs.getString("encoding")
        )
    }
}