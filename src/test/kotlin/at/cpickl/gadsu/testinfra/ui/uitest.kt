package at.cpickl.gadsu.testinfra.ui

import at.cpickl.gadsu.Gadsu
import at.cpickl.gadsu.global.GadsuSystemProperty
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientDriver
import at.cpickl.gadsu.preferences.PreferencesDriver
import at.cpickl.gadsu.testinfra.TestLogger
import at.cpickl.gadsu.treatment.TreatmentDriver
import at.cpickl.gadsu.view.MenuBarDriver
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterClass
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.Panel
import org.uispec4j.UISpec4J
import org.uispec4j.UISpecTestCase
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter

//@Guice(modules = arrayOf(UiTestModule::class))
//class UiTestModule : AbstractModule() {
//    override fun configure() {
//        bind(ClientDriver::class.java).toInstance(clientDriver)
//    }
//}

/**
 * Independent of any gadsu specific UI stuff.
 */
@Test(groups = arrayOf("uiTest"))
abstract class SimpleUiTest : UISpecTestCase() {
    companion object {
        init {
            GadsuSystemProperty.testRun.enable()
            GadsuSystemProperty.disableLog.enable()
            GadsuSystemProperty.disableAutoUpdate.enable()
            GadsuSystemProperty.disableAutoBackup.enable()
            System.setProperty("uispec4j.test.library", "testng")
            TestLogger().configureLog() // do it twice during the build, but enable once when running in IDE ;)
        }
    }

    protected val log = LoggerFactory.getLogger(javaClass)!!

    protected var window: Window? = null

    abstract protected fun postInit(window: Window)
    abstract protected fun newMainClassAdapter(): MainClassAdapter

    @BeforeClass
    fun initUi() {
        log.debug("initUi()")
        if (System.getProperty("java.awt.headless") == "true") {
            throw AssertionError("java.awt.headless must NOT be set to true for UI tests!")
        }
        super.setUp()

        setAdapter(newMainClassAdapter())
        window = retrieveWindow()

        postInit(window!!)
    }

    @AfterClass
    fun destroyUi() {
        log.debug("destroyUi()")
        super.tearDown()
    }

    @BeforeMethod
    fun beforeBridgeJunit() {
        super.setUp()
    }

    @AfterMethod
    fun afterBridgeJunit() {
        super.tearDown()
    }

    private fun retrieveWindow(): Window {
        // increase timeout, as it seems as app startup needs more time than default timeout
        val oldTimeout = UISpec4J.getWindowInterceptionTimeLimit()
        try {
            UISpec4J.setWindowInterceptionTimeLimit(1000 * 20)
            return mainWindow // this getter does all the logic to retrieve the main window
        } finally {
            UISpec4J.setWindowInterceptionTimeLimit(oldTimeout)
        }
    }

}

abstract class UiTest : SimpleUiTest() {
    private var _mainDriver: MainDriver? = null

    private var _menuBarDriver: MenuBarDriver? = null
    private var _clientDriver: ClientDriver? = null
    private var _treatmentDriver: TreatmentDriver? = null

    protected val mainDriver: MainDriver get() = _mainDriver!!

    protected val menuBarDriver: MenuBarDriver get() = _menuBarDriver!!
    protected val clientDriver: ClientDriver get() = _clientDriver!!
    protected val treatmentDriver: TreatmentDriver get() = _treatmentDriver!!


    override final fun newMainClassAdapter(): MainClassAdapter {
        return MainClassAdapter(Gadsu::class.java, "--databaseUrl", "jdbc:hsqldb:mem:testDb")
    }

    override final fun postInit(window: Window) {
//        println("postInit() this: ${javaClass.simpleName}")

        _menuBarDriver = MenuBarDriver(this, window)
        _clientDriver = ClientDriver(this, window)
        _treatmentDriver = TreatmentDriver(this, window)

        _mainDriver = MainDriver(this, window, menuBarDriver, clientDriver, treatmentDriver)
    }

    @Test(enabled = false) // must be public, so driver can acess it as well... hm... :-/
    fun openPreferencesDriver(): PreferencesDriver {
        return PreferencesDriver(this, mainDriver.openPreferencesWindow())
    }

    @Test(enabled = false) // must be public, so driver can acess it as well... hm... :-/
    fun assertPanelContainedInMainWindow(panelName: String) {
        assertThat("$panelName expected to be contained in main window.",
                window!!.containsUIComponent(Panel::class.java, panelName))
    }

    protected fun saveClient(client: Client) {
        clientDriver.saveBasicClient(client)
    }

}

