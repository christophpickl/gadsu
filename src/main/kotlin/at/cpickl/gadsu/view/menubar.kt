package at.cpickl.gadsu.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.Event
import at.cpickl.gadsu.QuitAskEvent
import at.cpickl.gadsu.SHORTCUT_MODIFIER
import at.cpickl.gadsu.acupuncture.ShowAcupunctureViewEvent
import at.cpickl.gadsu.appointment.gcal.sync.RequestGCalSyncEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientChangeStateEvent
import at.cpickl.gadsu.client.ClientNavigateDownEvent
import at.cpickl.gadsu.client.ClientNavigateUpEvent
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.ClientUpdatedEvent
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.DeleteCurrentClientEvent
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.client.ShowInClientsListEvent
import at.cpickl.gadsu.client.forClient
import at.cpickl.gadsu.client.view.ClientMasterView
import at.cpickl.gadsu.client.view.detail.ClientTabType
import at.cpickl.gadsu.client.view.detail.SelectClientTab
import at.cpickl.gadsu.development.Development
import at.cpickl.gadsu.mail.RequestPrepareMailEvent
import at.cpickl.gadsu.preferences.ShowPreferencesEvent
import at.cpickl.gadsu.report.CreateProtocolEvent
import at.cpickl.gadsu.report.multiprotocol.RequestCreateMultiProtocolEvent
import at.cpickl.gadsu.service.CurrentChangedEvent
import at.cpickl.gadsu.service.FormSaveEvent
import at.cpickl.gadsu.service.FormType
import at.cpickl.gadsu.service.InternetConnectionStateChangedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.PrintFormEvent
import at.cpickl.gadsu.service.ReconnectInternetConnectionEvent
import at.cpickl.gadsu.tcm.ShowElementsTableViewEvent
import at.cpickl.gadsu.treatment.NextTreatmentEvent
import at.cpickl.gadsu.treatment.PreviousTreatmentEvent
import at.cpickl.gadsu.treatment.TreatmentSaveEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import java.awt.event.KeyEvent
import javax.inject.Inject
import javax.swing.JCheckBoxMenuItem
import javax.swing.JComponent
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.KeyStroke


@Logged
open class GadsuMenuBarController @Inject constructor(
        private val menu: GadsuMenuBar,
        private val currentClient: CurrentClient,
        private val masterView: ClientMasterView
) {

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        menu.contentType = MainContentType.CLIENT
        menu.currentClient(currentClient.data)

        menu.clientNavigateUp.isEnabled = false
        menu.clientNavigateDown.isEnabled = false
    }

    @Subscribe open fun onMainContentChangedEvent(event: MainContentChangedEvent) {
        menu.contentType = event.newContent.type
        menu.currentClient(currentClient.data) // re-initialize menu bar for client specific items
    }

    @Subscribe open fun onCurrentChangedEvent(event: CurrentChangedEvent) {
        event.forClient {
            menu.currentClient(it)
            if (it == null || !it.yetPersisted) {
                menu.clientNavigateUp.isEnabled = false
                menu.clientNavigateDown.isEnabled = false
            } else {
                val neighbours: Pair<Client?, Client?> = masterView.hasPrevNextNeighbour(it)
                menu.clientNavigateUp.isEnabled = neighbours.first != null
                menu.clientNavigateDown.isEnabled = neighbours.second != null
            }
        }
    }

    @Subscribe open fun onClientUpdatedEvent(event: ClientUpdatedEvent) {
        menu.currentClient(event.client)
    }

}

@Logged
open class GadsuMenuBar @Inject constructor(
        private val bus: EventBus,
        private val mac: MacHandler
) : JMenuBar() {

    private val log = LOG(javaClass)

    private val gcalSync = buildItem("GCal Sync", { RequestGCalSyncEvent() })

    private val itemProtocol = JMenuItem("Protokoll erstellen")

    private val clientSeperator1 = JPopupMenu.Separator()
    private val clientSeperator2 = JPopupMenu.Separator()
    private val clientShowInactives = buildCheckBoxItem("Inaktive Klienten anzeigen", ::ShowInClientsListEvent)
    val clientSave = buildItem("Klient speichern", { SaveClientEvent() }, KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MODIFIER, true))
    val clientNavigateUp = buildItem("Vorheriger Klient", { ClientNavigateUpEvent() }, KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK, true))
    val clientNavigateDown = buildItem("N\u00e4chster Klient", { ClientNavigateDownEvent() }, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK, true))

    private val clientActivate = buildItem("Klient aktivieren", { ClientChangeStateEvent(ClientState.ACTIVE) })
    private val clientDeactivate = buildItem("Klient deaktivieren", { ClientChangeStateEvent(ClientState.INACTIVE) })
    private val clientDelete = buildItem("Klient löschen", { DeleteCurrentClientEvent() })
    private val clientTabMain = buildItem("Tab Allgemein", { SelectClientTab(ClientTabType.MAIN) }, KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MODIFIER, true))
    private val clientTabTexts = buildItem("Tab Texte", { SelectClientTab(ClientTabType.TEXTS) }, KeyStroke.getKeyStroke(KeyEvent.VK_2, SHORTCUT_MODIFIER, true))
    private val clientTabTcm = buildItem("Tab TCM", { SelectClientTab(ClientTabType.TCM) }, KeyStroke.getKeyStroke(KeyEvent.VK_3, SHORTCUT_MODIFIER, true))

    private val clientEntries: List<JComponent> = listOf(clientSeperator1, clientSeperator2, clientShowInactives, clientSave, clientActivate, clientDeactivate, clientDelete,
            clientTabMain, clientTabTexts, clientTabTcm, clientNavigateUp, clientNavigateDown)

    val treatmentPrevious = buildItem("Vorherige Behandlung", { PreviousTreatmentEvent() }, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK, true))
    val treatmentNext = buildItem("N\u00e4chste Behandlung", { NextTreatmentEvent() }, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK, true))
    val treatmentSave = buildItem("Behandlung speichern", { TreatmentSaveEvent() }, KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MODIFIER, true))
    private val treatmentEntries = listOf(treatmentPrevious, treatmentNext, treatmentSave)

    private val allEntries = clientEntries.union(treatmentEntries)

    lateinit var itemReconnect: JMenuItem
    private lateinit var itemSendMail: JMenuItem

    var contentType = MainContentType.CLIENT
        get() = field
        set(value) {
            log.debug("Set content type to: {}", value)
            field = value
            allEntries.forEach { it.isVisible = false }
            @Suppress("UNUSED_VARIABLE") // required to enforce compiler enforcing all branches
            val entriesToShow = when (field) {
                MainContentType.CLIENT -> clientEntries
                MainContentType.TREATMENT -> treatmentEntries
            }.apply { forEach { it.isVisible = true } }
        }


    init {
        add(menuApp())
        add(menuEdit())
        add(menuView())
        add(menuReports())

        Development.fiddleAroundWithMenuBar(this, bus)
    }

    @Subscribe open fun onInternetConnectionStateChangedEvent(event: InternetConnectionStateChangedEvent) {
        itemReconnect.isVisible = !event.isConnected
        gcalSync.isEnabled = event.isConnected
        itemSendMail.isEnabled = event.isConnected
    }

    private fun menuApp(): JMenu {
        val menuApp = JMenu("Datei")

        if (!mac.isEnabled()) {
            menuApp.addItem("\u00DCber Gadsu", { ShowAboutDialogEvent() })
            menuApp.addItem("Einstellungen", { ShowPreferencesEvent() }, KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, SHORTCUT_MODIFIER))
            menuApp.addSeparator()
        }
        itemReconnect = menuApp.addItem("Internet Verbindung herstellen", { ReconnectInternetConnectionEvent() })
        itemReconnect.isVisible = false

        itemSendMail = menuApp.addItem("Mails versenden", { RequestPrepareMailEvent() })

        menuApp.add(gcalSync)

        if (!mac.isEnabled()) {
            menuApp.addSeparator()
            menuApp.addItem("Beenden", { QuitAskEvent() })
        }
        return menuApp
    }

    private fun menuEdit() = JMenu("Bearbeiten").apply {
        add(clientSave)
        add(treatmentSave)
        add(clientActivate)
        add(clientDeactivate)
        add(clientDelete)
    }

    private fun menuView() = JMenu("Ansicht").apply {
        addItem("Akupunkturpunkte", { ShowAcupunctureViewEvent() })
        addItem("Entsprechungstabelle", { ShowElementsTableViewEvent() })
        addSeparator()

        add(clientTabMain)
        add(clientTabTexts)
        add(clientTabTcm)
        add(clientSeperator1)
        add(clientNavigateUp)
        add(clientNavigateDown)
        add(clientSeperator2)
        add(clientShowInactives)

        add(treatmentPrevious)
        add(treatmentNext)
    }

    fun currentClient(client: Client?) {
        log.trace("currentClient(client={})", client)
        if (contentType != MainContentType.CLIENT) {
            return
        }
        val isPersisted = client?.yetPersisted ?: false
        itemProtocol.isEnabled = isPersisted
        clientDelete.isEnabled = isPersisted

        if (client == null) {
            clientActivate.isVisible = false
            clientDeactivate.isVisible = false
            clientDelete.isVisible = false
        } else {
            clientActivate.isVisible = client.state == ClientState.INACTIVE
            clientDeactivate.isVisible = client.state == ClientState.ACTIVE
            clientDelete.isVisible = true
        }
    }

    private fun menuReports(): JMenu {
        val menuReports = JMenu("Berichte")

        itemProtocol.isEnabled = false
        itemProtocol.name = ViewNames.MenuBar.ProtocolGenerate
        itemProtocol.addActionListener { bus.post(CreateProtocolEvent()) }
        menuReports.add(itemProtocol)
        menuReports.addItem("Sammelprotokoll erstellen", { RequestCreateMultiProtocolEvent() })

        menuReports.addSeparator()
        menuReports.add(printReportMenu(FormType.ANAMNESE))
        menuReports.add(printReportMenu(FormType.TREATMENT))

        return menuReports
    }

    private fun printReportMenu(type: FormType) = JMenu(type.label).apply {
        val printItem = JMenuItem("Drucken")
        val saveItem = JMenuItem("Speichern")
        printItem.addActionListener { bus.post(PrintFormEvent(type)) }
        saveItem.addActionListener { bus.post(FormSaveEvent(type)) }
        add(printItem)
        add(saveItem)
    }

    private fun buildCheckBoxItem(label: String, funCreateEvent: (Boolean) -> Event, shortcut: KeyStroke? = null): JCheckBoxMenuItem {
        val item = JCheckBoxMenuItem(label)
        item.addActionListener { bus.post(funCreateEvent(item.isSelected)) }
        if (shortcut != null) item.accelerator = shortcut
        return item
    }

    private fun buildItem(label: String, eventBuilder: () -> Event, shortcut: KeyStroke? = null): JMenuItem {
        val item = JMenuItem(label)
        item.addActionListener { bus.post(eventBuilder()) }
        if (shortcut != null) item.accelerator = shortcut
        return item
    }

    private fun JMenu.addItem(label: String, eventBuilder: () -> Event, shortcut: KeyStroke? = null): JMenuItem {
        val item = buildItem(label, eventBuilder, shortcut)
        add(item)
        return item
    }

}
