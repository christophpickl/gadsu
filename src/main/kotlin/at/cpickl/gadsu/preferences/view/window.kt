package at.cpickl.gadsu.preferences.view

import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.PreferencesWindowClosedEvent
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

val HGAP_FROM_WINDOW = 15

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
