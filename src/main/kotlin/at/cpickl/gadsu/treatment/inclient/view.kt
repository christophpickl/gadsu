package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.treatment.CreateTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.transparent
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JPanel


fun main(args: Array<String>) {
    Framed.showWithContext({
        val view = TreatmentsInClientView(it.swing, TreatmentList(it.bus))
        view.enableData(listOf(
                Treatment.insertPrototype("3", 3, DateTime.now().plusDays(2)),
                Treatment.insertPrototype("2", 2, DateTime.now().plusDays(1)),
                Treatment.insertPrototype("1", 1, DateTime.now().plusDays(0))
        ))
        view
    })
}

class TreatmentsInClientView @Inject constructor(
        private val swing: SwingFactory,
        private val treatmentsList: TreatmentList
): JPanel() {
    private val log = LoggerFactory.getLogger(javaClass)

    private val newTreatmentButton = swing.newEventButton("Neue Behandlung anlegen", ViewNames.Treatment.OpenNewButton, { CreateTreatmentEvent() })

    init {
        debugColor = Color.RED
        transparent()
        enforceWidth(250)
        layout = BorderLayout()
        newTreatmentButton.isEnabled = false // disabled by default at startup

        add(treatmentsList.scrolled(), BorderLayout.CENTER)

        val btnPanel = GridPanel()
        btnPanel.debugColor = Color.ORANGE
        btnPanel.c.weightx = 1.0
        btnPanel.c.fill = GridBagConstraints.HORIZONTAL
        btnPanel.add(newTreatmentButton)
        add(btnPanel, BorderLayout.SOUTH)
    }

    fun insert(treatment: Treatment) {
        log.trace("insert(treatment={})", treatment)
        treatmentsList.addProperIndex(treatment)
    }

    fun delete(treatment: Treatment) {
        log.trace("delete(treatment={})", treatment)
        treatmentsList.removeElementByComparator(treatment.idComparator)
    }

    fun change(treatment: Treatment) {
        log.trace("change(treatment={})", treatment)
        treatmentsList.setElementByComparator(treatment, treatment.idComparator)
    }

    fun enableData(treatments: List<Treatment>) {
        log.trace("initData(treatments={})", treatments)

        treatmentsList.resetData(treatments)
        newTreatmentButton.isEnabled = true
    }

    fun disableData() {
        log.debug("disableData()")

        treatmentsList.clear()
        newTreatmentButton.isEnabled = false
    }

}
