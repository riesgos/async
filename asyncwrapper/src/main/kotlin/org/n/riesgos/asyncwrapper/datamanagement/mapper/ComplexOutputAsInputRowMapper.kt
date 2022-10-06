package org.n.riesgos.asyncwrapper.datamanagement.mapper

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutputAsInput
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class ComplexOutputAsInputRowMapper : RowMapper<ComplexOutputAsInput> {
    @Override
    override fun mapRow(rs: ResultSet, rowNum: Int): ComplexOutputAsInput? {
        return ComplexOutputAsInput(
                rs.getLong("id"),
                rs.getLong("job_id"),
                rs.getString("wps_identifier"),
                ComplexOutput(
                        rs.getLong("complex_output_id"),
                        rs.getLong("output_job_id"),
                        rs.getString("output_wps_identifier"),
                        rs.getString("output_link"),
                        rs.getString("output_mime_type"),
                        rs.getString("output_xmlschema"),
                        rs.getString("output_encoding")
                )
        )

    }
}