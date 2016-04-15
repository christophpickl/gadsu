package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.Gadsu
import at.cpickl.gadsu.client.ClientDriver
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.uispec4j.Button
import org.uispec4j.MenuItem
import org.uispec4j.Trigger
import org.uispec4j.UISpec4J
import org.uispec4j.UISpecTestCase
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import org.uispec4j.interception.WindowHandler
import org.uispec4j.interception.WindowInterceptor


@Test(groups = arrayOf("uiTest"))
abstract class UiTest : UISpecTestCase() {
    companion object {
        init {
            System.setProperty("gadsu.disableLog", "true")
            System.setProperty("uispec4j.test.library", "testng")
            TestLogger().configureLog()
        }
    }
    private val log = LoggerFactory.getLogger(javaClass)
    private var window: Window? = null

    private var clientDriver: ClientDriver? = null

    @BeforeClass
    fun initUi() {
        log.debug("initUi()")
        super.setUp()

        setAdapter(MainClassAdapter(Gadsu::class.java, "--databaseUrl", "jdbc:hsqldb:mem:testDb"))
        window = retrieveWindow()

        clientDriver = ClientDriver(this, window!!)
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
            return mainWindow
        } finally {
            UISpec4J.setWindowInterceptionTimeLimit(oldTimeout)
        }
    }

}

fun MenuItem.clickAndDisposeDialog(buttonLabelToClick: String) {
    _clickAndDisposeDialog(buttonLabelToClick, triggerClick())
}

fun Button.clickAndDisposeDialog(buttonLabelToClick: String) {
    _clickAndDisposeDialog(buttonLabelToClick, triggerClick())
}

private fun _clickAndDisposeDialog(buttonLabelToClick: String, trigger: Trigger) {
    WindowInterceptor
            .init(trigger)
            .process(object : WindowHandler() {
                override fun process(dialog: Window): Trigger {
                    return dialog.getButton(buttonLabelToClick).triggerClick();
                }
            })
            .run()
}
