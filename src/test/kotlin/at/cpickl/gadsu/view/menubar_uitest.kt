package at.cpickl.gadsu.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.ui.BaseDriver
import at.cpickl.gadsu.testinfra.ui.UiTest
import at.cpickl.gadsu.testinfra.ui.clickAndDisposeDialog
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.MenuItem
import org.uispec4j.Window

class MenuBarDriver(test: UiTest, window: Window) : BaseDriver<UiTest>(test, window) {

    private val menu = window.menuBar
    private val menuEdit = menu.getMenu("Bearbeiten")
    private val menuReports = menu.getMenu("Berichte")

    val menuEditDeleteClient: MenuItem get() = menuEdit.getSubMenu("Klient löschen")
    val menuItemPreferences: MenuItem get() = menu.getMenu("Datei").getSubMenu("Einstellungen")
    val menuReportsGenerateProtocol = menuReports.getSubMenu("Protokoll erstellen")

    fun assertItemEnabled(menuItem: MenuItem, expectedEnabled: Boolean) {
        if (expectedEnabled) {
            test.assertThat("Expected '${menuItem.name}' to be enabled!",
                    menuItem.isEnabled)
        } else {
            test.assertThat("Expected '${menuItem.name}' to be disabled!",
                    test.not(menuItem.isEnabled))
        }
    }

    fun deleteClient() {
        menuEditDeleteClient.clickAndDisposeDialog("L\u00F6schen")
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
        clientDriver.saveBasicClient(Client.unsavedValidInstance())
        driver.assertItemEnabled(driver.menuReportsGenerateProtocol, true)
    }

}
