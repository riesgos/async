package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.n.riesgos.asyncwrapper.datamanagement.mapper.StoredLinkMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.StoredLink
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.Statement

@Component
class StoredLinkRepo (val jdbcTemplate: JdbcTemplate) {

    fun findByStoredLink(storedLink: String): List<StoredLink> {
        val sql = """
            select *
            from stored_links
            where stored_link = ?
        """.trimIndent()

        return jdbcTemplate.query(sql, StoredLinkMapper(), storedLink)
    }

    fun findByOriginalLinkAndChecksum(originalLink: String, checksum: String): List<StoredLink> {
        val sql = """
            select *
            from stored_links
            where original_link = ?
            and checksum = ?
        """.trimIndent()

        return jdbcTemplate.query(sql, StoredLinkMapper(), originalLink, checksum)
    }

    fun persist (storedLink: StoredLink) : StoredLink {
        if (storedLink.id === null) {
            val sqlInsert = """
                insert into stored_links (original_link, checksum, stored_link)
                values (?, ?, ?)
                returning id
            """.trimIndent()

            val key = GeneratedKeyHolder()

            val preparedStatementCreator = PreparedStatementCreator { con: Connection ->
                val ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, storedLink.originalLink)
                ps.setString(2, storedLink.checksum)
                ps.setString(3, storedLink.storedLink)
                ps
            }


            jdbcTemplate.update(preparedStatementCreator, key)

            val newId = key.getKey()!!.toLong()

            return StoredLink(newId, storedLink.originalLink, storedLink.checksum, storedLink.storedLink)
        } else {
            val sqlUpdate = """
                update stored_links set
                original_link = ?,
                checksum = ?,
                stored_link = ?
                where id = ?
            """.trimIndent()

            jdbcTemplate.update(sqlUpdate, storedLink.originalLink, storedLink.checksum, storedLink.storedLink, storedLink.id)
            return storedLink
        }
    }
}