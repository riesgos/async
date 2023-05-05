package org.n.riesgos.asyncwrapper.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class VersionTest {

    @Test
    fun compareTo() {
        val one = Version("1.0.0")
        val oneOther = Version("1.0.0")
        val two = Version("2.0.0")
        assertTrue(one < two)
        assertTrue(one == oneOther)
        assertTrue(two > one)
    }
}