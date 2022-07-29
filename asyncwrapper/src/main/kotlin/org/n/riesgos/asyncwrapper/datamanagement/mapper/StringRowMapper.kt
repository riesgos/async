package org.n.riesgos.asyncwrapper.datamanagement.mapper

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class StringRowMapper (val field: String) : RowMapper<String> {
    @Override
    override fun mapRow(rs: ResultSet, rowNum: Int): String? {
        return rs.getString(field)
    }
}