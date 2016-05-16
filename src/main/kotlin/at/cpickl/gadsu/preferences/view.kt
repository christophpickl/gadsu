package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.version.CheckForUpdatesEvent
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.EventButton
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.disabled
import at.cpickl.gadsu.view.swing.isTransparent
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Component
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

    var txtApplicationDirectory: String
    var txtLatestBackup: String
}

class SwingPreferencesFrame @Inject constructor(
        private val mainFrame: MainFrame,
        private val bus: EventBus
) : MyFrame("Einstellungen"), PreferencesWindow {

    private val HGAP_FROM_WINDOW = 15
    private val VGAP_BETWEEN_COMPONENTS = 10

    private val inpApplicationDirectory = JTextField().disabled()
    private val inpLatestBackup = JTextField().disabled()

    private val log = LoggerFactory.getLogger(javaClass)
    private var yetCreated: Boolean = false
    private val inpUsername = JTextField()
    private val inpCheckUpdates = JCheckBox("Beim Start prüfen")

    override var txtApplicationDirectory: String
        get() = "unused"
        set(value) {
            inpApplicationDirectory.text = value
        }
    override var txtLatestBackup: String
        get() = "unused"
        set(value) {
            inpLatestBackup.text = value
        }

    init {
        name = ViewNames.Preferences.Window
        addCloseListener { doClose(false) }

        val panel = FormPanel(labelAnchor = GridBagConstraints.NORTHWEST).apply {
            border = BorderFactory.createEmptyBorder(10, HGAP_FROM_WINDOW, 0, HGAP_FROM_WINDOW)

            addDescriptiveFormInput("Dein Name", inpUsername, "Dein vollständiger Name wird unter anderem<br/>auf Rechnungen und Berichte (Protokolle) angezeigt.")
            addDescriptiveFormInput("Auto Update", initPanelCheckUpdates(), "Um immer am aktuellsten Stand zu bleiben,<br/>empfiehlt es sich diese Option zu aktivieren.",
                    GridBagFill.None, addTopInset = VGAP_BETWEEN_COMPONENTS)
            addDescriptiveFormInput("Programm Ordner", inpApplicationDirectory, "Hier werden die progamm-internen Daten gespeichert.",
                    addTopInset = VGAP_BETWEEN_COMPONENTS)
            addDescriptiveFormInput("Letztes Backup", inpLatestBackup, "Gadsu erstellt für dich täglich ein Backup aller Informationen.",
                    addTopInset = VGAP_BETWEEN_COMPONENTS)
            addLastColumnsFilled()
        }

        val btnClose = JButton("Schlie\u00dfen")
        btnClose.addActionListener { doClose(true) }
        rootPane.defaultButton = btnClose

        val panelSouth = JPanel().apply {
            layout = BorderLayout()
            border = BorderFactory.createEmptyBorder(15, HGAP_FROM_WINDOW, 15, HGAP_FROM_WINDOW)
            isTransparent = true
            add(btnClose, BorderLayout.EAST)
        }

        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.NORTH)
        contentPane.add(JPanel().apply { transparent() }, BorderLayout.CENTER)
        contentPane.add(panelSouth, BorderLayout.SOUTH)
    }

    private fun initPanelCheckUpdates(): Component {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.isTransparent = true

        panel.add(inpCheckUpdates)
        panel.add(EventButton("Jetzt prüfen", "", { CheckForUpdatesEvent() }, bus))
        return panel
    }

    private fun doClose(persistData: Boolean) {
        isVisible = false
        bus.post(PreferencesWindowClosedEvent(persistData))
    }

    override fun initData(preferencesData: PreferencesData) {
        log.trace("initData(preferencesData={})", preferencesData)
        inpUsername.text = preferencesData.username
        inpCheckUpdates.isSelected = preferencesData.checkUpdates
    }

    override fun readData() = PreferencesData(inpUsername.text, inpCheckUpdates.isSelected)

    override fun start() {
        if (yetCreated == false) {
            yetCreated = true
            pack()
            setLocationRelativeTo(mainFrame.asJFrame())
        }
        if (isVisible != true) {
            log.trace("Setting preferencies window visible.")
            isVisible = true
        }
    }

    override fun close() {
        hideAndClose()
    }

}
