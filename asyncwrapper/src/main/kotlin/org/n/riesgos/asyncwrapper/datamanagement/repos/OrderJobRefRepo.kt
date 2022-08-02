package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.models.OrderJobRef
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class OrderJobRefRepo (val jdbcTemplate: JdbcTemplate) {
    fun persist (orderJobRef: OrderJobRef) {
        if (orderJobRef.id == null) {
            val sqlInsert = """
                insert into order_job_refs (order_id, job_id)
                values (?, ?)
            """.trimIndent()
            jdbcTemplate.update(sqlInsert, orderJobRef.orderId, orderJobRef.jobId)
        } else {
            val sqlUpdate = """
                update order_job_refs set
                order_id = ?,
                job_id = ?
                where id = ?
            """.trimIndent()
            jdbcTemplate.update(sqlUpdate, orderJobRef.orderId, orderJobRef.jobId, orderJobRef.id)
        }
    }
}