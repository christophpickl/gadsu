package at.cpickl.gadsu.version

import at.cpickl.gadsu.service.LOG
import com.google.common.io.Resources
import java.net.URL

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
        val fileContent = Resources.toString(versionPropertiesFile, Charsets.UTF_8).trim()
        return Version.parse(fileContent)
    }

}
