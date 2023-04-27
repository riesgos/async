package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture

class ComplexInputAsValueRepoTest {
    @Test
    fun testFindByProcessWpsIdentifierJobStatusInputWpsIdentifierInputValueMimetypeXmlSchemaAndEncoding() {
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
                    insert into complex_inputs_as_values (job_id, wps_identifier, input_value, mime_type, xmlschema, encoding)
                    values (1, 'exposure', '{bla:12}', 'application/json', '', 'UTF-8')
                """.trimIndent()
        )

        val complexInputAsValueRepo = ComplexInputAsValueRepo(template)

        val result1 = complexInputAsValueRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierInputValueMimetypeXmlSchemaAndEncoding(
                assetmasterProcessIdentifier, "success", "exposure", "{bla:12}", "application/json", "", "UTF-8"
        )

        assertEquals(1, result1.size)
        assertEquals("{bla:12}", result1[0].inputValue)
    }
}