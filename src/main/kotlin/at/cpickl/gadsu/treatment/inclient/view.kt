package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.treatment.CreateTreatmentEvent
import at.cpickl.gadsu.treatment.DeleteTreatmentEvent
import at.cpickl.gadsu.treatment.OpenTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.calculateInsertIndex
import at.cpickl.gadsu.view.components.enablePopup
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.registerDoubleClicked
import at.cpickl.gadsu.view.components.scrolled
import com.google.common.eventbus.EventBus
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JList
import javax.swing.JPanel


fun main(args: Array<String>) {
    Framed.showWithContext({
        val view = TreatmentsInClientView(it.swing, it.bus)
        view.enableData(listOf(
                Treatment.insertPrototype("1", 1, DateTime.now()),
                Treatment.insertPrototype("2", 2, DateTime.now().plusDays(1)),
                Treatment.insertPrototype("3", 3, DateTime.now().plusDays(2))
        ))
        view
    })
}

class TreatmentsInClientView @Inject constructor(
        private val swing: SwingFactory,
        private val bus: EventBus
): JPanel() {
    private val log = LoggerFactory.getLogger(javaClass)

    private val newTreatmentButton = swing.newEventButton("Neue Behandlung anlegen", ViewNames.Treatment.OpenNewButton, { CreateTreatmentEvent() })


    private val treatmentsModel = MyListModel<Treatment>()
    private val treatmentsView = JList<Treatment>(treatmentsModel)


    init {
        debugColor = Color.RED
        layout = BorderLayout()
        newTreatmentButton.isEnabled = false // disabled by default at startup

        treatmentsView.name = ViewNames.Treatment.ListInClientView
        treatmentsView.enablePopup(bus, Pair<String, (Treatment) -> UserEvent>("L\u00f6schen", { DeleteTreatmentEvent(it) }))
        treatmentsView.cellRenderer = TreatmentListCellRenderer()
        treatmentsView.registerDoubleClicked { row, treatment -> bus.post(OpenTreatmentEvent(treatment)) }
        add(treatmentsView.scrolled(), BorderLayout.CENTER)

        val btnPanel = GridPanel()
        btnPanel.debugColor = Color.ORANGE
        btnPanel.c.weightx = 1.0
        btnPanel.c.fill = GridBagConstraints.HORIZONTAL
        btnPanel.add(newTreatmentButton)
        add(btnPanel, BorderLayout.SOUTH)
    }

    fun insert(treatment: Treatment) {
        log.trace("insert(treatment={})", treatment)
        val index = treatmentsModel.calculateInsertIndex(treatment)
        treatmentsModel.add(index, treatment)
    }

    fun delete(treatment: Treatment) {
        log.trace("delete(treatment={})", treatment)
        treatmentsModel.removeElementByComparator(treatment.idComparator)
    }

    fun change(treatment: Treatment) {
        log.trace("change(treatment={})", treatment)
        treatmentsModel.setElementByComparator(treatment, treatment.idComparator)
    }

    fun enableData(treatments: List<Treatment>) {
        log.trace("initData(treatments={})", treatments)

        treatmentsModel.resetData(treatments)
        newTreatmentButton.isEnabled = true
    }

    fun disableData() {
        log.debug("disableData()")

        treatmentsModel.clear()
        newTreatmentButton.isEnabled = false
    }

}
