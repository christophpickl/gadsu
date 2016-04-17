package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.savedValidInstance
import at.cpickl.gadsu.testinfra.BaseDriver
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.view.ViewNames
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.Button
import org.uispec4j.Panel
import org.uispec4j.Window

// good UI test code sample: https://github.com/UISpec4J/UISpec4J/blob/master/uispec4j/src/test/java/org/uispec4j/PanelTest.java
class TreatmentDriver(test: UiTest, window: Window) : BaseDriver(test, window) {

    // in client view
    val openNewButton = window.getButton(ViewNames.Treatment.OpenNewButton)
    val treatmentsInClientViewTable = window.getTable(ViewNames.Treatment.TableInClientView)

    // main treatment view. field access must be deferred as not yet added to the view.
    val saveButton: Button get() = window.getButton(ViewNames.Treatment.SaveButton)
    val backButton: Button get() = window.getButton(ViewNames.Treatment.BackButton)
    val mainPanel: Panel get() = window.getPanel(ViewNames.Treatment.MainPanel)

    fun windowContainsMainPanel() = window.findUIComponent(Panel::class.java, ViewNames.Treatment.MainPanel) != null

    fun assertPanelVisible() {
        test.assertPanelContainedInMainWindow(ViewNames.Treatment.MainPanel)
    }

    fun saveDummyTreatment() {
        // nothing to fill in ATM ...
        saveButton.click()
    }

    fun assertTreatmentsInClientViewContains(expectedRows: Int) {
        MatcherAssert.assertThat(treatmentsInClientViewTable.rowCount, Matchers.equalTo(expectedRows))
    }

}

@Test(groups = arrayOf("uiTest"))
class TreatmentUiTest : UiTest() {

    private var client = Client.savedValidInstance()
    private var treatment = Treatment.unsavedValidInstance(client.id!!)

    @BeforeMethod
    fun resetState() {
        if (treatmentDriver().windowContainsMainPanel()) {
            treatmentDriver().backButton.click()
        }
        clientDriver().createButton.click() // reset client form
    }

    fun `New treatment button only enabled when client is selected`() {
        assertThat("Expected new treatment button to be disabled at startup!",
                not(treatmentDriver().openNewButton.isEnabled))

        clientDriver().saveClient(client)
        assertThat("Expected new treatment button to be enabled after creating a new client!", treatmentDriver().openNewButton.isEnabled)

        clientDriver().createButton.click()
        assertThat("Expected new treatment button to be disabled when creating new client!", not(treatmentDriver().openNewButton.isEnabled))
    }

    fun `Given user is selected, when hit new treatment button, then panel should be displayed`() {
        clientDriver().saveClient(client)

        treatmentDriver().openNewButton.click()
        treatmentDriver().assertPanelVisible()
    }

    fun `Given creating new treatment, hitting back button leads to client view again`() {
        clientDriver().saveClient(client)

        treatmentDriver().openNewButton.click()
        treatmentDriver().backButton.click()

        clientDriver().assertPanelVisible()
    }

    fun `When creating a new treatment, then it shows up in the client view table for treatments`() {
        clientDriver().saveClient(client)
        treatmentDriver().assertTreatmentsInClientViewContains(0) // sanity check

        treatmentDriver().openNewButton.click()
        treatmentDriver().saveDummyTreatment()
        treatmentDriver().backButton.click()

        treatmentDriver().assertTreatmentsInClientViewContains(1) // MINOR improve assertion
    }

}