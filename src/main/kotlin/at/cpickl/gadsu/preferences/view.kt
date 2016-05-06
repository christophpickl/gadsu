package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.version.CheckForUpdatesEvent
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.EventButton
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.isTransparent
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextField

interface PreferencesWindow {
    fun start()
    fun close()
    fun initData(preferencesData: PreferencesData)
    fun readData(): PreferencesData
}

class SwingPreferencesFrame @Inject constructor(
        private val mainFrame: MainFrame,
        private val bus: EventBus
) : MyFrame("Einstellungen"), PreferencesWindow {

    private val log = LoggerFactory.getLogger(javaClass)
    private var yetCreated: Boolean = false
    private val inpUsername = JTextField()
    private val inpCheckUpdates = JCheckBox("Beim Start prüfen")

    init {
        name = ViewNames.Preferences.Window
        addCloseListener { doClose() }

        val panel = FormPanel(labelAnchor = GridBagConstraints.NORTHWEST)
        panel.border = BorderFactory.createEmptyBorder(10, 15, 10, 15)

        panel.addFormInput("Benutzername", inpUsername)
        panel.addFormInput("Auto Update", initPanelCheckUpdates(), GridBagFill.None)

        val btnClose = JButton("Schlie\u00dfen")
        btnClose.addActionListener { doClose() }
        rootPane.defaultButton = btnClose

        val panelSouth = JPanel()
        panelSouth.layout = BorderLayout()
        panelSouth.isTransparent = true
        panelSouth.add(btnClose, BorderLayout.WEST)

        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)
        contentPane.add(panelSouth, BorderLayout.SOUTH)
    }

    private fun initPanelCheckUpdates(): Component {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.isTransparent = true

        panel.add(inpCheckUpdates)
//        panel.add(JLabel("Beim Start prüfen"))
        panel.add(EventButton("Jetzt prüfen", "", { CheckForUpdatesEvent() }, bus))
        return panel
    }

    private fun doClose() {
        isVisible = false
        bus.post(PreferencesWindowClosedEvent())
    }

    override fun initData(preferencesData: PreferencesData) {
        log.trace("initData(preferencesData={})", preferencesData)
        inpUsername.text = preferencesData.username
        inpCheckUpdates.isSelected = preferencesData.checkUpdates
    }

    override fun readData() = PreferencesData(inpUsername.text, inpCheckUpdates.isSelected)

    override fun start() {
        if (yetCreated === false) {
            yetCreated = true
            size = Dimension(700, 300)
            isResizable = false
            setLocationRelativeTo(mainFrame.asJFrame())
            isVisible = true
        } else {
            isVisible = true
        }

    }

    override fun close() {
        hideAndClose()
    }

}
