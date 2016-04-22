package at.cpickl.gadsu.client

import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.testinfra.clickAndDisposeDialog
import at.cpickl.gadsu.testinfra.skip
import org.slf4j.LoggerFactory
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
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
            driver.cancelButton.click()
        }
        driver.createButton.click()

        // ensure each test is using a unique client instance
        client = Client.unsavedValidInstance().copy(firstName = javaClass.simpleName, lastName = method.name)
    }


    // ---------------------------------------------------------------------------

    //<editor-fold desc="save">

    fun saveClient_sunshine() {
        val driver = clientDriver()

        assertThat(driver.saveButton.textEquals("Neu anlegen")) // sanity check

        driver.inputFirstName.text = client.firstName
        driver.inputLastName.text = client.lastName

        driver.saveButton.click()
        assertThat(driver.saveButton.textEquals("Speichern"))

        driver.assertListContains(client)
        driver.assertListSelected(client)
    }

    fun updateClient_shouldUpdateInListAsWell() {
        val driver = clientDriver()

        driver.fillForm(client)
        driver.saveButton.click()

        driver.assertListContains(client)
        driver.assertListSelected(client)

        val updatedClient = client.copy(firstName = "initial first name will be updated")
        driver.fillForm(updatedClient)
        driver.saveButton.click()

        driver.assertListContains(updatedClient)
        driver.assertListSelected(updatedClient)
    }

    fun `Save without any name entered fails`() {
        val driver = clientDriver()

        driver.inputFirstName.text = ""
        driver.inputLastName.text = ""
        driver.inputJob.text = "test first name"

        assertThat("Expected save button to be enabled after changing some property (different from names)",
                driver.saveButton.isEnabled)

        driver.saveButton.clickAndDisposeDialog("Speichern Abbrechen", "Speichern abgebrochen")
    }

    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="cancel">
    fun cancelInsertClient_shouldClearAllFields() {
        val driver = clientDriver()

        assertThat(driver.inputFirstName.textIsEmpty())
        assertThat(driver.inputLastName.textIsEmpty())

        driver.inputFirstName.text = client.firstName
        driver.inputLastName.text = client.lastName

        driver.cancelButton.click()

        assertThat(driver.inputFirstName.textIsEmpty())
        assertThat(driver.inputLastName.textIsEmpty())
    }


    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="delete">

//    @Test(dependsOnMethods = arrayOf("saveClient_sunshine"))
    fun deleteClient_sunshine() {
        val driver = clientDriver()

        driver.saveNewClient(client)
        driver.deleteClient(client)

        driver.assertListNotContains(client)
    }

    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="create new">

    @Test(dependsOnMethods = arrayOf("saveClient_sunshine"))
    fun createNewClientRequest_shouldDeselectEntryInMasterList() {
        val driver = clientDriver()

        driver.saveNewClient(client)
        driver.assertListSelected(client)

        driver.createButton.click()
        driver.assertListSelectionEmpty()
    }

    fun `When hit create button, then the first name textfield should have focus`() {
        skip("focus works in real app, but does not work in ui test :(")
        val driver = clientDriver()

        driver.createButton.click()
        driver.assertHasFocus(driver.inputFirstName)
    }

    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="check changes">

    // same applies for already saved client
    // and also when hit the cancel button
    fun buttonsDisabledIfThereAreNoChangesForEmptyClient() {
        val driver = clientDriver()

        driver.assertNoChangesDetected()

        driver.inputFirstName.text = "changed"
        driver.assertChangesDetected()

        driver.inputFirstName.text = ""

        driver.assertNoChangesDetected()
    }

    fun checkUnsavedChanges_createButton_newClient() {
        val driver = clientDriver()

        driver.inputFirstName.text = "foo"
        driver.createButton.clickAndDisposeDialog("Abbrechen")
    }

    fun checkUnsavedChanges_createButton_newClient_save() {
        val driver = clientDriver()

        driver.inputFirstName.text = client.firstName
        driver.inputLastName.text = client.lastName
        driver.createButton.clickAndDisposeDialog("Speichern")

        driver.assertListContains(client)
    }

    fun checkUnsavedChanges_createButton_existingClient() {
        val driver = clientDriver()

        driver.saveNewClient(client)
        driver.inputFirstName.text = "something else"

        driver.createButton.clickAndDisposeDialog("Abbrechen")
    }

    fun checkUnsavedChanges_selectDifferentInList_forNewClient() {
        val driver = clientDriver()

        driver.saveNewClient(client)

        driver.createButton.click()
        driver.inputFirstName.text = "foo"

        driver.triggerDialogAndClick({ driver.selectList(client) }, "Abbrechen")
    }

    fun checkUnsavedChanges_selectDifferentInList_existingClient() {
        val driver = clientDriver()

        driver.inputFirstName.text = "foo"
        driver.createButton.clickAndDisposeDialog("Abbrechen")
    }

    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="image">

    // check changes
    fun `When changing picture, then save button should be enabled`() {
        skip("changing image has changed") // MINOR TEST reenable picture test
        val driver = clientDriver()
        assertThat(not(driver.saveButton.isEnabled)) // sanity check

        driver.changeImage()

        assertThat("Expected save button to be enabled after changing the image!", driver.saveButton.isEnabled)
    }

    fun `When changing image, then image content should have been changed`() {
        skip("image handling has changed :(") // MINOR re-enable ui test
//        val driver = clientDriver()
//        val oldImage = driver.readImage()
//
//        driver.changeImage()
//
//        val newImage = driver.readImage()
//        assertFalse("Expected image to have changed but was still the same as before changing it!",
//                Objects.equals(oldImage, newImage))
    }

    //</editor-fold>

}

