package org.n.riesgos.asyncwrapper.datamanagement

import org.junit.jupiter.api.Assertions
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * Helper class to provide an jdbcTemplate with an embedded h2 db
 * and with all of the migrations done.
 * State of the database is empty.
 */
class H2DbFixture {
    fun getJdbcTemplate (): JdbcTemplate {
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
            template.execute("delete from bbox_inputs")
            template.execute("delete from complex_outputs_as_inputs")
            template.execute("delete from complex_outputs")
            template.execute("delete from literal_inputs")
            template.execute("delete from jobs")
            template.execute("delete from processes")
        }
        return template
    }
}