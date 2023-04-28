package org.n.riesgos.asyncwrapper.dummy.utils

import okio.internal.commonAsUtf8ToByteArray
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HexUtilsTest {

    @Test
    fun testSha1() {
        val testString = "Hi there, Riesgos folks"
        val expectedResult = "6F6D567AB24FA91798CEB387521953A83ECBC910"

        val result = HexUtils().sha1(testString.commonAsUtf8ToByteArray(), true)

        assertEquals(expectedResult, result)

        val expectedResultlowercase = "6f6d567ab24fa91798ceb387521953a83ecbc910"
        assertEquals(expectedResultlowercase, HexUtils().sha1(testString.commonAsUtf8ToByteArray()))
    }
}