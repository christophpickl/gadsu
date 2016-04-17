package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.Gadsu
import at.cpickl.gadsu.client.ClientDriver
import at.cpickl.gadsu.treatment.TreatmentDriver
import at.cpickl.gadsu.view.MenuBarDriver
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

@Test(groups = arrayOf("uiTest"))
abstract class UiTest : UISpecTestCase() {
    companion object {
        init {
            System.setProperty("gadsu.disableLog", "true")
            System.setProperty("uispec4j.test.library", "testng")
            TestLogger().configureLog() // do it twice during the build, but enable once when running in IDE ;)
        }
    }
    private val log = LoggerFactory.getLogger(javaClass)
    private var window: Window? = null

    private var mainDriver: MainDriver? = null
    private var menuBarDriver: MenuBarDriver? = null
    private var clientDriver: ClientDriver? = null
    private var treatmentDriver: TreatmentDriver? = null

    @BeforeClass
    fun initUi() {
        log.debug("initUi()")
        super.setUp()

        log.debug("Clearing preferences for node: {}", javaClass.name)
        Preferences.userNodeForPackage(javaClass).clear()

        setAdapter(MainClassAdapter(Gadsu::class.java,
                "--databaseUrl", "jdbc:hsqldb:mem:testDb",
                "--preferences", javaClass.name))
        window = retrieveWindow()

        mainDriver = MainDriver(this, window!!)
        menuBarDriver = MenuBarDriver(this, window!!)
        clientDriver = ClientDriver(this, window!!)
        treatmentDriver = TreatmentDriver(this, window!!)
    }

    @AfterClass
    fun destroyUi() {
        log.debug("destroyUi()")
        super.tearDown()
    }

    fun mainDriver() = mainDriver!!
    fun menuBarDriver() = menuBarDriver!!
    fun clientDriver() = clientDriver!!
    fun treatmentDriver() = treatmentDriver!!

    @Test(enabled = false)
    fun assertPanelContainedInMainWindow(panelName: String) {
        assertThat("$panelName expected to be contained in main window.",
                window!!.containsUIComponent(Panel::class.java, panelName))
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

