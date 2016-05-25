package at.cpickl.gadsu.version

import at.cpickl.gadsu.service.LOG
import com.google.common.collect.ComparisonChain

enum class VersionTag() : Comparable<VersionTag> {
    // declaration order IS relevant for comparison!
    Release,
    Snapshot

}
data class Version(val major: Int, val minor: Int, val bugfix: Int, val tag: VersionTag): Comparable<Version> {
    companion object {
        private val log = LOG(javaClass)
        private val SNAPSHOT_TAG = "-SNAPSHOT"
        fun parse(raw: String): Version {
            if (raw == "@application.version@") {
                log.warn("Seems as gradle build did not run yet, as version string has not been injected.")
                return DUMMY
            }
            val tag = if (raw.contains(SNAPSHOT_TAG)) VersionTag.Snapshot else VersionTag.Release
            val splits = raw.split(".")
            val major = splits[0].toInt()
            val minor = splits[1].toInt()
            val bugfix = if (tag == VersionTag.Snapshot) splits[2].substring(0, splits[2].indexOf(SNAPSHOT_TAG)).toInt()
                        else splits[2].toInt()
            return Version(major, minor, bugfix, tag)
        }

        val DUMMY = Version(9, 99, 999, VersionTag.Snapshot)
    }

    fun toLabel(): String {
        return "$major.$minor.$bugfix${if (tag == VersionTag.Snapshot) "-SNAPSHOT" else ""}"
    }

    override fun compareTo(other: Version): Int {
        return ComparisonChain.start()
                .compare(this.major, other.major)
                .compare(this.minor, other.minor)
                .compare(this.bugfix, other.bugfix)
                .compare(this.tag, other.tag)
                .result()
    }

}
