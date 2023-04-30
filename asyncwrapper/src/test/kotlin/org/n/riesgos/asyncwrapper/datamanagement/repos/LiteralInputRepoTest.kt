package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture
import org.n.riesgos.asyncwrapper.datamanagement.mapper.LiteralInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.LiteralInput

class LiteralInputRepoTest {
    @Test
    fun testFindByProcessWpsIdentifierJobStatusInputWpsIdentifierAndValue() {
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
                    insert into literal_inputs (id, job_id, wps_identifier, input_value)
                    values (1, 1, 'schema', 'HAZUS')
                """.trimIndent()
        )

        val repo = LiteralInputRepo(template)

        val result1 = repo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierAndValue(assetmasterProcessIdentifier, "success", "schema", "HAZUS")
        assertEquals(1, result1.size)

        assertEquals(1, result1[0].id)
        assertEquals(1, result1[0].jobId)
        assertEquals("schema", result1[0].wpsIdentifier)
        assertEquals("HAZUS", result1[0].inputValue)

        val result0 = repo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierAndValue(assetmasterProcessIdentifier, "success", "schema", "SARA_v1.0")
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

        val repo = LiteralInputRepo(template)

        val literalInputToBeSaved = LiteralInput(null, 1, "schema", "HAZUS")
        val savedLiteralInput = repo.persist(literalInputToBeSaved)

        assertTrue(savedLiteralInput.id != null)

        val literalInputToBeUpdated = LiteralInput(savedLiteralInput.id, savedLiteralInput.jobId, savedLiteralInput.wpsIdentifier, "SARA_v1.0")
        val updatedLiteralInput = repo.persist(literalInputToBeUpdated)

        val queryResult = template.query("select * from literal_inputs where id = ?", LiteralInputRowMapper(), savedLiteralInput.id)
        assertEquals(1, queryResult.size)
        assertEquals("SARA_v1.0", queryResult[0].inputValue)
    }
}