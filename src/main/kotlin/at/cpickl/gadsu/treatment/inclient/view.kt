package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.debugColor
import at.cpickl.gadsu.service.formatDateTime
import at.cpickl.gadsu.treatment.CreateTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.MyTable
import at.cpickl.gadsu.view.components.MyTableModel
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.TableColumn
import at.cpickl.gadsu.view.components.calculateInsertIndex
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.scrolled
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import javax.inject.Inject
import javax.swing.JLabel
import javax.swing.JPanel


class TreatmentsInClientView @Inject constructor(
        private val swing: SwingFactory
): JPanel() {
    private val log = LoggerFactory.getLogger(javaClass)

    private val newTreatmentButton = swing.newEventButton("Neue Behandlung", ViewNames.Treatment.OpenNewButton, { CreateTreatmentEvent() })

    // TODO TreatmentsInClientView
    // set view name
    // inject event bus
    // register double click
    // right click popup
    private val model = MyTableModel<Treatment>(listOf(
            TableColumn<Treatment>("Nr", 20, { it.number }),
            TableColumn<Treatment>("Datum", 100, { it.date.formatDateTime() })
    ))

    private val table = MyTable<Treatment>(model)
    init {
        debugColor = Color.RED
        layout = BorderLayout()
        newTreatmentButton.isEnabled = false

        add(JLabel("Behandlungen"), BorderLayout.NORTH)
        add(table.scrolled(), BorderLayout.CENTER)

        val btnPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        btnPanel.debugColor = Color.ORANGE

        btnPanel.add(newTreatmentButton)
        add(btnPanel, BorderLayout.SOUTH)
    }

    /**
     * By default at startup disabled.
     */
    fun enableNewButton(value: Boolean) {
        newTreatmentButton.isEnabled = value
    }

    fun initData(treatments: List<Treatment>) {
        log.trace("initData(treatments={})", treatments)
        model.resetData(treatments)
    }

    fun insert(treatment: Treatment) {
        log.trace("insert(treatment={})", treatment)
        val index = model.calculateInsertIndex(treatment)
        model.addElementAt(index, treatment)
    }

    fun delete(treatment: Treatment) {
        log.trace("delete(treatment={})", treatment)
        model.removeElementByComparator(treatment.idComparator)
    }

    // not yet used
    fun change(treatment: Treatment) {
        log.trace("change(treatment={})", treatment)
        model.setElementByComparator(treatment, treatment.idComparator)
    }

}
