package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.Gadsu
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientDriver
import at.cpickl.gadsu.treatment.TreatmentDriver
import at.cpickl.gadsu.view.MenuBarDriver
import at.cpickl.gadsu.view.preferences.PreferencesDriver
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.uispec4j.Panel
import org.uispec4j.UISpec4J
import org.uispec4j.UISpecTestCase
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import java.util.prefs.Preferences

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
            System.setProperty("gadsu.disableLog", "true")
            System.setProperty("uispec4j.test.library", "testng")
            TestLogger().configureLog() // do it twice during the build, but enable once when running in IDE ;)
        }
    }

    protected val log = LoggerFactory.getLogger(javaClass)

    protected var window: Window? = null

    abstract protected fun postInit(window: Window)
    abstract protected fun newMainClassAdapter(): MainClassAdapter

    @BeforeClass
    fun initUi() {
        log.debug("initUi()")
        super.setUp()

        log.debug("Clearing preferences for node: {}", javaClass.name)
        Preferences.userNodeForPackage(javaClass).clear()

        setAdapter(newMainClassAdapter())
        window = retrieveWindow()

        postInit(window!!)
    }

    @AfterClass
    final fun destroyUi() {
        log.debug("destroyUi()")
        super.tearDown()
    }

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
        return MainClassAdapter(Gadsu::class.java,
                "--databaseUrl", "jdbc:hsqldb:mem:testDb",
                "--preferences", javaClass.name)
    }

    override final fun postInit(window: Window) {
        println("postInit() this: ${javaClass.simpleName}")

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
        clientDriver.saveNewClient(client)
    }


}

