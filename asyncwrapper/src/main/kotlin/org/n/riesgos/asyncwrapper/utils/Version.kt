package org.n.riesgos.asyncwrapper.utils


class Version(val versionStr : String) : Comparable<Version> {

    override fun compareTo(other: Version): Int {
        if (other == null) return 1
        val thisParts: List<String> = this.versionStr.split(".")
        val thatParts: List<String> = other.versionStr.split(".")
        val length = thisParts.size.coerceAtLeast(thatParts.size)
        for (i in 0 until length) {
            val thisPart = if (i < thisParts.size) thisParts[i].toInt() else 0
            val thatPart = if (i < thatParts.size) thatParts[i].toInt() else 0
            if (thisPart < thatPart) return -1
            if (thisPart > thatPart) return 1
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Version){
            (this.compareTo(other) == 0)
        }else{
            false;
        }
    }
}