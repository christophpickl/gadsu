package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentDeletedEvent
import at.cpickl.gadsu.treatment.TreatmentRepository
import at.cpickl.gadsu.view.components.MyTable
import at.cpickl.gadsu.view.components.MyTableModel
import at.cpickl.gadsu.view.components.TableColumn
import at.cpickl.gadsu.view.components.calculateInsertIndex
import at.cpickl.gadsu.view.components.scrolled
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JLabel
import javax.swing.JPanel


class TreatmentTableController @Inject constructor(
        private val table: TreatmentTable,
        private val repository: TreatmentRepository
) {
    private var recentClient: Client? = null

    @Subscribe fun onClientSelectedEvent(event: ClientSelectedEvent) {
        recentClient = event.client
        table.initData(repository.findAllFor(event.client))
    }

    @Subscribe fun onTreatmentCreatedEvent(event: TreatmentCreatedEvent) {
        if (!event.treatment.clientId.equals(recentClient?.id)) {
            return
        }
        table.insert(event.treatment)
    }

    @Subscribe fun onTreatmentDeletedEvent(event: TreatmentDeletedEvent) {
        if (!event.treatment.clientId.equals(recentClient?.id)) {
            return
        }
        table.delete(event.treatment)
    }

    // FIXME onTreatmentChanged

}


class TreatmentTable : JPanel() {
    private val log = LoggerFactory.getLogger(javaClass)

    // TODO :
    // set view name
    // inject event bus
    // register double click
    // right click popup
    private val model = MyTableModel<Treatment>(listOf(
            TableColumn<Treatment>("Nr", 20, { it.number }),
            TableColumn<Treatment>("Datum", 100, { DateFormats.DATE_TIME.print(it.date) })
    ))

    private val table = MyTable<Treatment>(model)
    init {
        layout = BorderLayout()
        add(JLabel("Behandlungen"), BorderLayout.NORTH)
        add(table.scrolled(), BorderLayout.CENTER)
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
