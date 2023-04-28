package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture
import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexInput

class ComplexInputRepoTest {
    @Test
    fun testFindByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding() {
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
                    insert into complex_inputs (job_id, wps_identifier, link, mime_type, xmlschema, encoding)
                    values (1, 'exposure', 'http://web', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )

        val complexInputRepo = ComplexInputRepo(template)

        val result1 = complexInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding(
                assetmasterProcessIdentifier, "success", "exposure", "http://web", "application/json", "", "UTF-8"
        )

        assertEquals(1, result1.size)
        assertEquals("http://web", result1[0].link)

        val result0 = complexInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding(
                assetmasterProcessIdentifier, "success", "exposure2", "http://web", "application/json", "", "UTF-8"
        )

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
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (1, 1, 'success')
                """.trimIndent()
        )
        val complexInput = ComplexInput(null, 1, "exposure", "http://web", "application/json", "", "UTF-8")

        val complexInputRepo = ComplexInputRepo(template)
        val complexInputSaved = complexInputRepo.persist(complexInput)

        assertTrue(complexInputSaved.id != null)

        val complexInputUpdate = ComplexInput(complexInputSaved.id, complexInputSaved.jobId, "other", complexInputSaved.link, complexInputSaved.mimeType, complexInputSaved.xmlschema, complexInputSaved.encoding)
        val complexInputAfterUpdate = complexInputRepo.persist(complexInputUpdate)

        val queryResult = template.query("select * from complex_inputs where id = ?", ComplexInputRowMapper(), complexInputAfterUpdate.id)
        assertEquals(1, queryResult.size)
        assertEquals("other", queryResult[0].wpsIdentifier)
    }
}