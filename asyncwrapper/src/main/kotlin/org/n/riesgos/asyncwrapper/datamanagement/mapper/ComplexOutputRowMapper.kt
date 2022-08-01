package org.n.riesgos.asyncwrapper.datamanagement.mapper

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.springframework.jdbc.core.RowMapper
import java.math.BigInteger
import java.sql.ResultSet

class ComplexOutputRowMapper : RowMapper<ComplexOutput> {
    @Override
    override fun mapRow(rs: ResultSet, rowNum: Int): ComplexOutput? {
        return ComplexOutput(
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