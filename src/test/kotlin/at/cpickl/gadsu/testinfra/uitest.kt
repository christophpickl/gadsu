package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.GadsuApp
import at.cpickl.gadsu.client.ClientDriver
import org.uispec4j.UISpec4J
import org.uispec4j.UISpecTestCase
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter

abstract class UiTest : UISpecTestCase() {
    companion object {
        init {
            // TODO test logging does not work in junit tests :(
            TestLogger().configureLog()
        }
    }
    private var window: Window? = null

    private var clientDriver: ClientDriver? = null

    override fun setUp() {
        super.setUp()

        setAdapter(MainClassAdapter(GadsuApp::class.java, "someother-url"))
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