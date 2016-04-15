package at.cpickl.gadsu.client

import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.testinfra.clickAndDisposeDialog
import org.slf4j.LoggerFactory
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.Trigger
import org.uispec4j.Window
import org.uispec4j.interception.WindowHandler
import org.uispec4j.interception.WindowInterceptor
import java.lang.reflect.Method



@Test(groups = arrayOf("uiTest"))
class ClientUiTest : UiTest() {

    private val log = LoggerFactory.getLogger(javaClass)

    private var client = Client.unsavedValidInstance()

    @BeforeMethod
    fun resetState(method: Method) {
        log.debug("resetState()")
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
        driver.assertListContains(client)
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
        val driver = clientDriver()

        driver.saveClient(client)
        driver.deleteClient(client)

        assertThat(not(driver.list.contains(client.fullName)))
    }

    @Test(dependsOnMethods = arrayOf("saveClient_sunshine"))
    fun createNewClientRequest_shouldDeselectEntryInMasterList() {
        val driver = clientDriver()

        driver.saveClient(client)
        assertThat(driver.list.selectionEquals(client.fullName))

        driver.createButton.click()
        assertThat(driver.list.selectionIsEmpty())
    }

    fun checkUnsavedChanges_createButton_newClient() {
        val driver = clientDriver()

        driver.inputFirstName.setText("foo", false)
        driver.createButton.clickAndDisposeDialog("Abbrechen")
    }

    fun checkUnsavedChanges_createButton_newClient_save() {
        val driver = clientDriver()

        driver.inputFirstName.setText(client.firstName, false)
        driver.inputLastName.setText(client.lastName, false)
        driver.createButton.clickAndDisposeDialog("Speichern")

        driver.assertListContains(client)
    }

    fun checkUnsavedChanges_createButton_existingClient() {
        val driver = clientDriver()

        driver.saveClient(client)
        driver.inputFirstName.setText("something else", false)

        driver.createButton.clickAndDisposeDialog("Abbrechen")
    }

    fun checkUnsavedChanges_selectDifferentInList_forNewClient() {
        val driver = clientDriver()

        driver.saveClient(client)

        driver.createButton.click()
        driver.inputFirstName.setText("foo", false)

        WindowInterceptor
                .init({ driver.list.select(client.fullName) })
                .process(object : WindowHandler() {
                    override fun process(dialog: Window): Trigger {
                        return dialog.getButton("Abbrechen").triggerClick();
                    }
                })
                .run()

    }

    fun checkUnsavedChanges_selectDifferentInList_existingClient() {
        val driver = clientDriver()

        driver.inputFirstName.setText("foo", false)
        driver.createButton.clickAndDisposeDialog("Abbrechen")
    }

}