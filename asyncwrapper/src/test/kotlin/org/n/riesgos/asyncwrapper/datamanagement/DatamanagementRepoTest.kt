package org.n.riesgos.asyncwrapper.datamanagement

import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.mapper.*
import org.n.riesgos.asyncwrapper.datamanagement.models.*
import org.n.riesgos.asyncwrapper.datamanagement.repos.*
import org.springframework.test.annotation.DirtiesContext
import java.util.*

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
    fun testAddJobToOrder() {
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

    @Test
    fun testFindComplexOutputsByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType() {
        val template = H2DbFixture().getJdbcTemplate()
        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        // We skip the order constraints here.
        template.execute(
                """
                    insert into orders (id)
                    values (1)
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
                    insert into order_job_refs (id, job_id, order_id)
                    values (1, 1, 1)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (1, 1, 'selectedRowsGeojson', 'https://assetmaster/1', 'application/json', '', 'UTF-8')
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

        val result1 = datamanagementRepo.findComplexOutputsByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(1L, assetmasterProcessIdentifier, "selectedRowsGeojson", "application/json")

        assertEquals(1, result1.size)

        assertEquals(1, result1[0].id)
        assertEquals(1, result1[0].jobId)
        assertEquals("selectedRowsGeojson", result1[0].wpsIdentifier)
        assertEquals("https://assetmaster/1", result1[0].link)
        assertEquals("application/json", result1[0].mimeType)
        assertEquals("", result1[0].xmlschema)
        assertEquals("UTF-8", result1[0].encoding)

        val result0 = datamanagementRepo.findComplexOutputsByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(2L, assetmasterProcessIdentifier, "selectedRowsGeojson", "application/json")

        assertEquals(0, result0.size)
    }

    @Test
    fun testComplexOutputs() {
        val template = H2DbFixture().getJdbcTemplate()
        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        // We skip the order constraints here.
        template.execute(
                """
                    insert into orders (id)
                    values (1)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (1, 1, 'success')
                """.trimIndent()
        )
        template.execute("""
            insert into order_job_refs (id, order_id, job_id)
            values (1, 1, 1)
        """.trimIndent())
        template.execute("""
            insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
            values (1, 1, 'selectedRowsGeojson', 'https://assetmaster/1', 'application/json', '', 'UTF-8')
        """.trimIndent())

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

        val result1 = repo.complexOutputs(1L, assetmasterProcessIdentifier, "selectedRowsGeojson")
        assertEquals(1, result1.size)

        assertEquals(1, result1[0].id)
        assertEquals(1, result1[0].jobId)
        assertEquals("selectedRowsGeojson", result1[0].wpsIdentifier)
        assertEquals("https://assetmaster/1", result1[0].link)
        assertEquals("application/json", result1[0].mimeType)
        assertEquals("", result1[0].xmlschema)
        assertEquals("UTF-8", result1[0].encoding)

        val result0 = repo.complexOutputs(1L, assetmasterProcessIdentifier, "something different")
        assertEquals(0, result0.size)
    }

    @Test
    fun testFindProcessIdOrInsert() {
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

        val startCount = template.queryForObject("select count(*) from processes", Int::class.javaObjectType)
        assertEquals(0, startCount)

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"

        val newProcessId = repo.findProcessIdOrInsert(gfzWpsUrl, assetmasterProcessIdentifier)
        assertTrue(newProcessId != null)

        val selectCount = template.queryForObject("""
            select count(*)
            from processes
            where processes.id = ${newProcessId}
            and processes.wps_url = '${gfzWpsUrl}'
            and processes.wps_identifier = '${assetmasterProcessIdentifier}'
            """.trimIndent(), Int::class.javaObjectType)
        assertEquals(1, selectCount)

        val existingProcessId = repo.findProcessIdOrInsert(gfzWpsUrl, assetmasterProcessIdentifier)
        assertEquals(newProcessId, existingProcessId)

        val midCount = template.queryForObject("select count(*) from processes", Int::class.javaObjectType)
        assertEquals(1, midCount)

        val nextProcessId = repo.findProcessIdOrInsert(gfzWpsUrl, "org.n52.gfz.riesgos.algorithm.impl.DeusProcess")
        assertTrue(nextProcessId !=  existingProcessId)
    }

    @Test
    fun testCreateJob() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )

        val startCount = template.queryForObject("select count(*) from jobs", Int::class.javaObjectType)
        assertEquals(0, startCount)

        val jobId = repo.createJob(1L, "pending")

        val selectCount = template.queryForObject("""
            select count(*)
            from jobs
            where jobs.id = ${jobId}
            and jobs.process_id = 1
            and jobs.status = 'pending'
            """.trimIndent(), Int::class.javaObjectType)
        assertEquals(1, selectCount)

        val nextJobId = repo.createJob(1L, "pending")
        assertTrue(jobId != nextJobId)

        val endCount = template.queryForObject("select count(*) from jobs", Int::class.javaObjectType)
        assertEquals(2, endCount)
    }

    @Test
    fun testInsertLiteralInput() {
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


        val savedLiteralInput = repo.insertLiteralInput(1, "schema", "HAZUS")
        assertTrue(savedLiteralInput.id != null)

        val queryResult = template.query("select * from literal_inputs where id = ?", LiteralInputRowMapper(), savedLiteralInput.id)
        assertEquals(1, queryResult.size)
        assertEquals("HAZUS", queryResult[0].inputValue)
    }

    @Test
    fun testInsertComplexInput() {
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
        val complexInputSaved = repo.insertComplexInput(1L, "exposure", ComplexInputConstraint("http://web", null, "application/json", "", "UTF-8"))

        assertTrue(complexInputSaved.id != null)

        val queryResult = template.query("select * from complex_inputs where id = ?", ComplexInputRowMapper(), complexInputSaved.id)
        assertEquals(1, queryResult.size)
        assertEquals("exposure", queryResult[0].wpsIdentifier)
    }

    @Test
    fun testInsertBboxInput() {
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

        val bboxAfterSave = repo.insertBboxInput(1, "bbox", BBoxInputConstraint(-10.0, -20.0, 30.0, 0.0, "epsg:4326"))
        assertTrue(bboxAfterSave.id != null)

        val queryResult = template.query(
                """select * from bbox_inputs where id = ${bboxAfterSave.id}""", BboxInputRowMapper()
        )
        assertEquals(1, queryResult.size)
        assertEquals(bboxAfterSave.id, queryResult[0].id)
        assertEquals("bbox", bboxAfterSave.wpsIdentifier)
    }

    @Test
    fun testInsertComplexInputAsValue() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.DeusProcess"
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${deusProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (1, 1, 'success')
                """.trimIndent()
        )

        val complexInput = repo.insertComplexInputAsValue(1L, "exposure", ComplexInputConstraint(null, "{bla: 42}", "application/json", "", "UTF-8"))
        assertTrue(complexInput.id != null)
        assertEquals(1L, complexInput.jobId)
        assertEquals("exposure", complexInput.wpsIdentifier)
        assertEquals("{bla: 42}", complexInput.inputValue)
        assertEquals("application/json", complexInput.mimeType)
        assertEquals("", complexInput.xmlschema)
        assertEquals("UTF-8", complexInput.encoding)
    }

    @Test
    fun testFindOptionalExistingComplexOutputToUseAsInput() {
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
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (1, 1, 'selectedRowsGeojson', 'https://assetmaster/1', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )


        val result1 = repo.findOptionalExistingComplexOutputToUseAsInput(ComplexInputConstraint("https://assetmaster/1", null, "application/json", "", "UTF-8"))
        assertTrue(result1 != null)
        assertEquals(1, result1!!.id)
        assertEquals(1, result1!!.jobId)
        assertEquals("selectedRowsGeojson", result1!!.wpsIdentifier)
        assertEquals("https://assetmaster/1", result1!!.link)
        assertEquals("application/json", result1!!.mimeType)
        assertEquals("", result1!!.xmlschema)
        assertEquals("UTF-8", result1!!.encoding)

        val result0 = repo.findOptionalExistingComplexOutputToUseAsInput(ComplexInputConstraint("https://assetmaster/2", null, "application/json", "", "UTF-8"))
        assertEquals(null, result0)
    }

    @Test
    fun testInsertComplexOutputAsInput() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.DeusProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (2, '${gfzWpsUrl}', '${deusProcessIdentifier}')
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
                    insert into jobs (id, process_id, status)
                    values (2, 2, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (1, 1, 'selectedRowsGeojson', 'https://assetmaster/1', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )

        val complexOutputAsInput = ComplexOutputAsInput(null, 2, "exposure", ComplexOutput(
                1, 1, "selectedRowsGeojson",
                "https://assetmaster/1", "application/json", "", "UTF-8")
        )


        val savedComplexOutputAsInput = repo.insertComplexOutputAsInput(1L, ComplexOutput(
                1, 1, "selectedRowsGeojson",
                "https://assetmaster/1", "application/json", "", "UTF-8"),
                "exposure")
        assertTrue(savedComplexOutputAsInput.id != null)


        val queryResult = template.query(
                """
                select
                    complex_outputs_as_inputs.id,
                    complex_outputs_as_inputs.job_id,
                    complex_outputs_as_inputs.wps_identifier,
                    complex_outputs_as_inputs.complex_output_id,
                    complex_outputs.job_id as output_job_id,
                    complex_outputs.wps_identifier as output_wps_identifier,
                    complex_outputs.link as output_link,
                    complex_outputs.mime_type as output_mime_type,
                    complex_outputs.xmlschema as output_xmlschema,
                    complex_outputs.encoding as output_encoding
                from complex_outputs_as_inputs
                join complex_outputs on complex_outputs.id = complex_outputs_as_inputs.complex_output_id
                where complex_outputs_as_inputs.id = ${savedComplexOutputAsInput.id}""".trimIndent(),
                ComplexOutputAsInputRowMapper()
        )

        assertEquals(1, queryResult.size)
        assertEquals("exposure", queryResult[0].wpsIdentifier)
    }

    @Test
    fun testUpdateJobStatus() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.DeusProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (2, '${gfzWpsUrl}', '${deusProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (1, 1, 'pending')
                """.trimIndent()
        )
        repo.updateJobStatus(1L, "failed")

        val jobStatus = template.queryForObject("select status from jobs where id = 1", String::class.javaObjectType)
        assertEquals("failed", jobStatus)
    }

    @Test
    fun testSetJobExceptionReport() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.DeusProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (2, '${gfzWpsUrl}', '${deusProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (1, 1, 'pending')
                """.trimIndent()
        )
        repo.setJobExceptionReport(1L, "Something bad happened")

        val exceptionReport = template.queryForObject("select exception_report from jobs where id = 1", String::class.javaObjectType)
        assertEquals("Something bad happened", exceptionReport)
    }

    @Test
    fun testInsertComplexOutput() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        // We skip the order constraints here.
        template.execute(
                """
                    insert into orders (id)
                    values (1)
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
                    insert into order_job_refs (id, job_id, order_id)
                    values (1, 1, 1)
                """.trimIndent()
        )

        val savedComplexOutput = repo.insertComplexOutput(1, "selectedRowsGeojson", "https://assetmaster/1",
                "application/json", "", "UTF-8")
        assertTrue(savedComplexOutput.id != null)


        val queryResult = template.query(
                """
                select *
                from complex_outputs
                where id = ${savedComplexOutput.id}""".trimIndent(),
                ComplexOutputRowMapper()
        )

        assertEquals(1, queryResult.size)
        assertEquals("selectedRowsGeojson", queryResult[0].wpsIdentifier)
    }

    @Test
    fun testJobIdHasAlreadyProcessedWithAssetmasterNotprocessed() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )

        val complexInputs = emptyMap<String, ComplexInputConstraint>()
        val literalInputs = mapOf("schema" to "SARA_v1.0", "querymode" to "intersect")
        val bboxInputs = emptyMap<String, BBoxInputConstraint>()
        val optionalJobId = repo.jobIdHasAlreadyProcessed(assetmasterProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(optionalJobId, Optional.empty<Long>())
    }

    @Test
    fun testJobIdHasAlreadyProcessedWithAssetmasterProcessed() {
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
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (1, 1, 'schema', 'SARA_v1.0')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (2, 1, 'querymode', 'intersect')
                """.trimIndent()
        )

        val complexInputs = emptyMap<String, ComplexInputConstraint>()
        val literalInputs = mapOf("schema" to "SARA_v1.0", "querymode" to "intersect")
        val bboxInputs = emptyMap<String, BBoxInputConstraint>()
        val optionalJobId = repo.jobIdHasAlreadyProcessed(assetmasterProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(optionalJobId, Optional.of(1L))
    }

    @Test
    fun testJobIdHasAlreadyProcessedWithAssetmasterProcessedInputTooMuch() {
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
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (1, 1, 'schema', 'SARA_v1.0')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (2, 1, 'querymode', 'intersect')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (3, 1, 'additional', 'different')
                """.trimIndent()
        )

        val complexInputs = emptyMap<String, ComplexInputConstraint>()
        val literalInputs = mapOf("schema" to "SARA_v1.0", "querymode" to "intersect")
        val bboxInputs = emptyMap<String, BBoxInputConstraint>()
        val optionalJobId = repo.jobIdHasAlreadyProcessed(assetmasterProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(Optional.empty<Long>(), optionalJobId)
    }

    @Test
    fun testJobIdHasAlreadyProcessedWithAssetmasterProcessedFailed() {
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
                    values (1, 1, 'failed')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (1, 1, 'schema', 'SARA_v1.0')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (2, 1, 'querymode', 'intersect')
                """.trimIndent()
        )

        val complexInputs = emptyMap<String, ComplexInputConstraint>()
        val literalInputs = mapOf("schema" to "SARA_v1.0", "querymode" to "intersect")
        val bboxInputs = emptyMap<String, BBoxInputConstraint>()
        val optionalJobId = repo.jobIdHasAlreadyProcessed(assetmasterProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(optionalJobId, Optional.empty<Long>())
    }

    @Test
    fun testJobIdHasAlreadyProcessedWithAssetmasterProcessedDifferentSchema() {
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
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (1, 1, 'schema', 'HAZUS')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (2, 1, 'querymode', 'intersect')
                """.trimIndent()
        )

        val complexInputs = emptyMap<String, ComplexInputConstraint>()
        val literalInputs = mapOf("schema" to "SARA_v1.0", "querymode" to "intersect")
        val bboxInputs = emptyMap<String, BBoxInputConstraint>()
        val optionalJobId = repo.jobIdHasAlreadyProcessed(assetmasterProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(optionalJobId, Optional.empty<Long>())
    }

    @Test
    fun testJobIdHasAlreadyProcessedWithDeusNotprocessed() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.DeusProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${deusProcessIdentifier}')
                """.trimIndent()
        )

        val complexInputs = mapOf(
                "exposure" to ComplexInputConstraint("https://assetmaster/1", null, "application/json", "", "UTF-8"),
                "fragility" to ComplexInputConstraint("https://modelprop/2", null, "application/json", "", "UTF-8"),
                "intensity" to ComplexInputConstraint("https://shakyground/3", null, "text/xml", "", "UTF-8")
        )
        val literalInputs = mapOf("schema" to "SARA_v1.0")
        val bboxInputs = emptyMap<String, BBoxInputConstraint>()
        val optionalJobId = repo.jobIdHasAlreadyProcessed(deusProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(optionalJobId, Optional.empty<Long>())
    }

    @Test
    fun testJobIdHasAlreadyProcessedWithDeusProcessed() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"
        val modelpropProcessIdentifier = "org.n52.gfz.riesgos.algorithm.ModelpropProcess"
        val shakygroundProcessIdentifier = "org.n52.gfz.riesgos.algorithm.ShakygroundProcessIdentifier"
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.DeusProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (2, '${gfzWpsUrl}', '${modelpropProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (3, '${gfzWpsUrl}', '${shakygroundProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (4, '${gfzWpsUrl}', '${deusProcessIdentifier}')
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
                    insert into jobs (id, process_id, status)
                    values (2, 2, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (3, 3, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (4, 4, 'success')
                """.trimIndent()
        )
        // Assetmaster output & deus input
        template.execute(
                """
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (1, 1, 'selectedRowsGeojson','https://assetmaster/1', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (1, 4, 'exposure', 1)
                """.trimIndent()
        )
        // modelprop output & deus input
        template.execute(
                """
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (2, 2, 'selectedRows','https://modelprop/2', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (2, 4, 'fragility', 2)
                """.trimIndent()
        )
        // shakyground output & deus input
        template.execute(
                """
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (3, 3, 'shakemap','https://shakyground/3', 'text/xml', '', 'UTF-8')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (3, 4, 'intensity', 3)
                """.trimIndent()
        )
        // and the literal input for deus
        template.execute(
                """
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (1, 4, 'schema', 'SARA_v1.0')
                """.trimIndent()
        )

        val complexInputs = mapOf(
                "exposure" to ComplexInputConstraint("https://assetmaster/1", null, "application/json", "", "UTF-8"),
                "fragility" to ComplexInputConstraint("https://modelprop/2", null, "application/json", "", "UTF-8"),
                "intensity" to ComplexInputConstraint("https://shakyground/3", null, "text/xml", "", "UTF-8")
        )
        val literalInputs = mapOf("schema" to "SARA_v1.0")
        val bboxInputs = emptyMap<String, BBoxInputConstraint>()
        val optionalJobId = repo.jobIdHasAlreadyProcessed(deusProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(optionalJobId, Optional.of(4L))
    }

    @Test
    fun testJobIdHasAlreadyProcessedWithDeusProcessedDifferentLink() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"
        val modelpropProcessIdentifier = "org.n52.gfz.riesgos.algorithm.ModelpropProcess"
        val shakygroundProcessIdentifier = "org.n52.gfz.riesgos.algorithm.ShakygroundProcessIdentifier"
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.DeusProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${assetmasterProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (2, '${gfzWpsUrl}', '${modelpropProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (3, '${gfzWpsUrl}', '${shakygroundProcessIdentifier}')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (4, '${gfzWpsUrl}', '${deusProcessIdentifier}')
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
                    insert into jobs (id, process_id, status)
                    values (2, 2, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (3, 3, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (4, 4, 'success')
                """.trimIndent()
        )
        // Assetmaster output & deus input
        template.execute(
                """
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (1, 1, 'selectedRowsGeojson','https://assetmaster/1', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (1, 4, 'exposure', 1)
                """.trimIndent()
        )
        // modelprop output & deus input
        template.execute(
                """
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (2, 2, 'selectedRows','https://modelprop/2', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (2, 4, 'fragility', 2)
                """.trimIndent()
        )
        // shakyground output & deus input
        template.execute(
                """
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (3, 3, 'shakemap','https://shakyground/3', 'text/xml', '', 'UTF-8')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (3, 4, 'intensity', 3)
                """.trimIndent()
        )
        // and the literal input for deus
        template.execute(
                """
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (1, 4, 'schema', 'SARA_v1.0')
                """.trimIndent()
        )

        val complexInputs = mapOf(
                "exposure" to ComplexInputConstraint("https://assetmaster/1", null, "application/json", "", "UTF-8"),
                "fragility" to ComplexInputConstraint("https://modelprop/2", null, "application/json", "", "UTF-8"),
                "intensity" to ComplexInputConstraint("https://shakyground/33", null, "text/xml", "", "UTF-8")
        )
        val literalInputs = mapOf("schema" to "SARA_v1.0")
        val bboxInputs = emptyMap<String, BBoxInputConstraint>()
        val optionalJobId = repo.jobIdHasAlreadyProcessed(deusProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(optionalJobId, Optional.empty<Long>())
    }

    @Test
    fun testJobIdHasAlreadyProcessedWithQuakeledgerProcessed() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val quakeledgerProcessIdentifier = "org.n52.gfz.riesgos.algorithm.QuakeledgerProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${quakeledgerProcessIdentifier}')
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
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (1, 1, 'type', 'stochastic')
                """.trimIndent()
        )

        template.execute(
                """
                    insert into bbox_inputs (id, job_id, wps_identifier, lower_corner_x, lower_corner_y, upper_corner_x, upper_corner_y, crs)
                    values (1, 1, 'bbox', -10.0, -20.0, 30.0, 0.0, 'epsg:4326')
                """.trimIndent()
        )

        val complexInputs = emptyMap<String, ComplexInputConstraint>()
        val literalInputs = mapOf("type" to "stochastic")
        val bboxInputs = mapOf("bbox" to BBoxInputConstraint(-10.0, -20.0, 30.0, 0.0, "epsg:4326"))
        val optionalJobId = repo.jobIdHasAlreadyProcessed(quakeledgerProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(Optional.of(1L), optionalJobId)
    }


    @Test
    fun testJobIdHasAlreadyProcessedWithQuakeledgerProcessedDifferentBbox() {
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

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val quakeledgerProcessIdentifier = "org.n52.gfz.riesgos.algorithm.QuakeledgerProcess"

        template.execute(
                """
                    insert into processes (id, wps_url, wps_identifier)
                    values (1, '${gfzWpsUrl}', '${quakeledgerProcessIdentifier}')
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
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (1, 1, 'type', 'stochastic')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into bbox_inputs (id, job_id, wps_identifier, lower_corner_x, lower_corner_y, upper_corner_x, upper_corner_y, crs)
                    values (1, 1, 'bbox', -10.0, -20.0, 30.0, 0.0, 'egsp:4326')
                """.trimIndent()
        )

        val complexInputs = emptyMap<String, ComplexInputConstraint>()
        val literalInputs = mapOf("type" to "stochastic")
        val bboxInputs = mapOf("bbox" to BBoxInputConstraint(-10.0, -20.0, 130.0, 0.0, "epsg:4326"))
        val optionalJobId = repo.jobIdHasAlreadyProcessed(quakeledgerProcessIdentifier, "success", complexInputs, literalInputs, bboxInputs)

        assertEquals(Optional.empty<Long>(), optionalJobId)
    }
}