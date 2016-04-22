package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.savedValidInstance
import at.cpickl.gadsu.testinfra.LogTestListener
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.testinfra.skip
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.equalTo
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Listeners
import org.testng.annotations.Test

@Test(groups = arrayOf("uiTest"))
@Listeners(LogTestListener::class)
class TreatmentUiTest : UiTest() {

    private var client = Client.savedValidInstance()
    private var treatment = Treatment.unsavedValidInstance(client.id!!)


    @BeforeMethod
    fun resetState() {
        if (treatmentDriver().windowContainsMainPanel()) {
            treatmentDriver().backButton.click() // MINOR this could lead to a "save confirmation dialog" if there have been any changes
        }
        clientDriver().createButton.click() // reset client form
    }


    // --------------------------------------------------------------------------- create button

    fun `New treatment button only enabled when client is selected`() {
        assertThat("Expected new treatment button to be disabled at startup!",
                not(treatmentDriver().openNewButton.isEnabled))

        saveClient(client)

        assertThat("Expected new treatment button to be enabled after creating a new client!",
                treatmentDriver().openNewButton.isEnabled)

        clientDriver().createButton.click()

        assertThat("Expected new treatment button to be disabled when creating new client!",
                not(treatmentDriver().openNewButton.isEnabled))
    }

    fun `Given client with a treatment, when hit create button, then the treatment list should be empty`() {
        val driver = clientDriver()
        saveClient(client)
        treatmentDriver().save(treatment = Treatment.uiInstance(note = "should not be here note 1"), returnToClientView = true)

        driver.createButton.click()

        treatmentDriver().assertTreatmentsListEmpty()
    }

    // --------------------------------------------------------------------------- open new

    fun `Given user is selected, when hit new treatment button, then panel should be displayed`() {
        saveClient(client)

        treatmentDriver().openNewButton.click()
        treatmentDriver().assertPanelVisible()
    }

    // --------------------------------------------------------------------------- back button

    fun `Given creating new treatment, hitting back button leads to client view again`() {
        saveClient(client)

        treatmentDriver().openNewButton.click()
        treatmentDriver().backButton.click()

        clientDriver().assertPanelVisible()
    }

    fun `Given datepicker popup opened by button, when hit back, then the date select panel should not be visible anymore`() {
        skip("works perfectly isolated, but fails when whole test class is executed!")
        val driver = treatmentDriver()
        saveClient(client)

        driver.openNewButton.click()
        driver.inputData.openPopupByButton({ popup, jwindow, panel ->
            assertThat(popup.isVisible)
            Assert.assertTrue(jwindow.isVisible, "Expected the treatment date picker popup to be invisible!")

            driver.backButton.click()

            Assert.assertFalse(jwindow.isVisible, "Expected the treatment date picker popup to be invisible!")
        })
    }

    // ---------------------------------------------------------------------------

    //<editor-fold desc="save">
    fun `When creating a new treatment, then it shows up in the client view table for treatments`() {
        val driver = treatmentDriver()
        mainDriver().createClientAndTreatment(client, treatment)
        driver.backButton.click()

        driver.assertTreatmentsInClientViewContains(1) // MINOR improve assertion
    }

    fun `When creating a new treatment, then the button labels change`() {
        val driver = treatmentDriver()

        saveClient(client)
        driver.openNewButton.click()
        driver.assertSaveButtonLabel("Neu anlegen")

        driver.saveButton.click()
        driver.assertSaveButtonLabel("Speichern")
    }


    fun `Given a saved treatment, when updating it,contents are stored and will show up again when doubleclicking on it in the client view`() {
        val driver = treatmentDriver()

        mainDriver().createClientAndTreatment(client, treatment)

        val newNote = "some other note from test"
        driver.inputNote.text = newNote
        driver.saveButton.click()

        MatcherAssert.assertThat(driver.inputNote.text, equalTo(newNote))
        driver.backButton.click()

        driver.openTreatment(treatment.number)
        MatcherAssert.assertThat(driver.inputNote.text, equalTo(newNote))
        // MINOR add some more asserts, after TreatmentMini got some more values
    }
    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="number generation">

    fun `reacalcNumber, full fledged test`() {
        skip("again a uispec4j bug with Rectangle rect = jList.getCellBounds(row, row); (ListBox#rightClick)")
        val driver = treatmentDriver()
        saveClient(client)

        val treatment1 = treatment.copy(note = "note1", number = 1) // TODO somehow get back the real treatment instance via driver (has ID and number properly set!)
        val treatment2 = treatment.copy(note = "note2", number = 2)
        val treatment3 = treatment.copy(note = "note3", number = 3)
        driver.save(treatment = treatment1, returnToClientView = true)
        driver.save(treatment = treatment2, returnToClientView = true)
        driver.save(treatment = treatment3, returnToClientView = true)

        driver.assertTreatmentsListContains(1, 2, 3) // FIXME reverse order!!!

        driver.deleteTreatment(treatment2)

        driver.assertTreatmentsListContains(1, 2)

        driver.assertTreatmentListAt(index = 1, expectedNote = treatment1.note)
        driver.assertTreatmentListAt(index = 2, expectedNote = treatment3.note)
    }


    fun `Given a fresh inserted treatment, then the save button should be disabled, but be enabled when changing some property`() {
        val driver = treatmentDriver()

        mainDriver().createClientAndTreatment(client, treatment)
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
    //</editor-fold>

    // --------------------------------------------------------------------------- check changes

    fun `Save button is enabled when inserting new, so it is allowed to save an empty one`() {
        val driver = treatmentDriver()
        saveClient(client)

        driver.openNewButton.click()

        assertThat(driver.saveButton.isEnabled)
    }

    // MINOR test create new treatment and insert, update it, then check if updates propagated to treatment table in client view

}


