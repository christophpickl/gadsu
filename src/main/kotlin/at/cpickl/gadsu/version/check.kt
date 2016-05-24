package at.cpickl.gadsu.version

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.MetaInf
import com.google.common.base.MoreObjects
import javax.inject.Inject


interface VersionChecker {
    fun check(): VersionCheckResult
}

class VersionCheckerImpl @Inject constructor(
        private val fetcher: LatestVersionFetcher,
        private val metaInf: MetaInf
) : VersionChecker {

    private val log = LOG(javaClass)

    override fun check(): VersionCheckResult {
        val currentVersion = metaInf.applicationVersion
        val latestVersion = fetcher.fetch()
        if (currentVersion > latestVersion) {
            log.warn("Current version > latest version, seems as you are running a local snapshot version ;)")
            return VersionCheckResult.UpToDate
        }
        if (currentVersion == latestVersion) {
            return VersionCheckResult.UpToDate
        }
//        Thread.sleep(5 * 1000)
        return VersionCheckResult.OutDated(currentVersion, latestVersion)
    }
}


sealed class VersionCheckResult() {
    object UpToDate: VersionCheckResult()
    class OutDated(val current: Version, val latest: Version): VersionCheckResult() {

        override fun equals(other: Any?): Boolean{
            if (this === other) return true
            if (other !is OutDated) return false

            if (current != other.current) return false
            if (latest != other.latest) return false

            return true
        }

        override fun hashCode(): Int{
            var result = current.hashCode()
            result += 31 * result + latest.hashCode()
            return result
        }

        override fun toString(): String {
            return MoreObjects.toStringHelper(this)
                    .add("current", current)
                    .add("latest", latest)
                    .toString()
        }
    }

}

