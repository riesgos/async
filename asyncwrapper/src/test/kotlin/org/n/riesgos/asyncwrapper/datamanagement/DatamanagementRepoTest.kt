package org.n.riesgos.asyncwrapper.datamanagement

import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInputConstraint
import org.n.riesgos.asyncwrapper.datamanagement.repos.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext

/**
 * You can run this test with
 * docker-compose run mvn mvn test -Dtest=DatamanagementRepoTest
 *
 * But it is not yet working as it should.
 * It doesn't cleanup the embedded database on each run - and the h2 has
 * some trouble when I try to use the persist methods in the repo classes.
 *
 * However, I can do some cleanup in my test logic here - and I can
 * test the simple queries against the database.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DatamanagementRepoTest {

    @Test
    fun testDatabaseStructure() {
        val template = H2DbFixture().getJdbcTemplate()
        template.execute("select * from literal_inputs")
    }

    @Test
    fun testFindLiteralInputsForComplexOutput() {
        val template = H2DbFixture().getJdbcTemplate()

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val modelpropProcessIdentifier = "org.n52.gfz.riesgos.algorithm.ModelpropProcess"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"

        template.execute(
                """
                insert into processes(id, wps_url, wps_identifier)
                values (1, '${gfzWpsUrl}', '${modelpropProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                insert into processes(id, wps_url, wps_identifier)
                values (2, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        // first call of modelprop
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (1, 1, 'success')
                """.trimIndent()
        )
        val firstModelpropSchema = "HAZUS"
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (1, 'schema', '${firstModelpropSchema}', 1)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (2, 'assetcategory', 'buildings', 1)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (1, 'selectedRows', 'application/json', 'UTF-8', '', 1, 'https://rz-vm140.gfz-potsdam.de/wps/results/modelprop/1')
                """.trimIndent()
        )
        // second call of modelprop
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (2, 1, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (3, 'schema', 'SUPPASRI2023_v2.0', 2)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (4, 'assetcategory', 'buildings', 2)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (2, 'selectedRows', 'application/json', 'UTF-8', '', 2, 'https://rz-vm140.gfz-potsdam.de/wps/results/modelprop/2')
                """.trimIndent()
        )
        // The call of assetmaster
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (3, 2, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (5, 'schema', 'SARA_v1.0', 3)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (6, 'querymode', 'intersects', 3)
                """.trimIndent()
        )
        val assetmasterOutputLink = "https://rz-vm140.gfz-potsdam.de/wps/results/assetmaster/3"
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (3, 'selectedRowsGeoJson', 'application/json', 'UTF-8', '', 3, '${assetmasterOutputLink}')
                """.trimIndent()
        )

        val datamanagementRepo = DatamanagementRepo(
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

        val literalInputs = datamanagementRepo.findLiteralInputsForComplexOutput(ComplexInputConstraint(
                assetmasterOutputLink, null, "application/json", "", "UTF-8"
        ), assetmasterProcessIdentifier, "schema")

        assertEquals(literalInputs.size, 1)
        assertEquals(literalInputs[0].inputValue, "SARA_v1.0")
    }

    @Test
    fun testFindLiteralInputsForParentProcessOfComplexOutput() {
        val template = H2DbFixture().getJdbcTemplate()

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val modelpropProcessIdentifier = "org.n52.gfz.riesgos.algorithm.ModelpropProcess"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.DeusProcess"


        template.execute(
                """
                insert into processes(id, wps_url, wps_identifier)
                values (1, '${gfzWpsUrl}', '${modelpropProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                insert into processes(id, wps_url, wps_identifier)
                values (2, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                insert into processes(id, wps_url, wps_identifier)
                values (3, '${gfzWpsUrl}', '${deusProcessIdentifier}')
                """.trimIndent()
        )
        // first call of modelprop
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (1, 1, 'success')
                """.trimIndent()
        )
        val firstModelpropSchema = "HAZUS"
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (1, 'schema', '${firstModelpropSchema}', 1)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (2, 'assetcategory', 'buildings', 1)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (1, 'selectedRows', 'application/json', 'UTF-8', '', 1, 'https://rz-vm140.gfz-potsdam.de/wps/results/modelprop/1')
                """.trimIndent()
        )
        // second call of modelprop
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (2, 1, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (3, 'schema', 'SUPPASRI2023_v2.0', 2)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (4, 'assetcategory', 'buildings', 2)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (2, 'selectedRows', 'application/json', 'UTF-8', '', 2, 'https://rz-vm140.gfz-potsdam.de/wps/results/modelprop/2')
                """.trimIndent()
        )
        // The call of assetmaster
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (3, 2, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (5, 'schema', 'SARA_v1.0', 3)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (6, 'querymode', 'intersects', 3)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (3, 'selectedRowsGeoJson', 'application/json', 'UTF-8', '', 3, 'https://rz-vm140.gfz-potsdam.de/wps/results/assetmaster/3')
                """.trimIndent()
        )
        // And the deus earthquake job
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (4, 3, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (7, 'schema', 'SARA_v1.0', 4)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (1, 4, 'exposure', 3)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (2, 4, 'fragility', 1)
                """.trimIndent()
        )
        val deusOutputLink = "https://rz-vm140.gfz-potsdam.de/wps/results/deus/4"
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (4, 'merged_output', 'application/json', 'UTF-8', '', 4, '${deusOutputLink}')
                """.trimIndent()
        )

        val datamanagementRepo = DatamanagementRepo(
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
        val literalInputs = datamanagementRepo.findLiteralInputsForParentProcessOfComplexOutput(modelpropProcessIdentifier, deusOutputLink)
        assertEquals(literalInputs.size, 2)
        val schema = literalInputs.filter { it.wpsIdentifier == "schema" }.map{ it.inputValue }.get(0)
        assertEquals(schema, firstModelpropSchema)
    }

    @Test
    fun testOrderConstraints() {
        val template = H2DbFixture().getJdbcTemplate()
        val datamanagementRepo = DatamanagementRepo(
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

        val noOrderConstraints = datamanagementRepo.orderConstraints(9999L)
        assertEquals(null, noOrderConstraints)

        // The format json is needed due to the H2 restrictions.
        // See https://h2database.com/html/datatypes.html#json_type
        template.execute("""
            insert into orders (id, order_constraints)
            values (1, '{"answer": 42}' FORMAT JSON)
        """.trimIndent())


        val existingOrderConstraints = datamanagementRepo.orderConstraints(1L)
        assertTrue(existingOrderConstraints != null)

        assertTrue(existingOrderConstraints != null)

        val expectedOrders = JSONObject()
        expectedOrders.put("answer", 42)

        assertEquals(expectedOrders.keySet(), existingOrderConstraints!!.keySet())
        assertEquals(expectedOrders.get("answer"), existingOrderConstraints!!.get("answer"))
    }

    @Test
    fun testHasJob() {
        val template = H2DbFixture().getJdbcTemplate()
        val datamanagementRepo = DatamanagementRepo(
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val modelpropProcessIdentifier = "org.n52.gfz.riesgos.algorithm.ModelpropProcess"

        template.execute(
                """
                insert into processes(id, wps_url, wps_identifier)
                values (1, '${gfzWpsUrl}', '${modelpropProcessIdentifier}')
                """.trimIndent()
        )
        assertEquals(false, datamanagementRepo.hasJob(1L))
        // first call of modelprop
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (1, 1, 'success')
                """.trimIndent()
        )
        assertEquals(true, datamanagementRepo.hasJob(1L))
    }


    @Test
    fun testAddJobToOrder () {
        val template = H2DbFixture().getJdbcTemplate()

        val datamanagementRepo = DatamanagementRepo(
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


        datamanagementRepo.addJobToOrder(1, 1)

        val countOrder1 = template.queryForObject("select count(*) from order_job_refs where order_id = 1", Int::class.javaObjectType)
        assertEquals(1, countOrder1)
    }

}