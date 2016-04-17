package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.savedValidInstance
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.view.ViewNames
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.Button
import org.uispec4j.Panel
import org.uispec4j.Window

// good UI test code sample: https://github.com/UISpec4J/UISpec4J/blob/master/uispec4j/src/test/java/org/uispec4j/PanelTest.java
class TreatmentDriver(private val test: UiTest, private val window: Window) {

    // in client view
    val openNewButton = window.getButton(ViewNames.Treatment.OpenNewButton)


    // main treatment view. fields must be deferred as might not been added yet.
    val backButton: Button get() = window.getButton(ViewNames.Treatment.BackButton)
    val mainPanel: Panel get() = window.getPanel(ViewNames.Treatment.MainPanel)

    fun windowContainsMainPanel() = window.findUIComponent(Panel::class.java, ViewNames.Treatment.MainPanel) != null

    fun assertPanelVisible() {
        test.assertPanelContainedInMainWindow(ViewNames.Treatment.MainPanel)
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

}