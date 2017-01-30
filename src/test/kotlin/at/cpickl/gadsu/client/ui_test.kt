package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.view.detail.ClientTabType
import at.cpickl.gadsu.testinfra.IS_TRAVIS
import at.cpickl.gadsu.testinfra.TEST_DATETIME2
import at.cpickl.gadsu.testinfra.skip
import at.cpickl.gadsu.testinfra.ui.UiTest
import at.cpickl.gadsu.testinfra.ui.clickAndDisposeDialog
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.lang.reflect.Method


@Test(groups = arrayOf("uiTest"))
class ClientUiTest : UiTest() {

    private var client = Client.unsavedValidInstance()

    private val driver: ClientDriver get() = clientDriver

    @BeforeMethod
    fun resetState(method: Method) {
        log.debug("resetState()")

        treatmentDriver.goBackIfIsTreatmentVisible()

        if (driver.cancelButton.awtComponent.isEnabled) {
            driver.cancelButton.click()
        }
        driver.createButton.click()

        // ensure each test is using a unique client instance
        client = Client.unsavedValidInstance().copy(firstName = javaClass.simpleName, lastName = method.name)
    }

    fun `birthday date panel is closed when creating new treatment`() {
        if (IS_TRAVIS) {
            skip("This test fails on travis only")
        } else {
            skip("Fails also locally, but executing this test manually succeeds :-/")
        }

        saveClient(client)
        driver.inputBirthdate.openPopupByButton { context ->
            context.assertPopupVisible(true)
            treatmentDriver.openNewButton.click()
            context.assertPopupVisible(false)
        }

    }


    // ---------------------------------------------------------------------------

    //<editor-fold desc="save">

    fun `save client should change save button text`() {
        with(driver) {
            assertSaveButtonTextEquals("Neu anlegen") // sanity check

            saveFullClient(client)
            assertSaveButtonTextEquals("Speichern")

            assertViewContains(client)
            assertListContains(client)
            assertListSelected(client)
        }
    }

    fun updateClient_shouldUpdateInListAsWell() {
        driver.fillNames(client)
        driver.saveButton.click()

        driver.assertListContains(client)
        driver.assertListSelected(client)

        val updatedClient = client.copy(firstName = "initial first name will be updated")
        driver.fillNames(updatedClient)
        driver.saveButton.click()

        driver.assertListContains(updatedClient)
        driver.assertListSelected(updatedClient)
    }

    fun `Save without any name entered fails`() {
        driver.inputFirstName.text = ""
        driver.inputLastName.text = ""
        driver.inputJob.text = "test first name"

        assertThat("Expected save button to be enabled after changing some property (different from names)",
                driver.saveButton.isEnabled)

        driver.saveButton.clickAndDisposeDialog("Speichern Abbrechen", "Speichern abgebrochen")
    }

    fun `changing TCM note and saving client should disable save button`() {
        saveClient(client)

        driver.changeTab(ClientTabType.TCM)
        assertFalse("Freshly saved client should not be savable!", driver.saveButton.isEnabled)
        driver.inputTcmNote.text = "something new"
        assertTrue("TCM note changed, client should be savable!", driver.saveButton.isEnabled)
        driver.saveButton.click()
        assertFalse("After saving client with changed TCM note, it should not be savable!", driver.saveButton.isEnabled)
    }

    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="cancel">
    fun cancelInsertClient_shouldClearAllFields() {
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
    fun `deleteClient sunshine`() {
        driver.saveBasicClient(client)

        mainDriver.menuBarDriver.deleteClient()

        driver.assertListNotContains(client)
    }

    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="create new">

    @Test(dependsOnMethods = arrayOf("saveClient_sunshine"))
    fun createNewClientRequest_shouldDeselectEntryInMasterList() {
        driver.saveBasicClient(client)
        driver.assertListSelected(client)

        driver.createButton.click()
        driver.assertListSelectionEmpty()
    }

    fun `When hit create button, then the first name textfield should have focus`() {
        skip("focus works in real app, but does not work in ui test :(")
        driver.createButton.click()
        driver.assertHasFocus(driver.inputFirstName)
    }

    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="check changes">

    // same applies for already saved client
    // and also when hit the cancel button
    fun buttonsDisabledIfThereAreNoChangesForEmptyClient() {
        driver.assertNoChangesDetected()

        driver.inputFirstName.text = "changed"
        driver.assertChangesDetected()

        driver.inputFirstName.text = ""

        driver.assertNoChangesDetected()
    }

    fun checkUnsavedChanges_createButton_newClient() {
        driver.inputFirstName.text = "foo"
        driver.createButton.clickAndDisposeDialog("Abbrechen")
    }

    fun checkUnsavedChanges_createButton_newClient_save() {
        driver.inputFirstName.text = client.firstName
        driver.inputLastName.text = client.lastName
        driver.createButton.clickAndDisposeDialog("Speichern")

        driver.assertListContains(client)
    }

    fun checkUnsavedChanges_createButton_existingClient() {
        driver.saveBasicClient(client)
        driver.inputFirstName.text = "something else"

        driver.createButton.clickAndDisposeDialog("Abbrechen")
    }

    fun checkUnsavedChanges_selectDifferentInList_forNewClient() {
        driver.saveBasicClient(client)

        driver.createButton.click()
        driver.inputFirstName.text = "foo"

        driver.triggerDialogAndClick({ driver.selectList(client) }, "Abbrechen")
    }

    fun checkUnsavedChanges_selectDifferentInList_existingClient() {
        driver.inputFirstName.text = "foo"
        driver.createButton.clickAndDisposeDialog("Abbrechen")
    }

    fun `checkChanges, when change birthday for new client, then save should be enabled (although not saveable as no name given)`() {
        driver.inputBirthdate.changeDate(TEST_DATETIME2.plusDays(17))
        driver.assertChangesDetected()
    }

    fun `checkChanges for full client`() {
        driver.saveFullClient(client)
        driver.createButton.click() // this should NOT trigger a popup!
    }

    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="image">

    // check changes
    fun `When changing picture, then save button should be enabled`() {
        skip("changing image has changed") // MINOR @TEST - reenable picture test
        assertThat(not(driver.saveButton.isEnabled)) // sanity check

        driver.changeImage()

        assertThat("Expected save button to be enabled after changing the image!", driver.saveButton.isEnabled)
    }

    fun `When changing image, then image content should have been changed`() {
        skip("image handling has changed :(") // MINOR @TEST - re-enable ui test
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

