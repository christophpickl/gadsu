package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.GadsuApp
import at.cpickl.gadsu.client.ClientDriver
import org.slf4j.LoggerFactory
import org.uispec4j.UISpec4J
import org.uispec4j.UISpecTestCase
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter

abstract class UiTest : UISpecTestCase() {
    companion object {
        init {
            // TODO test logging does not work in junit tests :(
            TestLogger().configureLog()
            System.setProperty("uispec4j.test.library", "junit") // MINOR seems as we can switch to testng anyway ;)
        }
    }
    private val log = LoggerFactory.getLogger(javaClass)
    private var window: Window? = null

    private var clientDriver: ClientDriver? = null

    override fun setUp() {
        log.debug("setUp()")
        super.setUp()

        setAdapter(MainClassAdapter(GadsuApp::class.java, "--databaseUrl", "jdbc:hsqldb:mem:testDb"))
        window = retrieveWindow()

        clientDriver = ClientDriver(this)
    }

    protected fun clientDriver() = clientDriver!!

    private fun retrieveWindow():Window {
        // increase timeout, as it seems as app startup needs more time than default timeout
        val old = UISpec4J.getWindowInterceptionTimeLimit()
        try {
            UISpec4J.setWindowInterceptionTimeLimit(1000 * 20)
            return getMainWindow()
        } finally {
            UISpec4J.setWindowInterceptionTimeLimit(old)
        }
    }

}