package org.n.riesgos.asyncwrapper.dummy.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.config.FilestorageConfig
import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.datamanagement.repos.*
import org.n.riesgos.asyncwrapper.dummy.AssetmasterWrapper.Companion.WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS
import org.n.riesgos.asyncwrapper.dummy.DeusTsWrapper.Companion.WPS_PROCESS_IDENTIFIER_ASSETMASTER
import org.n.riesgos.asyncwrapper.dummy.DeusTsWrapper.Companion.WPS_PROCESS_IDENTIFIER_MODELPROP
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.test.annotation.DirtiesContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * This is the test case for the deus tsunami wrapper.
 *
 * It has the same limitations as the DatamanagementRepoTest.
 *
 * You can run it with
 * docker-compose run mvn mvn test -Dtest=DeusUtilsTest
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DeusUtilsTest {

    private var jdbcTemplate: JdbcTemplate? = null

    @BeforeEach
    fun setUp() {
        val dataSource = EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build()

        val folderOfMigrationFiles = File("/usr/src/backend/migrations")
        val files = folderOfMigrationFiles.listFiles()
        Arrays.sort(files)
        Assertions.assertTrue(files.size > 0)
        val template = JdbcTemplate(dataSource)


        // This is here is the work around with the problem that we don't
        // get a clean state for the second run.
        // So we check if we need to run the migrations.
        var tablesExists = false
        try {
            template.execute("select * from literal_inputs")
            tablesExists = true
        } catch (ex: Exception) {
            tablesExists = false
        }
        if (!tablesExists) {
            for (file in files) {
                val bytes = Files.readAllBytes(Paths.get(file.toURI()))
                // H2 can't handle jsonb columns. But there is a json datatype instead.
                val text = String(bytes).replace("jsonb", "json")
                val commands = text.split(";")
                for (command in commands) {
                    template.execute(command)
                }
            }
        } else {
            // In case the migrations are already done, then we cleanup
            // the tables that we may filled with the other code so far.
            template.execute("delete from complex_outputs_as_inputs")
            template.execute("delete from complex_outputs")
            template.execute("delete from literal_inputs")
            template.execute("delete from jobs")
            template.execute("delete from processes")
        }

        this.jdbcTemplate = template
    }

    @Test
    fun testCreatedWithLiteralInput() {
        Assertions.assertNotNull(this.jdbcTemplate)
        val template = this.jdbcTemplate!!

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val modelpropProcessIdentifier = WPS_PROCESS_IDENTIFIER_MODELPROP
        val assetmasterProcessIdentifier = WPS_PROCESS_IDENTIFIER_ASSETMASTER
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.impl.DeusProcess"


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
        val complexOutputModelprop1 = ComplexOutput(1, 1, "selectedRows", "https://rz-vm140.gfz-potsdam.de/wps/results/modelprop/1", "application/json", "", "UTF-8")
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (${complexOutputModelprop1.id}, '${complexOutputModelprop1.wpsIdentifier}',
                           '${complexOutputModelprop1.mimeType}', '${complexOutputModelprop1.encoding}',
                           '${complexOutputModelprop1.xmlschema}', ${complexOutputModelprop1.jobId},
                           '${complexOutputModelprop1.link}')
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
        val complexOutputModelprop2 = ComplexOutput(2, 2, "selectedRows", "https://rz-vm140.gfz-potsdam.de/wps/results/modelprop/2", "application/json", "", "UTF-8")
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (${complexOutputModelprop2.id}, '${complexOutputModelprop2.wpsIdentifier}',
                           '${complexOutputModelprop2.mimeType}', '${complexOutputModelprop2.encoding}',
                           '${complexOutputModelprop2.xmlschema}', ${complexOutputModelprop2.jobId},
                           '${complexOutputModelprop2.link}')
                """.trimIndent()
        )
        // The call of assetmaster
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (3, 2, 'success')
                """.trimIndent()
        )
        val saraSchema = WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS[0]
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (5, 'schema', '${saraSchema}', 3)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (6, 'querymode', 'intersects', 3)
                """.trimIndent()
        )
        val complexOutputAssetmaster = ComplexOutput(3, 3, "selectedRowsGeoJson", "https://rz-vm140.gfz-potsdam.de/wps/results/assetmaster/3", "application/json", "", "UTF-8")
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (${complexOutputAssetmaster.id}, '${complexOutputAssetmaster.wpsIdentifier}',
                           '${complexOutputAssetmaster.mimeType}', '${complexOutputAssetmaster.encoding}',
                           '${complexOutputAssetmaster.xmlschema}', ${complexOutputAssetmaster.jobId},
                           '${complexOutputAssetmaster.link}')
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
        val complexOutputForDeusEq = ComplexOutput(4, 4, "merged_output","https://rz-vm140.gfz-potsdam.de/wps/results/deus/4" ,"application/json", "", "UTF-8")
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (${complexOutputForDeusEq.id}, '${complexOutputForDeusEq.wpsIdentifier}',
                           '${complexOutputForDeusEq.mimeType}', '${complexOutputForDeusEq.encoding}',
                           '${complexOutputForDeusEq.xmlschema}', ${complexOutputForDeusEq.jobId},
                           '${complexOutputForDeusEq.link}')
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

        val deusUtils = DeusUtils(datamanagementRepo)

        assertEquals(true, deusUtils.createdWithLiteralInput(complexOutputModelprop1, modelpropProcessIdentifier, "schema", listOf("HAZUS")))
        assertEquals(false, deusUtils.createdWithLiteralInput(complexOutputModelprop1, modelpropProcessIdentifier, "schema", listOf("SUPPASRI2023_v2.0")))
        assertEquals(true, deusUtils.createdWithLiteralInput(complexOutputModelprop2, modelpropProcessIdentifier, "schema", listOf("SUPPASRI2023_v2.0")))
    }

    @Test
    fun testIsDeusEqOutput () {
        Assertions.assertNotNull(this.jdbcTemplate)
        val template = this.jdbcTemplate!!

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val modelpropProcessIdentifier = WPS_PROCESS_IDENTIFIER_MODELPROP
        val assetmasterProcessIdentifier = WPS_PROCESS_IDENTIFIER_ASSETMASTER
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.impl.DeusProcess"


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
        val saraSchema = WPS_PROCESS_INPUT_IDENTIFIER_ASSETMASTER_SCHEMA_OPTIONS[0]
        template.execute(
                """
                    insert into literal_inputs (id, wps_identifier, input_value, job_id)
                    values (5, 'schema', '${saraSchema}', 3)
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
        val complexOutputForDeusEq = ComplexOutput(4, 4, "merged_output","https://rz-vm140.gfz-potsdam.de/wps/results/deus/4" ,"application/json", "", "UTF-8")
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (${complexOutputForDeusEq.id}, '${complexOutputForDeusEq.wpsIdentifier}',
                           '${complexOutputForDeusEq.mimeType}', '${complexOutputForDeusEq.encoding}',
                           '${complexOutputForDeusEq.xmlschema}', ${complexOutputForDeusEq.jobId},
                           '${complexOutputForDeusEq.link}')
                """.trimIndent()
        )

        // And a second deus execution. This time for the tsunamis.
        template.execute(
                """
                    insert into jobs (id, process_id, status)
                    values (5, 3, 'success')
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (3, 5, 'exposure', 4)
                """.trimIndent()
        )
        template.execute(
                """
                    insert into complex_outputs_as_inputs (id, job_id, wps_identifier, complex_output_id)
                    values (4, 5, 'fragility', 2)
                """.trimIndent()
        )
        val complexOutputForDeusTs = ComplexOutput(5, 5, "merged_output","https://rz-vm140.gfz-potsdam.de/wps/results/deus/5" ,"application/json", "", "UTF-8")
        template.execute(
                """
                    insert into complex_outputs (id, wps_identifier, mime_type, encoding, xmlschema, job_id, link)
                    values (${complexOutputForDeusTs.id}, '${complexOutputForDeusTs.wpsIdentifier}',
                           '${complexOutputForDeusTs.mimeType}', '${complexOutputForDeusTs.encoding}',
                           '${complexOutputForDeusTs.xmlschema}', ${complexOutputForDeusTs.jobId},
                           '${complexOutputForDeusTs.link}')
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

        val deusUtils = DeusUtils(datamanagementRepo)

        assertEquals(true, deusUtils.isDeusEqOutput(complexOutputForDeusEq))
        assertEquals(false, deusUtils.isDeusEqOutput(complexOutputForDeusTs))
    }


}