package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.components.panels.VFillFormPanel
import at.cpickl.gadsu.view.swing.ClosableWindow
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.closeOnEscape
import at.cpickl.gadsu.view.swing.scrolled
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

interface MultiProtocolWindow : ClosableWindow {
    fun start(protocolizableTreatments: Int)
    fun asJFrame(): JFrame
}

// change to window instead of frame, and make it modal, so there can only be one window :)
class MultiProtocolSwingWindow @Inject constructor(
        private val mainFrame: MainFrame,
        swing: SwingFactory
) : MyFrame("Sammelprotokoll"), MultiProtocolWindow {

    private val log = LoggerFactory.getLogger(javaClass)

    private val btnCreate = swing.newEventButton("Drucken & Speichern", ViewNames.MultiProtocol.ButtonPrint, { ReallyCreateMultiProtocolEvent(txtDescription.text) })
    private val lblProtocolizableTreatments = JLabel()
    private val btnTestPrint = swing.newEventButton("Testdruck", ViewNames.MultiProtocol.ButtonTestPrint, { TestCreateMultiProtocolEvent() })
    private val txtDescription = MyTextArea(ViewNames.MultiProtocol.InputDescription, 6)

    init {
        log.debug("creating new window instance (should be a prototype and received via a provider by controller.")

        rootPane.border = BorderFactory.createEmptyBorder(0, 15, 10, 15)!!
        closeOnEscape()
        addCloseListener { closeWindow() }
        isResizable = false

        contentPane.layout = BorderLayout()
        contentPane.add(HtmlEditorPane("<h1>Sammelprotokoll erstellen</h1>"), BorderLayout.NORTH)
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

    override fun asJFrame() = this

    override fun closeWindow() {
        hideAndClose()
    }

    private fun mainPanel() = GridPanel().apply {
        add(JLabel("Anzahl druckbare Protokolle: "))

        c.gridx++
        c.anchor = GridBagConstraints.WEST
        add(lblProtocolizableTreatments)

        c.gridx = 0
        c.gridy++
        c.gridwidth = 2
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        add(VFillFormPanel().apply {
            addFormInput("Beschreibung", txtDescription.scrolled())
        })
    }

    private fun buttonPanel() = JPanel().apply {
        add(btnCreate)
        add(btnTestPrint)
        add(JButton("Abbrechen").apply { addActionListener { closeWindow() } })
    }

}
