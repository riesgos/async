package org.n.riesgos.asyncwrapper.dummy.utils

import java.security.MessageDigest

class HexUtils {

    fun sha1(content: ByteArray, uppercase: Boolean = false): String {
        val format = if (uppercase)  "%02X" else "%02x"
        val digest = MessageDigest.getInstance("SHA-1")
        val hashed = digest.digest(content)
        val stringBuilder = StringBuilder()
        for (b in hashed) {
            stringBuilder.append(String.format(format, b))
        }
        return stringBuilder.toString()
    }

}