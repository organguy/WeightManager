package kr.co.weightmanager.util

class VersionCheck(version: String) : Comparable<VersionCheck> {

    private var version: String? = null

    init {
        requireNotNull(version) { "Version can not be null" }

        if(!version.matches(Regex("[0-9]+(\\.[0-9]+)*")))
            throw IllegalArgumentException("Invalid version format")

        this.version = version
    }

    fun get(): String? {
        return version
    }

    override fun compareTo(that: VersionCheck): Int {
        if (that == null) return 1

        val thisParts = this.get()!!.split(".").toTypedArray()
        val thatParts = that.get()!!.split(".").toTypedArray()

        val length = Math.max(thisParts.size, thatParts.size)

        for (i in 0 until length) {
            val thisPart = if (i < thisParts.size) thisParts[i].toInt() else 0
            val thatPart = if (i < thatParts.size) thatParts[i].toInt() else 0
            if (thisPart < thatPart) return -1
            if (thisPart > thatPart) return 1
        }

        return 0
    }

    override fun equals(that: Any?): Boolean {
        if (this === that) return true
        if (that == null) return false
        return if (this.javaClass != that.javaClass) false else this.compareTo((that as VersionCheck?)!!) == 0
    }


}