package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.testinfra.BaseDriver
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.testinfra.deleteAtRow
import at.cpickl.gadsu.treatment.inclient.TreatmentCell
import at.cpickl.gadsu.view.ViewNames
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.jdatepicker.impl.JDatePanelImpl
import org.slf4j.LoggerFactory
import org.uispec4j.Button
import org.uispec4j.Panel
import org.uispec4j.TextBox
import org.uispec4j.Trigger
import org.uispec4j.Window
import org.uispec4j.interception.WindowHandler
import org.uispec4j.interception.WindowInterceptor
import java.util.ArrayList
import javax.swing.JWindow


class TreatmentDriver(test: UiTest, window: Window) : BaseDriver(test, window) {

    private val log = LoggerFactory.getLogger(javaClass)

    // in client view
    val openNewButton = window.getButton(ViewNames.Treatment.OpenNewButton)
    val treatmentsList = window.getListBox(ViewNames.Treatment.ListInClientView)

    // main treatment view. field access must be deferred as not yet added to the view.
    val saveButton: Button get() = window.getButton(ViewNames.Treatment.SaveButton)
    val backButton: Button get() = window.getButton(ViewNames.Treatment.BackButton)
    val mainPanel: Panel get() = window.getPanel(ViewNames.Treatment.MainPanel)
    val inputNote: TextBox get() = window.getTextBox(ViewNames.Treatment.InputNote)
    val inputData: DateTimePicker get() = DateTimePicker(test, window,
            ViewNames.Treatment.InputDateButton, ViewNames.Treatment.InputDatePanel)

    fun windowContainsMainPanel() = window.findUIComponent(Panel::class.java, ViewNames.Treatment.MainPanel) != null

    fun assertPanelVisible() {
        test.assertPanelContainedInMainWindow(ViewNames.Treatment.MainPanel)
    }

    fun assertTreatmentsInClientViewContains(expectedRows: Int) {
        MatcherAssert.assertThat(treatmentsList.size, equalTo(expectedRows))
    }

    fun save(treatment: Treatment, returnToClientView: Boolean = false) {
        openNewButton.click()
        // TODO change date as well (DatePicker aufbrechen und in die interne reingreifen mit static casts)
        inputNote.text = treatment.note
        saveButton.click()

        if (returnToClientView) {
            backButton.click()
        }
    }

    fun openTreatment(number: Int) {
        log.debug("openTreatment(number={})", number)
        treatmentsList.doubleClick(findListRowForTreatmentNumber(number).second)
    }

    fun deleteTreatment(treatment: Treatment) {
        log.debug("deleteTreatment(treatment={})", treatment)
        treatmentsList.deleteAtRow(findListRowForTreatmentNumber(treatment.number).second)
    }

    fun assertSaveButtonLabel(expectedLabel: String) {
        MatcherAssert.assertThat(saveButton.label, equalTo(expectedLabel))
    }

    private fun listCellAt(row: Int): TreatmentCell {
        val rendering = treatmentsList.getSwingRendererComponentAt(row)
        if (rendering is TreatmentCell) {
            return rendering
        }
        throw AssertionError("Expecting treatments list rendered component to be a TreatmentCell, but was: ${rendering.javaClass.name} ($rendering)")
    }

    // MINOR reusable for client list!
    fun treatmentsListContent(): List<Pair<Treatment, Int>> {
        val list = ArrayList<Pair<Treatment, Int>>(treatmentsList.size)
        for (row in 0.rangeTo(treatmentsList.size - 1)) {
            val cell = listCellAt(row)
            list.add(Pair<Treatment, Int>(cell.treatment, row))
        }
        return list
    }


    // MINOR make reusable for client list (client resolved via using the direct table model... different approach!)
    private fun findListRowForTreatmentNumber(number: Int): Pair<Treatment, Int> {
        return treatmentsListContent().firstOrNull { it.first.number == number }
                ?: throw GadsuException("Could not find index for treatment number: $number!")
    }



    fun assertTreatmentsListContains(vararg expectedNumbers: Int) {
        val lists = treatmentsListContent()
        MatcherAssert.assertThat(lists, hasSize(3))
        MatcherAssert.assertThat(lists.map { it.first.number }.toIntArray(), equalTo(expectedNumbers))
    }

    fun assertTreatmentListAt(index: Int, expectedNote: String) {
        MatcherAssert.assertThat(listCellAt(index).treatment.note, equalTo(expectedNote))
    }

    fun assertTreatmentsListEmpty() {
        test.assertThat(treatmentsList.isEmpty)
    }

}

class DateTimePicker(private val test: UiTest,
                     private val window: Window,
                     buttonViewName: String,
                     private val panelViewName: String) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val openButton = window.getButton(buttonViewName)

    fun openPopupByButton(function: (Window, JWindow, JDatePanelImpl) -> Unit) {
        log.debug("openPopupByButton(function)")
        WindowInterceptor
            .init(openButton.triggerClick())
            .process(object : WindowHandler() {
                override fun process(dialog: Window): Trigger {
                    log.trace("process(dialog) date picker popup")
                    val popupContentRaw = (dialog.awtComponent as JWindow).rootPane.contentPane.getComponent(0)
                    if (popupContentRaw !is JDatePanelImpl) {
                        throw AssertionError("Expected popup's content to be a JDatePanelImpl, but was: ${popupContentRaw.javaClass.name} ($popupContentRaw)")
                    }
                    MatcherAssert.assertThat(popupContentRaw.name, equalTo(panelViewName))
                    function(dialog, dialog.awtComponent as JWindow, popupContentRaw)
                    return Trigger.DO_NOTHING
                }
            })
            .run()

    }

}
