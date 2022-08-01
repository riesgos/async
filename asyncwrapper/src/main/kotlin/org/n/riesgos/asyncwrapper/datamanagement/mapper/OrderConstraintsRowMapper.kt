package org.n.riesgos.asyncwrapper.datamanagement.mapper

import org.json.JSONObject
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class OrderConstraintsRowMapper : RowMapper<JSONObject> {
    @Override
    override fun mapRow(rs: ResultSet, rowNum: Int): JSONObject? {
        val jsonString = rs.getString("order_constraints")
        if (jsonString != null) {
            return JSONObject(jsonString)
        }
        return null
    }
}