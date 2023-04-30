package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture
import org.n.riesgos.asyncwrapper.datamanagement.models.StoredLink

class StoredLinkRepoTest {
    @Test
    fun testFindByStoredLink () {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = StoredLinkRepo(template)

        template.execute("""
            insert into stored_links (id, original_link, checksum, stored_link)
            values (1, 'https://long', '123', 'https://short')
        """.trimIndent())

        val result1 = repo.findByStoredLink("https://short")
        assertEquals(1, result1.size)

        assertEquals(1, result1[0].id)
        assertEquals("https://long", result1[0].originalLink)
        assertEquals("123", result1[0].checksum)
        assertEquals("https://short", result1[0].storedLink)

        val result0 = repo.findByStoredLink("http://no.existing/yet")
        assertEquals(0, result0.size)
    }

    @Test
    fun testFindByOriginalLinkAndChecksum () {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = StoredLinkRepo(template)

        template.execute("""
            insert into stored_links (id, original_link, checksum, stored_link)
            values (1, 'https://long', '123', 'https://short')
        """.trimIndent())

        val result1 = repo.findByOriginalLinkAndChecksum("https://long", "123")
        assertEquals(1, result1.size)

        assertEquals(1, result1[0].id)
        assertEquals("https://long", result1[0].originalLink)
        assertEquals("123", result1[0].checksum)
        assertEquals("https://short", result1[0].storedLink)

        val result0 = repo.findByOriginalLinkAndChecksum("https://long", "124")
        assertEquals(0, result0.size)
    }

    @Test
    fun testPersist() {
        val template = H2DbFixture().getJdbcTemplate()
        val repo = StoredLinkRepo(template)

        val startCount = template.queryForObject("select count(*) from stored_links", Int::class.javaObjectType)
        assertEquals(0, startCount)

        val storedLink = repo.persist(StoredLink(null, "https://long", "123", "https://short"))
        assertTrue(storedLink.id != null)

        val afterInsertCount = template.queryForObject("select count(*) from stored_links", Int::class.javaObjectType)
        assertEquals(1, afterInsertCount)

        val storedLinkToUpdate = StoredLink(storedLink.id, storedLink.originalLink, storedLink.checksum, "https://different")
        val updatedStoredLink = repo.persist(storedLinkToUpdate)

        val queryResults = repo.findByStoredLink("https://different")
        assertEquals(1, queryResults.size)
        assertEquals(updatedStoredLink.storedLink, queryResults[0].storedLink)
    }
}