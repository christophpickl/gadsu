package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.savedValidInstance
import at.cpickl.gadsu.testinfra.LogTestListener
import at.cpickl.gadsu.testinfra.skip
import at.cpickl.gadsu.testinfra.ui.UiTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Listeners
import org.testng.annotations.Test

@Test(groups = arrayOf("uiTest"))
@Listeners(LogTestListener::class)
class TreatmentUiTest : UiTest() {

    private val driver: TreatmentDriver get() = treatmentDriver
    private var client = Client.savedValidInstance()
    private var treatment = Treatment.unsavedValidInstance(client.id!!)


    @BeforeMethod
    fun resetState() {
        driver.goBackIfIsTreatmentVisible()
        clientDriver.createButton.click() // reset client form
    }


    // --------------------------------------------------------------------------- create button

    fun `New treatment button only enabled when client is selected`() {
        assertThat("Expected new treatment button to be disabled at startup!",
                not(driver.openNewButton.isEnabled))

        saveClient(client)

        assertThat("Expected new treatment button to be enabled after creating a new client!",
                driver.openNewButton.isEnabled)

        clientDriver.createButton.click()

        assertThat("Expected new treatment button to be disabled when creating new client!",
                not(driver.openNewButton.isEnabled))
    }

    fun `Given client with a treatment, when hit create button, then the treatment list should be empty`() {

        saveClient(client)
        driver.save(treatment = Treatment.uiInstance(note = "should not be here note 1"), returnToClientView = true)

        clientDriver.createButton.click()

        driver.assertTreatmentsListEmpty()
    }

    // --------------------------------------------------------------------------- open new

    fun `Given user is selected, when hit new treatment button, then panel should be displayed`() {
        saveClient(client)

        driver.openNewButton.click()
        driver.assertPanelVisible()
    }

    // --------------------------------------------------------------------------- back button

    fun `Given creating new treatment, hitting back button leads to client view again`() {
        saveClient(client)

        driver.openNewButton.click()
        driver.backButton.click()

        clientDriver.assertPanelVisible()
    }

    fun `Given datepicker popup opened by button, when hit back, then the date select panel should not be visible anymore`() {
        skip("works perfectly isolated, but fails when whole test class is executed!")

        saveClient(client)
        driver.openNewButton.click()
        driver.inputDate.openPopupByButton({ context ->
            context.assertPopupVisible(true)
            driver.backButton.click()
            context.assertPopupVisible(false)
        })
    }


    // ---------------------------------------------------------------------------

    //<editor-fold desc="save">
    fun `When creating a new treatment, then it shows up in the client view table for treatments`() {
        mainDriver.createClientAndTreatment(client, treatment)
        driver.backButton.click()

        driver.assertTreatmentsInClientViewContains(1) // MINOR @TEST - improve assertion
    }

    fun `When creating a new treatment, then the button labels change`() {
        saveClient(client)
        driver.openNewButton.click()
        driver.assertSaveButtonLabel("Neu anlegen")

        driver.saveButton.click()
        driver.assertSaveButtonLabel("Speichern")
    }


    fun `Given a saved treatment, when updating it,contents are stored and will show up again when doubleclicking on it in the client view`() {
        mainDriver.createClientAndTreatment(client, treatment)

        val newNote = "some other note from test"
        driver.inputNote.text = newNote
        driver.saveButton.click()

        MatcherAssert.assertThat(driver.inputNote.text, equalTo(newNote))
        driver.backButton.click()

        driver.openTreatment(treatment.number)
        MatcherAssert.assertThat(driver.inputNote.text, equalTo(newNote))
        // MINOR @TEST - add some more asserts, after TreatmentMini got some more values
    }
    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="number generation">

    fun `reacalcNumber, full fledged test`() {
        skip("again a uispec4j bug with Rectangle rect = jList.getCellBounds(row, row); (ListBox#rightClick)")
        saveClient(client)

        // MINOR @TEST - somehow get back the real treatment instance via driver (has ID and number properly set!)
        val treatment1 = treatment.copy(note = "note1", number = 1)
        val treatment2 = treatment.copy(note = "note2", number = 2)
        val treatment3 = treatment.copy(note = "note3", number = 3)
        driver.save(treatment = treatment1, returnToClientView = true)
        driver.save(treatment = treatment2, returnToClientView = true)
        driver.save(treatment = treatment3, returnToClientView = true)

        driver.assertTreatmentsListContains(1, 2, 3) // FIXME @TEST - reverse order!!!

        driver.deleteTreatment(treatment2)

        driver.assertTreatmentsListContains(1, 2)

        driver.assertTreatmentListAt(index = 1, expectedNote = treatment1.note)
        driver.assertTreatmentListAt(index = 2, expectedNote = treatment3.note)
    }


    fun `Given a fresh inserted treatment, then the save button should be disabled, but be enabled when changing some property`() {
        mainDriver.createClientAndTreatment(client, treatment)
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
        saveClient(client)

        driver.openNewButton.click()

        assertThat(driver.saveButton.isEnabled)
    }

    // MINOR @TEST - create new treatment and insert, update it, then check if updates propagated to treatment table in client view

}


