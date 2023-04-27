package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture
import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexOutputAsInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutputAsInput

class ComplexOutputAsInputRepoTest {
    @Test
    fun testFindByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding() {
        val template = H2DbFixture().getJdbcTemplate()

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
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (1, 2, 'exposure', 1)
                """.trimIndent()
        )

        val complexOutputAsInputRepo = ComplexOutputAsInputRepo(template)

        val result1 = complexOutputAsInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding(
                deusProcessIdentifier, "success", "exposure", "https://assetmaster/1", "application/json", "", "UTF-8"
        )

        assertEquals(1, result1.size)
        val entry = result1[0]

        assertEquals(1, entry.id)
        assertEquals(2, entry.jobId)
        assertEquals("exposure", entry.wpsIdentifier)
        assertEquals(1, entry.complexOutput.id)
        assertEquals(1, entry.complexOutput.jobId)
        assertEquals("https://assetmaster/1", entry.complexOutput.link)
        assertEquals("selectedRowsGeojson", entry.complexOutput.wpsIdentifier)
        assertEquals("application/json", entry.complexOutput.mimeType)
        assertEquals("", entry.complexOutput.xmlschema)
        assertEquals("UTF-8", entry.complexOutput.encoding)

        val result0 = complexOutputAsInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierLinkMimetypeXmlSchemaAndEncoding(
                deusProcessIdentifier, "success", "exposure2", "https://assetmaster/1", "application/json", "", "UTF-8"
        )

        assertEquals(0, result0.size)
    }

    @Test
    fun testPersist() {
        val template = H2DbFixture().getJdbcTemplate()

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

        val complexOutputAsInputRepo = ComplexOutputAsInputRepo(template)

        val savedComplexOutputAsInput = complexOutputAsInputRepo.persist(complexOutputAsInput)
        assertTrue(savedComplexOutputAsInput.id != null)

        val complexOutputAsInputToUpdate = ComplexOutputAsInput(
                savedComplexOutputAsInput.id,
                savedComplexOutputAsInput.jobId,
                "exposure-update",
                savedComplexOutputAsInput.complexOutput
        )

        val updatedComplexOutputAsInput = complexOutputAsInputRepo.persist(complexOutputAsInputToUpdate)

        assertEquals(savedComplexOutputAsInput.id, updatedComplexOutputAsInput.id)

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
        assertEquals("exposure-update", queryResult[0].wpsIdentifier)
    }

    @Test
    fun testFindInputsByJobId() {
        val template = H2DbFixture().getJdbcTemplate()

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
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (1, 2, 'exposure', 1)
                """.trimIndent()
        )

        val complexOutputAsInputRepo = ComplexOutputAsInputRepo(template)

        val result1 = complexOutputAsInputRepo.findInputsByJobId(2L)

        assertEquals(1, result1.size)
        val entry = result1[0]

        assertEquals(1, entry.id)
        assertEquals(2, entry.jobId)
        assertEquals("exposure", entry.wpsIdentifier)
        assertEquals(1, entry.complexOutput.id)
        assertEquals(1, entry.complexOutput.jobId)
        assertEquals("https://assetmaster/1", entry.complexOutput.link)
        assertEquals("selectedRowsGeojson", entry.complexOutput.wpsIdentifier)
        assertEquals("application/json", entry.complexOutput.mimeType)
        assertEquals("", entry.complexOutput.xmlschema)
        assertEquals("UTF-8", entry.complexOutput.encoding)

        val result0 = complexOutputAsInputRepo.findInputsByJobId(1L)
        assertEquals(0, result0.size)


    }
}