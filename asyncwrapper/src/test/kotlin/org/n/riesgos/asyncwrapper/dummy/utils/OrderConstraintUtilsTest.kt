package org.n.riesgos.asyncwrapper.dummy.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture
import org.n.riesgos.asyncwrapper.datamanagement.models.BBoxInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.models.JobIdConstraintResult
import org.n.riesgos.asyncwrapper.datamanagement.models.OrderConstraintsResult
import org.n.riesgos.asyncwrapper.datamanagement.repos.*

class OrderConstraintUtilsTest {

    fun ignore(someString: String) {
        // nothing
    }
    @Test
    fun testGetOrderConstraintsNoOrder() {

        val template = H2DbFixture().getJdbcTemplate()
        val repo = DatamanagementRepo(
                template,
                LiteralInputRepo(template),
                ComplexInputRepo(template),
                ComplexOutputAsInputRepo(template),
                ComplexInputAsValueRepo(template),
                BboxInputRepo(template),
                OrderJobRefRepo(template),
                ComplexOutputRepo(template),
                OrderRepo(template),
                StoredLinkRepo(template)
        )


        val result = OrderConstraintUtils(repo).getOrderConstraints(1L, "something", ::ignore)
        assertEquals(null, result)
    }

    @Test
    fun testGetOrderConstraintsNoConstraints() {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = DatamanagementRepo(
                template,
                LiteralInputRepo(template),
                ComplexInputRepo(template),
                ComplexOutputAsInputRepo(template),
                ComplexInputAsValueRepo(template),
                BboxInputRepo(template),
                OrderJobRefRepo(template),
                ComplexOutputRepo(template),
                OrderRepo(template),
                StoredLinkRepo(template)
        )

        template.execute("""insert into orders (id) values (1)""")


        val result = OrderConstraintUtils(repo).getOrderConstraints(1L, "something", ::ignore)
        assertEquals(null, result)
    }

    @Test
    fun testGetOrderConstraintsEmptyObject() {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = DatamanagementRepo(
                template,
                LiteralInputRepo(template),
                ComplexInputRepo(template),
                ComplexOutputAsInputRepo(template),
                ComplexInputAsValueRepo(template),
                BboxInputRepo(template),
                OrderJobRefRepo(template),
                ComplexOutputRepo(template),
                OrderRepo(template),
                StoredLinkRepo(template)
        )

        template.execute("""insert into orders (id, order_constraints) values (1, '{}' FORMAT JSON)""")

        val result = OrderConstraintUtils(repo).getOrderConstraints(1L, "something", ::ignore)
        assertEquals(OrderConstraintsResult(emptyMap(), emptyMap(), emptyMap()), result)
    }

    @Test
    fun testGetOrderConstraintsJobId() {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = DatamanagementRepo(
                template,
                LiteralInputRepo(template),
                ComplexInputRepo(template),
                ComplexOutputAsInputRepo(template),
                ComplexInputAsValueRepo(template),
                BboxInputRepo(template),
                OrderJobRefRepo(template),
                ComplexOutputRepo(template),
                OrderRepo(template),
                StoredLinkRepo(template)
        )

        template.execute("""insert into orders (id, order_constraints) values (1, '{"something": {"job_id": 42}}' FORMAT JSON)""")

        val result = OrderConstraintUtils(repo).getOrderConstraints(1L, "something", ::ignore)
        assertEquals(JobIdConstraintResult(42L), result)
    }

    @Test
    fun testGetOrderConstraintsLiteralInputs() {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = DatamanagementRepo(
                template,
                LiteralInputRepo(template),
                ComplexInputRepo(template),
                ComplexOutputAsInputRepo(template),
                ComplexInputAsValueRepo(template),
                BboxInputRepo(template),
                OrderJobRefRepo(template),
                ComplexOutputRepo(template),
                OrderRepo(template),
                StoredLinkRepo(template)
        )

        template.execute("""
            insert into orders (id, order_constraints) values
            (1, '{"something": {"literal_inputs": {"schema": ["SARA_v1.0", "HAZUS"]}}}' FORMAT JSON)""".trimIndent())

        val result = OrderConstraintUtils(repo).getOrderConstraints(1L, "something", ::ignore)
        assertEquals(
                OrderConstraintsResult(
                    mapOf("schema" to listOf("SARA_v1.0", "HAZUS")),
                    emptyMap(),
                    emptyMap()),
                result)
    }

    @Test
    fun testGetOrderConstraintsComplexInputs() {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = DatamanagementRepo(
                template,
                LiteralInputRepo(template),
                ComplexInputRepo(template),
                ComplexOutputAsInputRepo(template),
                ComplexInputAsValueRepo(template),
                BboxInputRepo(template),
                OrderJobRefRepo(template),
                ComplexOutputRepo(template),
                OrderRepo(template),
                StoredLinkRepo(template)
        )

        template.execute("""
            insert into orders (id, order_constraints) values
            (1, '{"something": {"complex_inputs": {"exposure": [{"link": "https://assetmaster/1", "mime_type": "application/json", "xmlschema": "", "encoding": "UTF-8"}, {"input_value": "{}", "mime_type": "application/json", "xmlschema": "", "encoding": "UTF-8"}]}}}' FORMAT JSON)""".trimIndent())

        val result = OrderConstraintUtils(repo).getOrderConstraints(1L, "something", ::ignore)
        assertEquals(
                OrderConstraintsResult(
                        emptyMap(),
                        mapOf(
                                "exposure" to mutableListOf(
                                        ComplexInputConstraint("https://assetmaster/1", null, "application/json", "", "UTF-8"),
                                        ComplexInputConstraint(null, "{}", "application/json", "", "UTF-8")
                                )
                        ),
                        emptyMap()),
                result)
    }

    @Test
    fun testGetOrderConstraintsBboxInputs() {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = DatamanagementRepo(
                template,
                LiteralInputRepo(template),
                ComplexInputRepo(template),
                ComplexOutputAsInputRepo(template),
                ComplexInputAsValueRepo(template),
                BboxInputRepo(template),
                OrderJobRefRepo(template),
                ComplexOutputRepo(template),
                OrderRepo(template),
                StoredLinkRepo(template)
        )

        template.execute("""
            insert into orders (id, order_constraints) values
            (1, '{"something": {"bbox_inputs": {"bbox": [{"lower_corner_x": -10, "lower_corner_y": -20, "upper_corner_x": -5, "upper_corner_y": -3, "crs": "epsg:4326"}, {"lower_corner_x": 3, "lower_corner_y": 5, "upper_corner_x": 50, "upper_corner_y": 30, "crs": "epsg:4326"}]}}}' FORMAT JSON)""".trimIndent())

        val result = OrderConstraintUtils(repo).getOrderConstraints(1L, "something", ::ignore)
        assertEquals(
                OrderConstraintsResult(
                        emptyMap(),
                        emptyMap(),
                        mapOf(
                            "bbox" to mutableListOf(
                                    BBoxInputConstraint(-10.0, -20.0, -5.0, -3.0, "epsg:4326"),
                                    BBoxInputConstraint(3.0, 5.0, 50.0, 30.0, "epsg:4326"),
                            )
                        )),
                result)
    }

}