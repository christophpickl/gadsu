package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.service.GapiCredentials
import at.cpickl.gadsu.service.nullIfEmpty
import at.cpickl.gadsu.version.CheckForUpdatesEvent
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.EventButton
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.components.inputs.NumberField
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.swing.ClosableWindow
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.disableFocusable
import at.cpickl.gadsu.view.swing.disabled
import at.cpickl.gadsu.view.swing.isTransparent
import at.cpickl.gadsu.view.swing.registerCloseOnEscape
import at.cpickl.gadsu.view.swing.selectAllOnFocus
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


interface PreferencesWindow : ClosableWindow {
    fun start()
    fun initData(preferencesData: PreferencesData)
    fun readData(): PreferencesData

    var txtApplicationDirectory: String
    var txtLatestBackup: String
    var txtProxy: String
    var txtGcalName: String
    var txtGmailAddress: String
    var txtGapiClientId: String
    var txtGapiClientSecret: String
    val btnCheckUpdate: EventButton
}

class PreferencesSwingWindow @Inject constructor(
        private val mainFrame: MainFrame,
        private val bus: EventBus,
        swing: SwingFactory
) : MyFrame("Einstellungen"), PreferencesWindow {

    private val HGAP_FROM_WINDOW = 15
    private val VGAP_BETWEEN_COMPONENTS = 10

    private val inpApplicationDirectory = JTextField().disabled().disableFocusable()
    private val inpLatestBackup = JTextField().disabled().disableFocusable()

    private val log = LoggerFactory.getLogger(javaClass)
    private var yetCreated: Boolean = false
    private val inpUsername = JTextField()
    private val inpProxy = JTextField()
    private val inpGcalName = JTextField()
    private val inpGmailAddress = JTextField()
    private val inpGapiClientId = JTextField()
    private val inpGapiClientSecret = JTextField()
    private val inpCheckUpdates = JCheckBox("Beim Start prüfen")
    private val inpTreatmentGoal = NumberField(4).selectAllOnFocus()

    override val btnCheckUpdate = swing.newEventButton("Jetzt prüfen", "", { CheckForUpdatesEvent() })

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
    override var txtProxy: String
        get() = inpProxy.text
        set(value) {
            inpProxy.text = value
        }
    override var txtGcalName: String
        get() = inpGcalName.text
        set(value) {
            inpGcalName.text = value
        }
    override var txtGmailAddress: String
        get() = inpGmailAddress.text
        set(value) {
            inpGmailAddress.text = value
        }
    override var txtGapiClientId: String
        get() = inpGapiClientId.text
        set(value) {
            inpGapiClientId.text = value
        }
    override var txtGapiClientSecret: String
        get() = inpGapiClientSecret.text
        set(value) {
            inpGapiClientSecret.text = value
        }

    init {
        registerCloseOnEscape()

        name = ViewNames.Preferences.Window
        addCloseListener { doClose(false) }

        val panel = FormPanel(labelAnchor = GridBagConstraints.NORTHWEST).apply {
            border = BorderFactory.createEmptyBorder(10, HGAP_FROM_WINDOW, 0, HGAP_FROM_WINDOW)

            addDescriptiveFormInput("Dein Name", inpUsername, "Dein vollständiger Name wird unter anderem<br/>auf Rechnungen und Berichte (Protokolle) angezeigt.")
            addDescriptiveFormInput("Auto Update", initPanelCheckUpdates(), "Um immer am aktuellsten Stand zu bleiben,<br/>empfiehlt es sich diese Option zu aktivieren.",
                    GridBagFill.None, addTopInset = VGAP_BETWEEN_COMPONENTS)
            addDescriptiveFormInput("HTTP Proxy*", inpProxy, "Falls du \u00fcber einen Proxy ins Internet gelangst,<br/>dann konfiguriere diesen bitte hier. (z.B.: <tt>proxy.heim.at:8080</tt>)")
            addDescriptiveFormInput("Google Calendar*", inpGcalName, "Trage hier den Kalendernamen ein um die Google Integration einzuschalten.")
            addDescriptiveFormInput("GMail Addresse", inpGmailAddress, "Trage hier deine GMail Adresse ein für das Versenden von E-Mails.")
            addDescriptiveFormInput("Google API ID", inpGapiClientId, "Um die Google API nutzen zu können, brauchst du eine Zugangs-ID.<br/>" +
                    "Credentials sind erstellbar in der Google API Console.<br/>" +
                    "Bsp.: <tt>123456789012-aaaabbbbccccddddeeeefffffaaaabb.apps.googleusercontent.com</tt>")
            addDescriptiveFormInput("Google API Secret", inpGapiClientSecret, "Das zugehörige Passwort.<br/>" +
                    "Bsp.: <tt>AABBCCDDDaabbccdd12345678</tt>")
            addDescriptiveFormInput("Behandlungsziel*", inpTreatmentGoal, "Setze dir ein Ziel wieviele (unprotokollierte) Behandlungen du schaffen m\u00f6chtest.")

            addDescriptiveFormInput("Programm Ordner", inpApplicationDirectory, "Hier werden die progamm-internen Daten gespeichert.",
                    addTopInset = VGAP_BETWEEN_COMPONENTS)
            addDescriptiveFormInput("Letztes Backup", inpLatestBackup, "Gadsu erstellt für dich täglich ein Backup aller Informationen.",
                    addTopInset = VGAP_BETWEEN_COMPONENTS)

            c.gridwidth = 2
            add(HtmlEditorPane("<b>*</b> ... <i>Neustart erforderlich</i>").disableFocusable())
        }

        val btnClose = JButton("Speichern und schlie\u00dfen")
        btnClose.addActionListener { doClose(true) }
        rootPane.defaultButton = btnClose

        val panelSouth = JPanel().apply {
            layout = BorderLayout()
            border = BorderFactory.createEmptyBorder(15, HGAP_FROM_WINDOW, 15, HGAP_FROM_WINDOW)
            isTransparent = true
            add(btnClose, BorderLayout.EAST)
        }

        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)
//        contentPane.add(JPanel().apply { transparent() }, BorderLayout.CENTER)
        contentPane.add(panelSouth, BorderLayout.SOUTH)
    }

    private fun initPanelCheckUpdates(): Component {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.isTransparent = true

        panel.add(inpCheckUpdates)
        panel.add(btnCheckUpdate)
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
        inpProxy.text = preferencesData.proxy ?: ""
        inpGcalName.text = preferencesData.gcalName ?: ""
        inpGmailAddress.text = preferencesData.gmailAddress ?: ""
        inpGapiClientId.text = preferencesData.gapiCredentials?.clientId
        inpGapiClientSecret.text = preferencesData.gapiCredentials?.clientSecret
        inpTreatmentGoal.numberValue = preferencesData.treatmentGoal ?: 0
    }

    override fun readData(): PreferencesData {
        return PreferencesData(
                inpUsername.text,
                inpCheckUpdates.isSelected,
                inpProxy.text.nullIfEmpty(),
                inpGcalName.text.nullIfEmpty(),
                inpGmailAddress.text.nullIfEmpty(),
                GapiCredentials.buildNullSafe(inpGapiClientId.text.nullIfEmpty(), inpGapiClientSecret.text.nullIfEmpty()),
                if (inpTreatmentGoal.numberValue <= 0) null else inpTreatmentGoal.numberValue
        )
    }

    override fun start() {
        if (yetCreated == false) {
            yetCreated = true
            pack()
            setLocationRelativeTo(mainFrame.asJFrame())
        }
        if (isVisible != true) {
            log.trace("Setting preferencies window visible.")
            isVisible = true
        } else {
            requestFocus()
        }
    }

    override fun closeWindow() {
        hideAndClose()
    }
}
