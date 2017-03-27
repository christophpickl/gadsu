package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.service.GapiCredentials
import at.cpickl.gadsu.service.nullIfEmpty
import at.cpickl.gadsu.version.CheckForUpdatesEvent
import at.cpickl.gadsu.view.*
import at.cpickl.gadsu.view.components.EventButton
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.components.inputs.NumberField
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.swing.*
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.*

private val HGAP_FROM_WINDOW = 15

interface WritablePreferencesWindow {
    var txtApplicationDirectory: String set
    var txtLatestBackup: String set
    val btnCheckUpdate: EventButton get
}

interface PreferencesWindow : ClosableWindow, WritablePreferencesWindow {
    fun start()
    fun initData(preferencesData: PreferencesData)
    fun readData(): PreferencesData
}

private abstract class PrefsTab(override val tabTitle: String) : KTab {
    protected val VGAP_BETWEEN_COMPONENTS = 10
    override val scrolled = true
}

private class PrefsTabGeneral(swing: SwingFactory) : PrefsTab("Allgemein") {

    val inpUsername = JTextField().viewName { Preferences.InputUsername }
    val inpCheckUpdates = JCheckBox("Beim Start prüfen")
    val inpTreatmentGoal = NumberField(4).selectAllOnFocus().leftAligned()

    val inpApplicationDirectory = JTextField().disabled().disableFocusable()
    val inpLatestBackup = JTextField().disabled().disableFocusable()

    val btnCheckUpdate = swing.newEventButton("Jetzt prüfen", "", { CheckForUpdatesEvent() })

    override fun asComponent() = FormPanel(fillCellsGridy = false, labelAnchor = GridBagConstraints.NORTHWEST).apply {
        border = BorderFactory.createEmptyBorder(10, HGAP_FROM_WINDOW, 0, HGAP_FROM_WINDOW)

        addDescriptiveFormInput("Dein Name", inpUsername, "Dein vollständiger Name wird unter anderem<br/>auf Rechnungen und Berichte (Protokolle) angezeigt.")
        addDescriptiveFormInput("Auto Update", initPanelCheckUpdates(), "Um immer am aktuellsten Stand zu bleiben,<br/>empfiehlt es sich diese Option zu aktivieren.",
                GridBagFill.None, addTopInset = VGAP_BETWEEN_COMPONENTS)
        addDescriptiveFormInput("Behandlungsziel*", inpTreatmentGoal, "Setze dir ein Ziel wieviele (unprotokollierte) Behandlungen du schaffen m\u00f6chtest.")

        addDescriptiveFormInput("Programm Ordner", inpApplicationDirectory, "Hier werden die progamm-internen Daten gespeichert.",
                addTopInset = VGAP_BETWEEN_COMPONENTS)
        addDescriptiveFormInput("Letztes Backup", inpLatestBackup, "Gadsu erstellt für dich täglich ein Backup aller Informationen.",
                addTopInset = VGAP_BETWEEN_COMPONENTS)

        c.gridwidth = 2
        add(HtmlEditorPane("<b>*</b> ... <i>Neustart erforderlich</i>").disableFocusable())
        addLastColumnsFilled()
    }

    private fun initPanelCheckUpdates() = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        transparent()

        add(inpCheckUpdates)
        add(btnCheckUpdate)
    }

}

private class PrefsTabConnectivity : PrefsTab("Connectivity") {

    val inpProxy = JTextField()
    val inpGcalName = JTextField()
    val inpGmailAddress = JTextField()
    val inpGapiClientId = JTextField()
    val inpGapiClientSecret = JTextField()
    val inpConfirmMailSubject = JTextField()
    val inpConfirmMailBody = MyTextArea("", visibleRows = 6)

    override fun asComponent() = FormPanel(
            fillCellsGridy = false,
            labelAnchor = GridBagConstraints.NORTHWEST,
            inputAnchor = GridBagConstraints.NORTHWEST).apply {
        border = BorderFactory.createEmptyBorder(10, HGAP_FROM_WINDOW, 0, HGAP_FROM_WINDOW)

        addDescriptiveFormInput("HTTP Proxy*", inpProxy, "Falls du \u00fcber einen Proxy ins Internet gelangst,<br/>dann konfiguriere diesen bitte hier. (z.B.: <tt>proxy.heim.at:8080</tt>)")
        addDescriptiveFormInput("Google Calendar*", inpGcalName, "Trage hier den Kalendernamen ein um die Google Integration einzuschalten.")
        addDescriptiveFormInput("GMail Addresse", inpGmailAddress, "Trage hier deine GMail Adresse ein für das Versenden von E-Mails.")
        addDescriptiveFormInput("Google API ID", inpGapiClientId, "Um die Google API nutzen zu können, brauchst du eine Zugangs-ID.<br/>" +
                "Credentials sind erstellbar in der Google API Console.<br/>" +
                "Bsp.: <tt>123456789012-aaaabbbbccccddddeeeefffffaaaabb.apps.googleusercontent.com</tt>")
        addDescriptiveFormInput("Google API Secret", inpGapiClientSecret, "Das zugehörige Passwort.<br/>" +
                "Bsp.: <tt>AABBCCDDDaabbccdd12345678</tt>")
        addDescriptiveFormInput("Mail Subject", inpConfirmMailSubject, "Bestätigungsmail Vorlage welche die selben Variablen nutzen kann wie der Mail Body.")
        // for available variables see: AppointmentConfirmationerImpl
        addDescriptiveFormInput("Mail Body", inpConfirmMailBody.scrolled(), "Bestätigungsmail Vorlage. Mögliche Variablen: \${name}, \${dateStart?datetime}, \${dateEnd?datetime}.")
        addLastColumnsFilled()
    }

}

class PreferencesSwingWindow @Inject constructor(
        private val mainFrame: MainFrame,
        private val bus: EventBus,
        swing: SwingFactory
) : MyFrame("Einstellungen"), PreferencesWindow {

    private val tabbedPane = JTabbedPane(JTabbedPane.NORTH, JTabbedPane.SCROLL_TAB_LAYOUT).transparent()
    private val tabGeneral = PrefsTabGeneral(swing)
    private val tabConnectivity = PrefsTabConnectivity()
    private val allTabs: List<PrefsTab> = listOf(tabGeneral, tabConnectivity)

    private val log = LoggerFactory.getLogger(javaClass)
    private var yetCreated: Boolean = false

    override var txtApplicationDirectory: String = ""
        set(value) {
            tabGeneral.inpApplicationDirectory.text = value
        }
    override var txtLatestBackup: String = ""
        set(value) {
            tabGeneral.inpLatestBackup.text = value
        }
    override val btnCheckUpdate: EventButton = tabGeneral.btnCheckUpdate

    init {
        registerCloseOnEscape()

        name = ViewNames.Preferences.Window
        addCloseListener { doClose(false) }

        tabbedPane.addKTabs(allTabs)

        val btnClose = JButton("Speichern und schlie\u00dfen")
        btnClose.addActionListener { doClose(true) }
        rootPane.defaultButton = btnClose

        val panelSouth = JPanel().apply {
            layout = BorderLayout()
            border = BorderFactory.createEmptyBorder(15, HGAP_FROM_WINDOW, 15, HGAP_FROM_WINDOW)
            transparent()
            add(btnClose, BorderLayout.EAST)
        }

        contentPane.layout = BorderLayout()
        contentPane.add(tabbedPane, BorderLayout.CENTER)
        contentPane.add(panelSouth, BorderLayout.SOUTH)
    }

    private fun doClose(persistData: Boolean) {
        isVisible = false
        bus.post(PreferencesWindowClosedEvent(persistData))
    }

    override fun initData(preferencesData: PreferencesData) {
        log.trace("initData(preferencesData={})", preferencesData)
        tabGeneral.inpUsername.text = preferencesData.username
        tabGeneral.inpCheckUpdates.isSelected = preferencesData.checkUpdates
        tabGeneral.inpTreatmentGoal.numberValue = preferencesData.treatmentGoal ?: 0

        tabConnectivity.inpProxy.text = preferencesData.proxy ?: ""
        tabConnectivity.inpGcalName.text = preferencesData.gcalName ?: ""
        tabConnectivity.inpGmailAddress.text = preferencesData.gmailAddress ?: ""
        tabConnectivity.inpGapiClientId.text = preferencesData.gapiCredentials?.clientId
        tabConnectivity.inpGapiClientSecret.text = preferencesData.gapiCredentials?.clientSecret
        tabConnectivity.inpConfirmMailSubject.text = preferencesData.templateConfirmSubject ?: ""
        tabConnectivity.inpConfirmMailBody.text = preferencesData.templateConfirmBody ?: ""
    }

    override fun readData(): PreferencesData {
        return PreferencesData(
                username = tabGeneral.inpUsername.text,
                checkUpdates = tabGeneral.inpCheckUpdates.isSelected,
                treatmentGoal = if (tabGeneral.inpTreatmentGoal.numberValue <= 0) null else tabGeneral.inpTreatmentGoal.numberValue,

                proxy = tabConnectivity.inpProxy.text.nullIfEmpty(),
                gcalName = tabConnectivity.inpGcalName.text.nullIfEmpty(),
                gmailAddress = tabConnectivity.inpGmailAddress.text.nullIfEmpty(),
                gapiCredentials = GapiCredentials.buildNullSafe(tabConnectivity.inpGapiClientId.text.nullIfEmpty(), tabConnectivity.inpGapiClientSecret.text.nullIfEmpty()),
                templateConfirmSubject = tabConnectivity.inpConfirmMailSubject.text.nullIfEmpty(),
                templateConfirmBody = tabConnectivity.inpConfirmMailBody.text.nullIfEmpty()
        )
    }

    override fun start() {
        if (!yetCreated) {
            yetCreated = true
            pack()
            setLocationRelativeTo(mainFrame.asJFrame())
        }
        if (!isVisible) {
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
