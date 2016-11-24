package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.components.MyFrame
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
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel


interface SyncReportWindow : ClosableWindow {
    fun start()
    fun destroy()
    fun initReport(report: SyncReport, clients: List<Client>)
    fun readImportAppointments(): List<ImportAppointment>
}

data class ImportAppointment(
        val event: GCalEvent,
        var enabled: Boolean,
        var selectedClient: Client,
        val allClients: List<Client> // order is specific to this appointment
)

class SyncReportSwingWindow
@Inject constructor(
        private val mainFrame: MainFrame,
        bus: EventBus
)
    : MyFrame("Sync Bericht"), SyncReportWindow {
    private val model = MyTableModel<ImportAppointment>(listOf(
            TableColumn("", 30, { it.enabled }),
            TableColumn("Titel", 60, { it.event.summary }),
            TableColumn("Client", 60, { it.selectedClient.fullName })
    ))
    private val table = SyncTable(model)
    private val btnImport = JButton("Import").apply { addActionListener { bus.post(RequestImportSyncEvent()) } }

    init {
        addCloseListener { closeWindow() }
        registerCloseOnEscape()

        contentPane.add(GridPanel().apply {
            border = BorderFactory.createEmptyBorder(10, 15, 10, 15)

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
                add(btnImport, BorderLayout.WEST)
                add(JButton("Abbrechen").apply { addActionListener { closeWindow() } }, BorderLayout.EAST)
            })
        })
        pack()
        setLocationRelativeTo(mainFrame.asJFrame())

        rootPane.defaultButton = btnImport
    }

    override fun readImportAppointments() = model.getData()

    // proper starting, see PreferencesWindow (MINOR outsource logic!)
    override fun start() {
        isVisible = true
    }

    override fun initReport(report: SyncReport, clients: List<Client>) {
        val defaultSelected = clients.first()
        model.resetData(report.eventsAndClients.map {
            ImportAppointment(it.key, true, it.value.firstOrNull() ?: defaultSelected, clientsOrdered(it.value, clients))
        })
    }

    private fun clientsOrdered(topClients: List<Client>, allClients: List<Client>) =
            topClients.union(allClients.minus(topClients)).toList()

    override fun closeWindow() {
        isVisible = false
    }

    override fun destroy() {
        isVisible = false
        dispose()
    }

}
