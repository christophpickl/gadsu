package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.view.*
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.testinfra.clickAndDisposeDialog
import at.cpickl.gadsu.testinfra.skip
import at.cpickl.gadsu.view.ViewNames
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.Window
import org.uispec4j.interception.PopupMenuInterceptor
import java.lang.reflect.Method
import javax.swing.JLabel


class ClientDriver(private val test: UiTest, private val window: Window) {

    // MINOR or: val list: ListBox get() = test.mainWindow.getListBox(ViewNames.Client.List) ?
    val list = window.getListBox(ViewNames.Client.List)
    val createButton = window.getButton(ViewNames.Client.CreateButton)

    val inputFirstName = window.getInputTextBox(ViewNames.Client.InputFirstName)
    val inputLastName = window.getInputTextBox(ViewNames.Client.InputLastName)
    val saveButton = window.getButton(ViewNames.Client.SaveButton)
    val cancelButton = window.getButton(ViewNames.Client.CancelButton)

    fun saveClient(client: Client) {
        createButton.click()

        inputFirstName.setText(client.firstName, false)
        inputLastName.setText(client.lastName, false)

        saveButton.click()
    }

    fun listIndexOf(findLabel: String): Int {
        for (i in 0.rangeTo(list.size - 1)) {
            val label = (list.getSwingRendererComponentAt(i) as JLabel).text
            if (findLabel.equals(label)) {
                return i
            }
        }
        throw AssertionError("Not found list entry with label: '$findLabel'!")
    }

    fun deleteClient(client: Client) {
        val popup = PopupMenuInterceptor.run(list.triggerRightClick(listIndexOf(client.fullName)))
        //        popup.contentEquals("Looooschen")
        val btnDelete = popup.getSubMenu("L\u00F6schen")
        btnDelete.clickAndDisposeDialog()
    }

}

@Test(groups = arrayOf("uiTest"))
class ClientUiTest : UiTest() {

    private var client = Client.unsavedValidInstance()

    @BeforeMethod
    fun resetState(method: Method) {
        val driver = clientDriver()

        if (driver.cancelButton.awtComponent.isEnabled) {
            // TODO watch out for confirmation
            driver.cancelButton.click()
        }
        driver.createButton.click()

        // ensure each test is using a unique client instance
        client = Client.unsavedValidInstance().copy(firstName = javaClass.simpleName, lastName = method.name)
    }

    fun saveClient_sunshine() {
        val driver = clientDriver()

        assertThat(driver.saveButton.textEquals("Neu anlegen")) // sanity check

        driver.inputFirstName.setText(client.firstName, false)
        driver.inputLastName.setText(client.lastName, false)

        driver.saveButton.click()
        assertThat(driver.saveButton.textEquals("Speichern"))

        assertThat(driver.list.selectionEquals("${client.firstName} ${client.lastName}"))
        assertThat(driver.list.contains("${client.firstName} ${client.lastName}"))
    }

    fun cancelInsertClient_shouldClearAllFields() {
        val driver = clientDriver()

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

        driver.inputFirstName.setText("changed", false)

        assertThat(driver.saveButton.isEnabled)
        assertThat(driver.cancelButton.isEnabled)

        driver.inputFirstName.clear()

        assertThat(not(driver.saveButton.isEnabled))
        assertThat(not(driver.cancelButton.isEnabled))
    }

    fun updateClient_shouldUpdateInListAsWell() {
        val driver = clientDriver()

        driver.inputLastName.setText("initial last name will be updated", false)
        driver.saveButton.click()

        driver.inputLastName.setText(client.lastName, false)
        driver.saveButton.click()

        assertThat(driver.list.selectionEquals(client.lastName))
        assertThat(driver.list.contains(client.lastName))
    }

    @Test(dependsOnMethods = arrayOf("saveClient_sunshine"))
    fun deleteClient_sunshine() {
        skip("Popup list bug: https://github.com/UISpec4J/UISpec4J/issues/30")
        val driver = clientDriver()

        driver.saveClient(client)
        driver.deleteClient(client)

        assertThat(not(driver.list.contains(client.fullName)))
        // MINOR assertThat ... detail view is reset to empty form, as when right click on client in master list, it will get selected automatically and therefor the detail view displays this (deleted) user
    }

}
