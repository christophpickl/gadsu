package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.GadsuApp
import at.cpickl.gadsu.client.ClientDriver
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.UISpec4J
import org.uispec4j.UISpecTestCase
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter


@Test(groups = arrayOf("uiTest"))
abstract class UiTest : UISpecTestCase() {
    companion object {
        init {
            // TODO test logging does not work in tests for main app :(
            TestLogger().configureLog()
            System.setProperty("uispec4j.test.library", "testng")
        }
    }
    private val log = LoggerFactory.getLogger(javaClass)
    private var window: Window? = null

    private var clientDriver: ClientDriver? = null

    // do setup and teardown for every test method (not class!) to ensure a fresh state
    @BeforeMethod
    override fun setUp() {
        log.debug("setUp()")
        super.setUp()

        setAdapter(MainClassAdapter(GadsuApp::class.java, "--databaseUrl", "jdbc:hsqldb:mem:testDb"))
        window = retrieveWindow()

        clientDriver = ClientDriver(this)
    }

    @AfterMethod
    override fun tearDown() {
        log.debug("tearDown()")
        super.tearDown()
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