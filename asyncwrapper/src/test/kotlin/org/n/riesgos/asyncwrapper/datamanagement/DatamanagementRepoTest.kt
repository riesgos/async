package org.n.riesgos.asyncwrapper.datamanagement

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.models.Process
import org.n.riesgos.asyncwrapper.datamanagement.repos.*
import org.n.riesgos.asyncwrapper.dummy.ModelpropEqWrapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.test.annotation.DirtiesContext
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * You can run this test with
 * docker-compose run mvn mvn test -Dtest=DatamanagementRepoTest
 *
 * But it is not yet working as it should.
 * It doesn't cleanup the embedded database on each run - and the h2 has
 * some trouble when I try to use the persist methods in the repo classes.
 *
 * Nevertheless, it is as fast as we can get for the moment.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DatamanagementRepoTest {
    private var jdbcTemplate: JdbcTemplate? = null

    @BeforeEach
    fun setUp() {
        val dataSource = EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build()

        val folderOfMigrationFiles = File("/usr/src/backend/migrations")
        val files = folderOfMigrationFiles.listFiles()
        Arrays.sort(files)
        assertTrue(files.size > 0)
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
            template.execute("delete from processes")
        }

        this.jdbcTemplate = template
    }

    @Test
    fun testDatabaseStructure() {
        assertNotNull(this.jdbcTemplate)
        this.jdbcTemplate?.execute("select * from literal_inputs")
    }

    @Test
    fun testFindLiteralInputsForParentProcessOfComplexOutput() {
        assertNotNull(this.jdbcTemplate)
        val template = this.jdbcTemplate!!

        val gfzWpsUrl = "https://rz-vm140.gfz-potsdam.de/wps"
        val modelpropProcessIdentifier = "org.n52.gfz.riesgos.algorithm.ModelpropProcess"
        val assetmasterProcessIdentifier = "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"
        val deusProcessIdentifier = "org.n52.gfz.riesgos.algorithm.DeusProcess"

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


        template.execute(
                "insert into processes(id, wps_url, wps_identifier) values (1, '"
                        + gfzWpsUrl + "', '" + modelpropProcessIdentifier + "')"
        )
        /*
        val modelpropProcess = datamanagementRepo.processRepo.persist(Process(null, gfzWpsUrl, modelpropProcessIdentifier))
        val assetmasterProcess = datamanagementRepo.processRepo.persist(Process(null, gfzWpsUrl, assetmasterProcessIdentifier))
        val deusProcess = datamanagementRepo.processRepo.persist(Process(null, gfzWpsUrl, deusProcessIdentifier))
         */

    }

    
}