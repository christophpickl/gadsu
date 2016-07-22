package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.ClosableWindow
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.closeOnEscape
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

interface MultiProtocolWindow : ClosableWindow {
    fun start(protocolizableTreatments: Int)
}

class MultiProtocolSwingWindow @Inject constructor(
        private val mainFrame: MainFrame,
        //        private val bus: EventBus,
        swing: SwingFactory
) : MyFrame("Sammelprotokoll"), MultiProtocolWindow {

    private val log = LoggerFactory.getLogger(javaClass)

    private val btnCreate = swing.newEventButton("Drucken", "TODO", { ReallyCreateMultiProtocolEvent() })
    private val lblProtocolizableTreatments = JLabel()
    private val btnTestPrint = swing.newEventButton("Testdruck", "TODO", { TestCreateMultiProtocolEvent() })

    init {
        log.debug("creating new window instance (should be a prototype and received via a provider by controller.")
        closeOnEscape()
        addCloseListener { closeWindow() }
        isResizable = false

        contentPane.layout = BorderLayout()
        contentPane.add(mainPanel(), BorderLayout.CENTER)
        contentPane.add(buttonPanel(), BorderLayout.SOUTH)
    }

    override fun start(protocolizableTreatments: Int) {
        lblProtocolizableTreatments.text = protocolizableTreatments.toString()

        if (protocolizableTreatments == 0) {
            btnCreate.isEnabled = false
            btnTestPrint.isEnabled = false
        }

        pack()
        setLocationRelativeTo(mainFrame.asJFrame())
        isVisible = true
    }

    override fun closeWindow() {
        hideAndClose()
    }

    private fun mainPanel() = GridPanel().apply {
        add(JLabel("Anzahl druckbare Protokolle: "))
        c.gridx++
        add(lblProtocolizableTreatments)

        c.gridx = 0
        c.gridy++
        c.gridwidth = 2
        add(btnTestPrint)
    }

    private fun buttonPanel() = JPanel().apply {
        add(btnCreate)
        add(JButton("Abbrechen").apply { addActionListener { closeWindow() } })
    }

}
