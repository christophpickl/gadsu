package at.cpickl.gadsu.version

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.NoInternetConnectionException
import com.google.common.io.Resources
import java.net.URL
import java.net.UnknownHostException

fun main(args: Array<String>) {
    val url = URL("https://raw.githubusercontent.com/christophpickl/gadsu/master/src/test/resources/gadsu_test/version_latest_TEST.txt")
    println("Loaded version: [${WebLatestVersionFetcher(url).fetch()}]")
}

interface LatestVersionFetcher {

    fun fetch(): Version

}

class WebLatestVersionFetcher(private val versionPropertiesFile: URL) : LatestVersionFetcher {
    private val log = LOG(javaClass)
    override fun fetch(): Version {
        log.debug("fetch() ... url: {}", versionPropertiesFile)
        try {
            val fileContent = Resources.toString(versionPropertiesFile, Charsets.UTF_8).trim()
            return Version.parse(fileContent)
        } catch(e: UnknownHostException) {
            throw NoInternetConnectionException(e)
        }
    }

}
