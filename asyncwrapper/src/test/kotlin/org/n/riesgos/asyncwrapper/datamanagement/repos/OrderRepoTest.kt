package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture

class OrderRepoTest {
    @Test
    fun testGetOptionalById() {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = OrderRepo(template)

        val noOrder = repo.getOptionalById(9999L)
        assertEquals(null, noOrder)

        // The format json is needed due to the H2 restrictions.
        // See https://h2database.com/html/datatypes.html#json_type
        template.execute("""
            insert into orders (id, order_constraints)
            values (1, '{"answer": 42}' FORMAT JSON)
        """.trimIndent())


        val existingOrder = repo.getOptionalById(1L)
        assertTrue(existingOrder != null)
        assertEquals(1L, existingOrder!!.id)

        assertTrue(existingOrder.orderConstraints != null)

        val expectedOrders = JSONObject()
        expectedOrders.put("answer", 42)

        assertEquals(expectedOrders.keySet(), existingOrder.orderConstraints!!.keySet())
        assertEquals(expectedOrders.get("answer"), existingOrder.orderConstraints!!.get("answer"))
    }
}