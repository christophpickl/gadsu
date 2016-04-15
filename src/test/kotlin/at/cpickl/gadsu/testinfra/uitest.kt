package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.GadsuApp
import at.cpickl.gadsu.client.ClientDriver
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.uispec4j.*
import org.uispec4j.interception.MainClassAdapter
import org.uispec4j.interception.WindowHandler
import org.uispec4j.interception.WindowInterceptor


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

    // MINOR could not get BeforeMethod setup for uispec4j to work :(
    @BeforeClass
    fun initUi() {
        log.debug("initUi()")
        super.setUp()

        setAdapter(MainClassAdapter(GadsuApp::class.java, "--databaseUrl", "jdbc:hsqldb:mem:testDb"))
        window = retrieveWindow()

        clientDriver = ClientDriver(this, mainWindow)
    }

    @AfterClass
    fun destroyUi() {
        log.debug("destroyUi()")
        super.tearDown()
    }

    protected fun clientDriver() = clientDriver!!

    private fun retrieveWindow():Window {
        // increase timeout, as it seems as app startup needs more time than default timeout
        val oldTimeout = UISpec4J.getWindowInterceptionTimeLimit()
        try {
            UISpec4J.setWindowInterceptionTimeLimit(1000 * 20)
            return getMainWindow()
        } finally {
            UISpec4J.setWindowInterceptionTimeLimit(oldTimeout)
        }
    }

}

fun MenuItem.clickAndDisposeDialog(label: String = "OK") {
    WindowInterceptor
            .init(triggerClick())
            .process(object : WindowHandler() {
                override fun process(dialog: Window): Trigger {
                    return dialog.getButton(label).triggerClick();
                }
            })
            .run()
}
