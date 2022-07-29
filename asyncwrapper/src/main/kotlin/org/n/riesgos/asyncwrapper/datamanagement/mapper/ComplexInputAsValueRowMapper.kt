package org.n.riesgos.asyncwrapper.datamanagement.mapper

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputAsValue
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class ComplexInputAsValueRowMapper : RowMapper<ComplexInputAsValue> {
    @Override
    override fun mapRow(rs: ResultSet, rowNum: Int): ComplexInputAsValue? {
        return ComplexInputAsValue(
                rs.getLong("id"),
                rs.getLong("job_id"),
                rs.getString("wps_identifier"),
                rs.getString("input_value"),
                rs.getString("mime_type"),
                rs.getString("xmlschema"),
                rs.getString("encoding")
        )
    }
}