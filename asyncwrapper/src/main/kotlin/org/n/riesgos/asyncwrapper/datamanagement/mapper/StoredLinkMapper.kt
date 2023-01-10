package org.n.riesgos.asyncwrapper.datamanagement.mapper

import org.n.riesgos.asyncwrapper.datamanagement.models.StoredLink
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class StoredLinkMapper: RowMapper<StoredLink> {
    override fun mapRow(rs: ResultSet, rowNum: Int): StoredLink {
        return StoredLink(
                rs.getLong("id"),
                rs.getString("original_link"),
                rs.getString("checksum"),
                rs.getString("stored_link")
        )
    }
}