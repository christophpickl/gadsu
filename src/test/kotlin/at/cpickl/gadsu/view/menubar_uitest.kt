package at.cpickl.gadsu.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.testinfra.BaseDriver
import at.cpickl.gadsu.testinfra.UiTest
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.MenuItem
import org.uispec4j.Window

class MenuBarDriver(test: UiTest, window: Window) : BaseDriver(test, window) {

    private val menu = window.menuBar
    private val menuReports = menu.getMenu("Berichte")
    val menuReportsGenerateProtocol = menuReports.getSubMenu("Protokoll erstellen")
    val menuItemPreferences: MenuItem get() = menu.getMenu("Datei").getSubMenu("Einstellungen")

    fun assertItemEnabled(menuItem: MenuItem, expectedEnabled: Boolean) {
        if (expectedEnabled) {
            test.assertThat("Expected '${menuItem.name}' to be enabled!",
                    menuItem.isEnabled)
        } else {
            test.assertThat("Expected '${menuItem.name}' to be disabled!",
                    test.not(menuItem.isEnabled))
        }
    }

}

@Test(groups = arrayOf("uiTest"))
class MenubarUiTest : UiTest() {

    private val driver: MenuBarDriver get() = menuBarDriver

    @BeforeMethod
    fun resetState() {
        clientDriver.createButton.click()
    }

    @Test(dependsOnMethods = arrayOf("Given user is selected, generate protocol should be enabled"))
    fun `On startup, generate protocol should be disabled`() {
        driver.assertItemEnabled(driver.menuReportsGenerateProtocol, false)
    }

    fun `Given user is selected, generate protocol should be enabled`() {
        clientDriver.saveNewClient(Client.unsavedValidInstance())
        driver.assertItemEnabled(driver.menuReportsGenerateProtocol, true)
    }

}
