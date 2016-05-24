package at.cpickl.gadsu.version

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.NoInternetConnectionException
import com.google.common.io.Resources
import java.net.SocketException
import java.net.URL
import java.net.UnknownHostException

fun main(args: Array<String>) {
    val url = URL("https://raw.githubusercontent.com/christophpickl/gadsu/master/src/test/resources/gadsu_test/version_latest_TEST.txt")
    println("Loaded version: [${WebLatestVersionFetcher(url).fetch()}]")
}

interface LatestVersionFetcher {

    fun fetch(): Version

}

/*
fun <T> withTimeout(timeoutInMs: Int, function: () -> T): T? {
    var result: T? = null
    val timer = Timer(true)

    var thrownException: Exception? = null
    val thread = Thread( {
        try {
            println("thread internally started")
            result = function()
            println("thread internally finished")
        } catch(e: Exception) {
            thrownException = e
        } finally {
            timer.cancel()
        }
    })
    println("thread.start()")
    thread.isDaemon = true
    thread.start()

    val task = object : TimerTask() {
        override fun run() {
            println("thread needed longer than timeout")
            thread.interrupt()
        }
    }
    println("timer.schedule()")
    timer.schedule(task, timeoutInMs.toLong())

    println("snoop doggy joint")
    thread.join()

    if (thrownException != null) {
        throw thrownException!!
    }
    println("EEEEND")
    return result
}
*/
class WebLatestVersionFetcher(private val versionPropertiesFile: URL) : LatestVersionFetcher {
    private val log = LOG(javaClass)
    override fun fetch(): Version {
        log.debug("fetch() ... url: {}", versionPropertiesFile)

//        FIXME return withTimeout(CONNECTION_TIMEOUT) {
        try {
            println("Internet connection START")
            val fileContent = Resources.toString(versionPropertiesFile, Charsets.UTF_8).trim()
            println("Internet connection END")
            return Version.parse(fileContent)
        } catch (e: UnknownHostException) {
            throw NoInternetConnectionException(e)
        } catch (e: SocketException) {
            throw NoInternetConnectionException(e)
        }

    }

}
