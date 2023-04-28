package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture
import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexOutputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput

class ComplexOutputRepoTest {
    @Test
    fun testBindByLinkMimetypeXmlschemaAndEncoding() {
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
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (1, 1, 'selectedRowsGeojson', 'https://assetmaster/1', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )

        val repo = ComplexOutputRepo(template)

        val result1 = repo.findByLinkMimetypeXmlschemaAndEncoding("https://assetmaster/1", "application/json", "", "UTF-8")
        assertEquals(1, result1.size)

        assertEquals(1, result1[0].id)
        assertEquals(1, result1[0].jobId)
        assertEquals("selectedRowsGeojson", result1[0].wpsIdentifier)
        assertEquals("https://assetmaster/1", result1[0].link)
        assertEquals("application/json", result1[0].mimeType)
        assertEquals("", result1[0].xmlschema)
        assertEquals("UTF-8", result1[0].encoding)

        val result0 = repo.findByLinkMimetypeXmlschemaAndEncoding("https://assetmaster/2", "application/json", "", "UTF-8")
        assertEquals(0, result0.size)
    }

    @Test
    fun testFindOptionalFirstByLinkMimetypeXmlschemaAndEncoding () {
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
                    insert into complex_outputs (id, job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (1, 1, 'selectedRowsGeojson', 'https://assetmaster/1', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )

        val repo = ComplexOutputRepo(template)

        val result1 = repo.findOptionalFirstByLinkMimetypeXmlschemaAndEncoding("https://assetmaster/1", "application/json", "", "UTF-8")
        assertTrue(result1 != null)
        assertEquals(1, result1!!.id)
        assertEquals(1, result1!!.jobId)
        assertEquals("selectedRowsGeojson", result1!!.wpsIdentifier)
        assertEquals("https://assetmaster/1", result1!!.link)
        assertEquals("application/json", result1!!.mimeType)
        assertEquals("", result1!!.xmlschema)
        assertEquals("UTF-8", result1!!.encoding)

        val result0 = repo.findOptionalFirstByLinkMimetypeXmlschemaAndEncoding("https://assetmaster/2", "application/json", "", "UTF-8")
        assertEquals(null, result0)
    }

    @Test
    fun testFindByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType() {
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

        val repo = ComplexOutputRepo(template)

        val result1 = repo.findByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(1L, assetmasterProcessIdentifier, "selectedRowsGeojson", "application/json")

        assertEquals(1, result1.size)

        assertEquals(1, result1[0].id)
        assertEquals(1, result1[0].jobId)
        assertEquals("selectedRowsGeojson", result1[0].wpsIdentifier)
        assertEquals("https://assetmaster/1", result1[0].link)
        assertEquals("application/json", result1[0].mimeType)
        assertEquals("", result1[0].xmlschema)
        assertEquals("UTF-8", result1[0].encoding)

        val result0 = repo.findByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType(2L, assetmasterProcessIdentifier, "selectedRowsGeojson", "application/json")

        assertEquals(0, result0.size)
    }

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

        val repo = ComplexOutputRepo(template)



        val complexOutputToSave = ComplexOutput(
                null, 1, "selectedRowsGeojson",
                "https://assetmaster/1", "application/json", "", "UTF-8")


        val savedComplexOutput = repo.persist(complexOutputToSave)
        assertTrue(savedComplexOutput.id != null)

        val complexOutputToUpdate = ComplexOutput(
                savedComplexOutput.id, savedComplexOutput.jobId, "selectedRowsGeojsonExteneded",
                savedComplexOutput.link, savedComplexOutput.mimeType, savedComplexOutput.xmlschema, savedComplexOutput.encoding)

        val updatedComplexOutput = repo.persist(complexOutputToUpdate)

        assertEquals(savedComplexOutput.id, updatedComplexOutput.id)

        val queryResult = template.query(
                """
                select *
                from complex_outputs
                where id = ${savedComplexOutput.id}""".trimIndent(),
                ComplexOutputRowMapper()
        )

        assertEquals(1, queryResult.size)
        assertEquals("selectedRowsGeojsonExteneded", queryResult[0].wpsIdentifier)
    }
}