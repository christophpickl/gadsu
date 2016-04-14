package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.view.CancelButton
import at.cpickl.gadsu.client.view.Client
import at.cpickl.gadsu.client.view.CreateButton
import at.cpickl.gadsu.client.view.InputFirstName
import at.cpickl.gadsu.client.view.InputLastName
import at.cpickl.gadsu.client.view.List
import at.cpickl.gadsu.client.view.SaveButton
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.view.ViewNames
import org.testng.annotations.Test


class ClientDriver(private val test: UiTest) {

    // MINOR or: val list: ListBox get() = test.mainWindow.getListBox(ViewNames.Client.List) ?
    val list = test.mainWindow.getListBox(ViewNames.Client.List)
    val createButton = test.mainWindow.getButton(ViewNames.Client.CreateButton)

    val inputFirstName = test.mainWindow.getInputTextBox(ViewNames.Client.InputFirstName)
    val inputLastName = test.mainWindow.getInputTextBox(ViewNames.Client.InputLastName)
    val saveButton = test.mainWindow.getButton(ViewNames.Client.SaveButton)
    val cancelButton = test.mainWindow.getButton(ViewNames.Client.CancelButton)

}

@Test(groups = arrayOf("uiTest"))
class ClientUiTest : UiTest() {

    private val client = Client.unsavedValidInstance()

    fun saveClient_sunshine() {
        val driver = clientDriver()

        assertEquals(driver.list.size, 0) // sanity check

        // not necessary, by default in insert mode ... driver.createButton.click()
        assertThat(driver.saveButton.textEquals("Neu anlegen"))

        driver.inputFirstName.setText(client.firstName, false)
        driver.inputLastName.setText(client.lastName, false)

        driver.saveButton.click()
        assertThat(driver.saveButton.textEquals("Speichern"))

        assertEquals(driver.list.size, 1)
        assertThat(driver.list.selectionEquals("${client.firstName} ${client.lastName}"))
        assertThat(driver.list.contentEquals("${client.firstName} ${client.lastName}"))
    }

    fun cancelInsertClient_shouldClearAllFields() {
        val driver = clientDriver()

        // sanity checks
        assertEquals(driver.list.size, 0)
        assertThat(driver.inputFirstName.textIsEmpty())
        assertThat(driver.inputLastName.textIsEmpty())

        driver.inputFirstName.setText(client.firstName, false)
        driver.inputLastName.setText(client.lastName, false)

        driver.cancelButton.click()

        assertThat(driver.inputFirstName.textIsEmpty())
        assertThat(driver.inputLastName.textIsEmpty())
    }

    // same applies for already saved client
    // and also when hit the cancel button
    fun buttonsDisabledIfThereAreNoChangesForEmptyClient() {
        val driver = clientDriver()

        assertThat(not(driver.saveButton.isEnabled))
        assertThat(not(driver.cancelButton.isEnabled))

        driver.inputFirstName.setText("change", false)

        assertThat(driver.saveButton.isEnabled)
        assertThat(driver.cancelButton.isEnabled)

        driver.inputFirstName.clear()

        assertThat(not(driver.saveButton.isEnabled))
        assertThat(not(driver.cancelButton.isEnabled))
    }
}
