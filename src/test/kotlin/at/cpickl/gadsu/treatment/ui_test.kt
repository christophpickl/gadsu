package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.savedValidInstance
import at.cpickl.gadsu.testinfra.UiTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test(groups = arrayOf("uiTest"))
class TreatmentUiTest : UiTest() {

    private var client = Client.savedValidInstance()
    private var treatment = Treatment.unsavedValidInstance(client.id!!)
    private var treatmentMini = treatment.toMini()

    @BeforeMethod
    fun resetState() {
        if (treatmentDriver().windowContainsMainPanel()) {
            treatmentDriver().backButton.click()
        }
        clientDriver().createButton.click() // reset client form
    }

    // --------------------------------------------------------------------------- create button state

    fun `New treatment button only enabled when client is selected`() {
        assertThat("Expected new treatment button to be disabled at startup!",
                not(treatmentDriver().openNewButton.isEnabled))

        clientDriver().saveClient(client)

        assertThat("Expected new treatment button to be enabled after creating a new client!",
                treatmentDriver().openNewButton.isEnabled)

        clientDriver().createButton.click()

        assertThat("Expected new treatment button to be disabled when creating new client!",
                not(treatmentDriver().openNewButton.isEnabled))
    }

    // --------------------------------------------------------------------------- open new

    fun `Given user is selected, when hit new treatment button, then panel should be displayed`() {
        clientDriver().saveClient(client)

        treatmentDriver().openNewButton.click()
        treatmentDriver().assertPanelVisible()
    }

    // --------------------------------------------------------------------------- back button

    fun `Given creating new treatment, hitting back button leads to client view again`() {
        clientDriver().saveClient(client)

        treatmentDriver().openNewButton.click()
        treatmentDriver().backButton.click()

        clientDriver().assertPanelVisible()
    }

    // --------------------------------------------------------------------------- save

    fun `When creating a new treatment, then it shows up in the client view table for treatments`() {
        val driver = treatmentDriver()
        mainDriver().createClientAndTreatment(client, treatmentMini)
        driver.backButton.click()

        driver.assertTreatmentsInClientViewContains(1) // MINOR improve assertion
    }

    fun `When creating a new treatment, then the button labels change`() {
        val driver = treatmentDriver()

        clientDriver().saveClient(client)
        driver.openNewButton.click()
        driver.assertSaveButtonLabel("Neu anlegen")

        driver.saveButton.click()
        driver.assertSaveButtonLabel("Speichern")
    }


    fun `Given a saved treatment, when updating it,contents are stored and will show up again when doubleclicking on it in the client view`() {
        val driver = treatmentDriver()

        mainDriver().createClientAndTreatment(client, treatmentMini)

        val newNote = "some other note from test"
        driver.inputNote.text = newNote
        driver.saveButton.click()

        MatcherAssert.assertThat(driver.inputNote.text, equalTo(newNote))
        driver.backButton.click()

        driver.openTreatment(treatmentMini)
        MatcherAssert.assertThat(driver.inputNote.text, equalTo(newNote))
        // MINOR add some more asserts, after TreatmentMini got some more values
    }


    // --------------------------------------------------------------------------- check changes

    fun `Save button is enabled when inserting new, so it is allowed to save an empty one`() {
        val driver = treatmentDriver()
        clientDriver().saveClient(client)

        driver.openNewButton.click()

        assertThat(driver.saveButton.isEnabled)
    }

    fun `Given a fresh inserted treatment, then the save button should be disabled, but be enabled when changing some property`() {
        val driver = treatmentDriver()

        mainDriver().createClientAndTreatment(client, treatmentMini)
        assertThat("Not any changes, therefor save button should be disabled!",
                not(driver.saveButton.isEnabled))

        driver.inputNote.text = "some other note from test"
        assertThat("Expected save button to be enabled after changing the note!",
                driver.saveButton.isEnabled)

        // and check save again as well ;)
        driver.saveButton.click()
        assertThat("Changes have been saved, therefor save button should be disabled!",
                not(driver.saveButton.isEnabled))
    }

    // MINOR test create new treatment and insert, update it, then check if updates propagated to treatment table in client view

}

data class TreatmentMini(val number: Int, val note: String)

fun Treatment.toMini() = TreatmentMini(number, note)
