package org.n.riesgos.asyncwrapper.datamanagement.mapper

import org.json.JSONObject
import org.n.riesgos.asyncwrapper.datamanagement.models.Order
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class OrderRowMapper : RowMapper<Order> {
    @Override
    override fun mapRow(rs: ResultSet, rowNum: Int): Order {
        return Order(rs.getLong("id"), mapConstraints(rs), rs.getLong("user_id"))
    }

    fun mapConstraints (rs: ResultSet): JSONObject? {
        val jsonString = rs.getString("order_constraints")
        if (jsonString != null) {
            return JSONObject(jsonString)
        }
        return null
    }
}