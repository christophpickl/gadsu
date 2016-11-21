package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.MyTable
import at.cpickl.gadsu.view.components.MyTableModel
import at.cpickl.gadsu.view.components.TableColumn
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.ClosableWindow
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.registerCloseOnEscape
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

interface SyncReportWindow : ClosableWindow {
    fun start()
    fun destroy()
    fun initReport(report: SyncReport, clients: List<Client>)
    fun readSelectedEvents(): List<ImportAppointment>
}

data class ImportAppointment(
        val event: GCalEvent,
        var enabled: Boolean,
        var selectedClient: Client
)

class SyncReportSwingWindow
@Inject constructor(
        bus: EventBus
)
    : MyFrame("Sync Bericht"), SyncReportWindow {
    private val model = MyTableModel<ImportAppointment>(listOf(
            TableColumn("", 30, { it.enabled }),
            TableColumn("Titel", 60, { it.event.summary }),
            TableColumn("Client", 60, { it.selectedClient.fullName })
    ))
    private val table = MyTable<ImportAppointment>(model, "SyncReportSwingWindow.table")

    init {
        addCloseListener { closeWindow() }
        registerCloseOnEscape()

        contentPane.add(GridPanel().apply {

            c.weightx = 1.0
            c.weighty = 0.0
            c.fill = GridBagConstraints.HORIZONTAL
            add(JLabel("Ergebnis:"))

            c.gridy++
            c.weighty = 1.0
            c.fill = GridBagConstraints.BOTH
            add(table.scrolled())

            c.gridy++
            c.weighty = 0.0
            c.fill = GridBagConstraints.NONE
            add(JPanel(BorderLayout()).apply {
                add(JButton("Import").apply { addActionListener { bus.post(RequestImportSyncEvent()) } }, BorderLayout.WEST)
                add(JButton("Abbrechen").apply { addActionListener { closeWindow() } }, BorderLayout.EAST)
            })
        })

    }

    override fun readSelectedEvents() = model.getData()

    // proper starting, see PreferencesWindow (MINOR outsource logic!)
    override fun start() {
        isVisible = true
    }

    override fun initReport(report: SyncReport, clients: List<Client>) {
        val defaultSelected = clients.first()
        // FIXME create JDropDown by given list here!
        model.resetData(report.eventsAndClients.map {
            ImportAppointment(it.key, true, it.value.firstOrNull() ?: defaultSelected)
        })
    }

    override fun closeWindow() {
        isVisible = false
    }

    override fun destroy() {
        isVisible = false
        dispose()
    }

}
