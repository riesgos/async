package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.ComplexOutputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Statement

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

        return jdbcTemplate.query(
                sql,
                ComplexOutputRowMapper(),
                link,
                mimetype,
                xmlschema,
                encoding
        )
    }

    // A faster way compared to findByLinkMimetypeXmlschemaAndEncoding that returns the overall list
    fun findOptionalFirstByLinkMimetypeXmlschemaAndEncoding (link: String?, mimetype: String, xmlschema: String, encoding: String): ComplexOutput? {
        val sql = """
            select complex_outputs.*
            from complex_outputs
            where link = ?
            and mime_type = ?
            and xmlschema = ?
            and encoding = ?
            limit 1
        """.trimIndent()

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    ComplexOutputRowMapper(),
                    link,
                    mimetype,
                    xmlschema,
                    encoding
            )
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    fun findByOrderIdProcessWpsIdentifierOutputWpsIdentifierAndMimeType (orderId: Long, processWpsIdentifier: String, outputWpsIdentifier: String, mimeType: String) : List<ComplexOutput> {
        val sql = """
            select distinct complex_outputs.*
            from complex_outputs
            join jobs on jobs.id = complex_outputs.job_id
            join order_job_refs on order_job_refs.job_id = jobs.id
            join processes on processes.id = jobs.process_id
            where order_job_refs.order_id = ?
            and processes.wps_identifier = ?
            and complex_outputs.wps_identifier = ?
            and complex_outputs.mime_type = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, ComplexOutputRowMapper(), orderId, processWpsIdentifier, outputWpsIdentifier, mimeType)
    }





    fun persist (complexOutput: ComplexOutput): ComplexOutput {
        if (complexOutput.id == null) {
            val sqlInsert = """
                insert into complex_outputs (job_id, wps_identifier, link, mime_type, xmlschema, encoding) values (?, ?, ?, ?, ?, ?)
            """.trimIndent()

            val key = GeneratedKeyHolder()
            val preparedStatementCreator = PreparedStatementCreator { con: Connection ->
                val ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)
                ps.setLong(1, complexOutput.jobId)
                ps.setString(2, complexOutput.wpsIdentifier)
                ps.setString(3, complexOutput.link)
                ps.setString(4, complexOutput.mimeType)
                ps.setString(5, complexOutput.xmlschema)
                ps.setString(6, complexOutput.encoding)
                ps
            }

            jdbcTemplate.update(preparedStatementCreator, key)

            val newId = (key.getKeyList().get(0).get("id") as Integer).toLong()

            return ComplexOutput(newId, complexOutput.jobId, complexOutput.wpsIdentifier, complexOutput.link, complexOutput.mimeType, complexOutput.xmlschema, complexOutput.encoding)


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
            return complexOutput
        }
    }
}