package at.cpickl.gadsu.view

import at.cpickl.gadsu.IS_OS_MAC
import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.acupuncture.ShopAcupunctureViewEvent
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.forClient
import at.cpickl.gadsu.development.Development
import at.cpickl.gadsu.preferences.ShowPreferencesEvent
import at.cpickl.gadsu.report.CreateMultiProtocolEvent
import at.cpickl.gadsu.report.CreateProtocolEvent
import at.cpickl.gadsu.service.CurrentChangedEvent
import at.cpickl.gadsu.service.InternetConnectionStateChangedEvent
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.ReconnectInternetConnectionEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.inject.Inject
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
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
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe open fun onMenuBarEntryClickedEvent(event: MenuBarEntryClickedEvent) {
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
            menuBar.itemProtocol.isEnabled = it != null && it.yetPersisted
        }
    }

}

@Logged
open class GadsuMenuBar @Inject constructor(
        private val bus: EventBus,
        private val mac: MacHandler
) : JMenuBar() {

    val itemProtocol = JMenuItem("Protokoll erstellen")

    lateinit var itemReconnect: JMenuItem
    init {
        menuApp()
        menuReports()

        Development.fiddleAroundWithMenuBar(this, bus)
    }

    @Subscribe open fun onInternetConnectionStateChangedEvent(event: InternetConnectionStateChangedEvent) {
        itemReconnect.isVisible = !event.isConnected
    }

    private fun menuApp() {
        val menuApp = JMenu("Datei")

        if (!mac.isEnabled()) {
            menuApp.addItem("\u00DCber Gadsu", ShowAboutDialogEvent())

            val shortcut = if (IS_OS_MAC) {
                KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit ().menuShortcutKeyMask) // CTRL for win/linux, and CMD for mac
            } else null
            menuApp.addItem("Einstellungen", ShowPreferencesEvent(), shortcut)
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
        add(menuApp)
    }

    private fun menuReports() {
        val menuReports = JMenu("Berichte")

        itemProtocol.isEnabled = false
        itemProtocol.name = ViewNames.MenuBar.ProtocolGenerate
        itemProtocol.addActionListener { bus.post(MenuBarEntryClickedEvent(MenuBarEntry.REPORT_PROTOCOL)) }
        menuReports.add(itemProtocol)
        menuReports.addItem("Sammelprotokoll erstellen", MenuBarEntryClickedEvent(MenuBarEntry.REPORT_MULTI_PROTOCOL))

        add(menuReports)
    }

    private fun JMenu.addItem(label: String, event: Any, shortcut: KeyStroke? = null): JMenuItem {
        val item = JMenuItem(label)
        item.addActionListener { e -> bus.post(event) }
        if (shortcut != null) item.accelerator = shortcut
        add(item)
        return item
    }

}
