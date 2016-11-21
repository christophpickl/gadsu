package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.service.formatDateTime
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.ClosableWindow
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.closeOnEscape
import at.cpickl.gadsu.view.swing.scrolled
import java.awt.GridBagConstraints
import javax.swing.JButton
import javax.swing.JLabel

interface SyncReportWindow {
    fun start()
    fun destroy()
    fun initReport(report: SyncReport)
}

class SyncReportSwingWindow
//@Inject constructor(
//        bus: EventBus,
//        swing: SwingFactory
//)
    : MyFrame("Sync Bericht"), SyncReportWindow, ClosableWindow {

    private val txtFoobar = MyTextArea("foobar", 20)

    init {
        addCloseListener { closeWindow() }
        closeOnEscape()

        contentPane.add(GridPanel().apply {

            c.fill = GridBagConstraints.NONE
            add(JLabel("Ergebnis:"))

            c.gridy++
            c.fill = GridBagConstraints.BOTH
            add(txtFoobar.scrolled())

            c.gridy++
            c.fill = GridBagConstraints.NONE
            add(JButton("Abbrechen").apply { addActionListener { closeWindow() } })
        })

    }

    // proper starting, see PreferencesWindow (MINOR outsource logic!)
    override fun start() {
        isVisible = true
    }

    override fun initReport(report: SyncReport) {
        txtFoobar.text = report.eventsAndClients.map {
            "- ${it.key.summary} @ ${it.key.start.formatDateTime()}. suggestions: " +
                    it.value.map { "${it.firstName} ${it.nickName} ${it.lastName}" }.joinToString(", ")
        }.joinToString("\n")
    }

    override fun closeWindow() {
        isVisible = false
    }

    override fun destroy() {
        isVisible = false
        dispose()
    }

}
