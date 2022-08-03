package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.OrderRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.Order
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class OrderRepo (val jdbcTemplate: JdbcTemplate) {
    fun getOptionalById (id: Long): Order? {
        val sql = """select orders.* from orders where id = ?"""
        try {
            return jdbcTemplate.queryForObject(sql, OrderRowMapper(), id)
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }
}