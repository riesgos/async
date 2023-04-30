package org.n.riesgos.asyncwrapper.datamanagement.repos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.n.riesgos.asyncwrapper.datamanagement.H2DbFixture
import org.n.riesgos.asyncwrapper.datamanagement.mapper.BboxInputRowMapper
import org.n.riesgos.asyncwrapper.datamanagement.models.BboxInput

class BboxInputRepoTest {
    @Test
    fun testFindByProcessWpsIdentifierJobStatusInputWpsIdentifierCornersAndCrs() {
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
                    insert into bbox_inputs(id, job_id, wps_identifier, lower_corner_x, lower_corner_y, upper_corner_x, upper_corner_y, crs)
                    values (1, 1, 'bbox', -10, -20, 30, 0, 'epsg:4326')
                """.trimIndent()
        )

        val bboxInputRepo = BboxInputRepo(template)

        val resultList1 = bboxInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierCornersAndCrs(
                assetmasterProcessIdentifier,
                "success",
                "bbox",
                -10.0,
                -20.0,
                30.0,
                0.0,
                "epsg:4326"
        )
        assertEquals(resultList1.size, 1)
        val bbox = resultList1[0]
        assertEquals(1, bbox.id)
        assertEquals(1, bbox.jobId)
        assertEquals("epsg:4326", bbox.crs)
        assertEquals(-10.0, bbox.lowerCornerX)
        assertEquals(-20.0, bbox.lowerCornerY)
        assertEquals(30.0, bbox.upperCornerX)
        assertEquals(0.0, bbox.upperCornerY)

        assertEquals(0, bboxInputRepo.findByProcessWpsIdentifierJobStatusInputWpsIdentifierCornersAndCrs(
                assetmasterProcessIdentifier,
                "success",
                "bbox2",
                -10.0,
                -20.0,
                30.0,
                0.0,
                "epsg:4326"
        ).size)
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
        val bboxInput = BboxInput(null, 1, "bbox", -10.0, -20.0, 30.0, 0.0, "epsg:4326")

        val bboxInputRepo = BboxInputRepo(template)

        val bboxAfterSave = bboxInputRepo.persist(bboxInput)
        assertTrue(bboxAfterSave.id != null)

        val bboxInput2 = BboxInput(null, 1, "bbox2", -10.0, -20.0, 30.0, 5.0, "epsg:4326")
        val bboxAfterSave2 = bboxInputRepo.persist(bboxInput2)
        assertEquals(bboxAfterSave.id!! + 1, bboxAfterSave2.id)

        val bbox1Update = BboxInput(bboxAfterSave.id, bboxAfterSave.jobId, "updated value", bboxAfterSave.lowerCornerX, bboxAfterSave.lowerCornerY, bboxAfterSave.upperCornerX, bboxAfterSave.upperCornerY, bboxAfterSave.crs);

        bboxInputRepo.persist(bbox1Update)

        val queryResult = template.query(
                """select * from bbox_inputs where id = ${bboxAfterSave.id}""", BboxInputRowMapper()
        )
        assertEquals(1, queryResult.size)
        assertEquals(bbox1Update.id, queryResult[0].id)
        assertEquals(bboxAfterSave!!.id, queryResult[0].id)
        assertEquals("updated value", bbox1Update.wpsIdentifier)
    }
}