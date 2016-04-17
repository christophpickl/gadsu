package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.testinfra.BaseDriver
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.view.ViewNames
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.slf4j.LoggerFactory
import org.uispec4j.Button
import org.uispec4j.Panel
import org.uispec4j.TextBox
import org.uispec4j.Window


class TreatmentDriver(test: UiTest, window: Window) : BaseDriver(test, window) {

    private val log = LoggerFactory.getLogger(javaClass)

    // in client view
    val openNewButton = window.getButton(ViewNames.Treatment.OpenNewButton)
    val treatmentsInClientViewTable = window.getTable(ViewNames.Treatment.TableInClientView)

    // main treatment view. field access must be deferred as not yet added to the view.
    val saveButton: Button get() = window.getButton(ViewNames.Treatment.SaveButton)
    val backButton: Button get() = window.getButton(ViewNames.Treatment.BackButton)
    val mainPanel: Panel get() = window.getPanel(ViewNames.Treatment.MainPanel)
    val inputNote: TextBox get() = window.getTextBox(ViewNames.Treatment.InputNote)

    fun windowContainsMainPanel() = window.findUIComponent(Panel::class.java, ViewNames.Treatment.MainPanel) != null

    fun assertPanelVisible() {
        test.assertPanelContainedInMainWindow(ViewNames.Treatment.MainPanel)
    }

    fun assertTreatmentsInClientViewContains(expectedRows: Int) {
        MatcherAssert.assertThat(treatmentsInClientViewTable.rowCount, Matchers.equalTo(expectedRows))
    }

    fun save(treatment: TreatmentMini) {
        // TODO change date as well (DatePicker aufbrechen und in die interne reingreifen mit static casts)
        inputNote.text = treatment.note
        saveButton.click()
    }

    fun openTreatment(treatment: TreatmentMini) {
        log.debug("openTreatment(treatment={})", treatment)
        treatmentsInClientViewTable.doubleClick(findTableRow(treatment), 0)
    }

    private fun findTableRow(treatment: TreatmentMini): Int {
        for (row in 0.rangeTo(treatmentsInClientViewTable.rowCount - 1)) {
            val number = treatmentsInClientViewTable.getContentAt(row, 0) as String
            if (number.equals(treatment.number.toString())) {
                return row
            }
        }
        throw GadsuException("Could not find index for treatment: $treatment!")
    }

}
