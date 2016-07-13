package at.cpickl.gadsu.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.Event
import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.SHORTCUT_MODIFIER
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.acupuncture.ShopAcupunctureViewEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientChangeStateEvent
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.ClientUpdatedEvent
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.ShowInClientsListEvent
import at.cpickl.gadsu.client.forClient
import at.cpickl.gadsu.client.view.detail.ClientTabType
import at.cpickl.gadsu.client.view.detail.SelectClientTab
import at.cpickl.gadsu.development.Development
import at.cpickl.gadsu.preferences.ShowPreferencesEvent
import at.cpickl.gadsu.report.CreateMultiProtocolEvent
import at.cpickl.gadsu.report.CreateProtocolEvent
import at.cpickl.gadsu.report.PrintReportPrintEvent
import at.cpickl.gadsu.report.PrintReportSaveEvent
import at.cpickl.gadsu.report.PrintReportType
import at.cpickl.gadsu.service.CurrentChangedEvent
import at.cpickl.gadsu.service.InternetConnectionStateChangedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.ReconnectInternetConnectionEvent
import at.cpickl.gadsu.treatment.NextTreatmentEvent
import at.cpickl.gadsu.treatment.PreviousTreatmentEvent
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


enum class MenuBarEntry {
    REPORT_PROTOCOL,
    REPORT_MULTI_PROTOCOL,
    RECONNECT_INTERNET_CONNECTION
}

class MenuBarEntryClickedEvent(val entry: MenuBarEntry) : UserEvent() {
    override fun toString(): String{
        return "MenuBarEntryClickedEvent(entry=$entry)"
    }
}


@Logged
open class GadsuMenuBarController @Inject constructor(
        private val menuBar: GadsuMenuBar,
        private val bus: EventBus,
        private val currentClient: CurrentClient
) {

    // initialize at startup
    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        menuBar.contentType = MainContentType.CLIENT
        menuBar.currentClient(currentClient.data)
    }

    @Subscribe open fun onMainContentChangedEvent(event: MainContentChangedEvent) {
        menuBar.contentType = event.newContent.type
        menuBar.currentClient(currentClient.data) // re-initialize menu bar for client specific items
    }

    @Subscribe open fun onMenuBarEntryClickedEvent(event: MenuBarEntryClickedEvent) {
        // MINOR i dont get kotlin :-/
        val enforceRemainingBranchesKotlinBug = when (event.entry) {
            // client must never be null, as menu item will be disabled if there is no client
            // TODO @REFACTOR - rethink this double dispatching. aint necessary :-/
            MenuBarEntry.REPORT_PROTOCOL -> bus.post(CreateProtocolEvent())
            MenuBarEntry.REPORT_MULTI_PROTOCOL -> bus.post(CreateMultiProtocolEvent())

//            else -> throw GadsuException("Unhandled menu bar entry: ${event.entry}")
            MenuBarEntry.RECONNECT_INTERNET_CONNECTION -> bus.post(ReconnectInternetConnectionEvent())
        }
    }

    @Subscribe open fun onCurrentChangedEvent(event: CurrentChangedEvent) {
        event.forClient {
            menuBar.currentClient(it)
        }
    }

    @Subscribe open fun onClientUpdatedEvent(event: ClientUpdatedEvent) {
        menuBar.currentClient(event.client)
    }

}

@Logged
open class GadsuMenuBar @Inject constructor(
        private val bus: EventBus,
        private val mac: MacHandler
) : JMenuBar() {

    private val log = LOG(javaClass)
    private val itemProtocol = JMenuItem("Protokoll erstellen")

    private val clientSeperator1 = JPopupMenu.Separator()
    private val clientShowInactives = buildCheckBoxItem("Inaktive Klienten anzeigen", { ShowInClientsListEvent(it) })
    private val clientActivate = buildItem("Klient aktivieren", ClientChangeStateEvent(ClientState.ACTIVE))
    private val clientDeactivate = buildItem("Klient deaktivieren", ClientChangeStateEvent(ClientState.INACTIVE))
    private val clientTabMain = buildItem("Tab Allgemein", SelectClientTab(ClientTabType.MAIN), KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MODIFIER, true))
    private val clientTabTexts = buildItem("Tab Texte", SelectClientTab(ClientTabType.TEXTS), KeyStroke.getKeyStroke(KeyEvent.VK_2, SHORTCUT_MODIFIER, true))
    private val clientTabTcm = buildItem("Tab TCM", SelectClientTab(ClientTabType.TCM), KeyStroke.getKeyStroke(KeyEvent.VK_3, SHORTCUT_MODIFIER, true))
    private val clientEntries: List<JComponent> = listOf(clientSeperator1, clientShowInactives, clientActivate, clientDeactivate, clientTabMain, clientTabTexts, clientTabTcm)

    val treatmentPrevious = buildItem("Vorherige Behandlung", PreviousTreatmentEvent(), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, SHORTCUT_MODIFIER, true))
    val treatmentNext = buildItem("N\u00e4chste Behandlung", NextTreatmentEvent(), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, SHORTCUT_MODIFIER, true))
    private val treatmentEntries = listOf(treatmentPrevious, treatmentNext)

    private val allEntries = listOf(clientEntries, treatmentEntries)

    lateinit var itemReconnect: JMenuItem

    var contentType = MainContentType.CLIENT
        get() = field
        set(value) {
            log.debug("Set content type to: {}", value)
            field = value
            val entriesToShow = when (field) {
                MainContentType.CLIENT -> clientEntries
                MainContentType.TREATMENT -> treatmentEntries
            }.apply { forEach { it.isVisible = true} }
            allEntries.filter { it != entriesToShow }.forEach { it.forEach { it.isVisible = false } }
        }


    init {
        add(menuApp())
        add(menuEdit())
        add(menuView())
        add(menuReports())

        Development.fiddleAroundWithMenuBar(this, bus)
    }

    private fun menuApp(): JMenu {
        val menuApp = JMenu("Datei")

        if (!mac.isEnabled()) {
            menuApp.addItem("\u00DCber Gadsu", ShowAboutDialogEvent())
            menuApp.addItem("Einstellungen", ShowPreferencesEvent(), KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, SHORTCUT_MODIFIER))
        }

        itemReconnect = menuApp.addItem("Internet Verbindung herstellen", MenuBarEntryClickedEvent(MenuBarEntry.RECONNECT_INTERNET_CONNECTION))
        itemReconnect.isVisible = false
//        val itemExport = JMenuItem("Export")
//        itemExport.isEnabled = false
//        menuApp.add(itemExport)

        menuApp.addItem("Akupunkturpunkte", ShopAcupunctureViewEvent())

        if (!mac.isEnabled()) {
            menuApp.addSeparator()
            menuApp.addItem("Beenden", QuitEvent())
        }
        return menuApp
    }

    private fun menuEdit() = JMenu("Bearbeiten").apply {
        add(clientActivate)
        add(clientDeactivate)
    }

    private fun menuView() = JMenu("Ansicht").apply {
        add(clientTabMain)
        add(clientSeperator1)
        add(clientTabTexts)
        add(clientSeperator1)
        add(clientTabTcm)
        add(clientSeperator1)
        add(clientShowInactives)

        add(treatmentPrevious)
        add(treatmentNext)
    }

    @Subscribe open fun onInternetConnectionStateChangedEvent(event: InternetConnectionStateChangedEvent) {
        itemReconnect.isVisible = !event.isConnected
    }

    fun currentClient(client: Client?) {
        log.trace("currentClient(client={})", client)
        val isPersisted = client?.yetPersisted ?: false
        itemProtocol.isEnabled = isPersisted
        if (client == null) {
            clientActivate.isVisible = false
            clientDeactivate.isVisible = false
        } else {
            clientActivate.isVisible = client.state == ClientState.INACTIVE
            clientDeactivate.isVisible = client.state == ClientState.ACTIVE
        }
    }

    private fun menuReports(): JMenu {
        val menuReports = JMenu("Berichte")

        itemProtocol.isEnabled = false
        itemProtocol.name = ViewNames.MenuBar.ProtocolGenerate
        itemProtocol.addActionListener { bus.post(MenuBarEntryClickedEvent(MenuBarEntry.REPORT_PROTOCOL)) }
        menuReports.add(itemProtocol)
        menuReports.addItem("Sammelprotokoll erstellen", MenuBarEntryClickedEvent(MenuBarEntry.REPORT_MULTI_PROTOCOL))

        menuReports.addSeparator()
        menuReports.add(printReportMenu(PrintReportType.ANAMNESE))
        menuReports.add(printReportMenu(PrintReportType.TREATMENT))

        return menuReports
    }

    private fun printReportMenu(type: PrintReportType) = JMenu(type.label).apply {
        val printItem = JMenuItem("Drucken")
        val saveItem = JMenuItem("Speichern")
        printItem.addActionListener { bus.post(PrintReportPrintEvent(type))}
        saveItem.addActionListener { bus.post(PrintReportSaveEvent(type))}
        add(printItem)
        add(saveItem)
    }

    private fun buildCheckBoxItem(label: String, funCreateEvent: (Boolean) -> Event, shortcut: KeyStroke? = null): JCheckBoxMenuItem {
        val item = JCheckBoxMenuItem(label)
        item.addActionListener { e -> bus.post(funCreateEvent(item.isSelected)) }
        if (shortcut != null) item.accelerator = shortcut
        return item
    }

    private fun buildItem(label: String, event: Any, shortcut: KeyStroke? = null): JMenuItem {
        val item = JMenuItem(label)
        item.addActionListener { e -> bus.post(event) }
        if (shortcut != null) item.accelerator = shortcut
        return item
    }

    private fun JMenu.addItem(label: String, event: Any, shortcut: KeyStroke? = null): JMenuItem {
        val item = buildItem(label, event, shortcut)
        add(item)
        return item
    }

}
