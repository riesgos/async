package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexOutputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class ComplexOutputRepo (val jdbcTemplate: JdbcTemplate){

    fun findByLinkMimetypeXmlschemaAndEncoding (link: String?, mimetype: String, xmlschema: String, encoding: String): List<ComplexOutput> {
        val sql = """
            select complex_outputs.*
            from complex_outputs
            where link = ?
            and mime_type = ?
            and xmlschema = ?
            and encoding = ?
        """.trimIndent()

        return jdbcTemplate.queryForObject(
                sql,
                ComplexOutputRowMapper(),
                link,
                mimetype,
                xmlschema,
                encoding
        )
    }

    fun persist (complexOutput: ComplexOutput) {
        if (complexOutput.id == null) {
            val sqlInsert = """
                insert into complex_outputs (job_id, wps_identifier, link, mime_type, xmlschema, encoding) values (?, ?, ?, ?, ?, ?)
            """.trimIndent()
            jdbcTemplate.update(sqlInsert, complexOutput.jobId, complexOutput.wpsIdentifier,
                    complexOutput.link, complexOutput.mimeType, complexOutput.xmlschema, complexOutput.encoding)
            // Doesn't extract the id at the moment.
        } else {
            val sqlUpdate = """
                update complex_outputs set 
                job_id = ?,
                wps_identifier = ?,
                link = ?,
                mime_type = ?,
                xmlschema = ?,
                encoding = ?
                where id = ?
            """.trimIndent()
            jdbcTemplate.update(sqlUpdate, complexOutput.jobId, complexOutput.wpsIdentifier, complexOutput.link, complexOutput.mimeType, complexOutput.xmlschema, complexOutput.encoding, complexOutput.id)
        }
    }
}