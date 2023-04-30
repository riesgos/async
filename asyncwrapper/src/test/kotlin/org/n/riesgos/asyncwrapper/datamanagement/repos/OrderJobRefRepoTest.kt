package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture
import org.n.riesgos.asyncwrapper.datamanagement.models.OrderJobRef

class OrderJobRefRepoTest {
    @Test
    fun testPersist() {
        val template = H2DbFixture().getJdbcTemplate()

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (1, 1, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into orders (id)
                    values (1)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into orders (id)
                    values (2)
                """.trimIndent()
                )

        val currentCount = template.queryForObject("select count(*) from order_job_refs", Int::class.javaObjectType)
        assertEquals(0, currentCount)

        val repo = OrderJobRefRepo(template)

        repo.persist(OrderJobRef(null, 1, 1))


        val countOrder1 = template.queryForObject("select count(*) from order_job_refs where order_id = 1", Int::class.javaObjectType)
        assertEquals(1, countOrder1)

        val orderId = template.queryForObject("select id from order_job_refs where order_id = 1 limit 1", Int::class.javaObjectType)
        assertTrue(orderId != null)
        repo.persist(OrderJobRef(orderId!!.toLong(), 2, 1))

        val countOrder1AfterUpdate = template.queryForObject("select count(*) from order_job_refs where order_id = 1", Int::class.javaObjectType)
        assertEquals(0, countOrder1AfterUpdate)

        val countOrder2 = template.queryForObject("select count(*) from order_job_refs where order_id = 2", Int::class.javaObjectType)
        assertEquals(1, countOrder2)
    }
}