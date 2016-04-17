package at.cpickl.gadsu.view

import at.cpickl.gadsu.Development
import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.QuitUserEvent
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.ClientUnselectedEvent
import at.cpickl.gadsu.preferences.ShowPreferencesEvent
import at.cpickl.gadsu.report.CreateProtocolEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import javax.inject.Inject
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem


enum class MenuBarEntry {
    REPORT_PROTOCOL
}

class MenuBarEntryClickedEvent(val entry: MenuBarEntry) : UserEvent()


class GadsuMenuBarController @Inject constructor(
        private val menuBar: GadsuMenuBar,
        private val bus: EventBus
) {

    private var recentClient: Client? = null

    @Subscribe fun onMenuBarEntryClickedEvent(event: MenuBarEntryClickedEvent) {
        when (event.entry) {
            // client must never be null, as menu item will be disabled if there is no client
            MenuBarEntry.REPORT_PROTOCOL -> bus.post(CreateProtocolEvent(recentClient!!))

            else -> throw GadsuException("Unhandled menu bar entry: ${event.entry}")
        }
    }

    @Subscribe fun onClientSelectedEvent(event: ClientSelectedEvent) {
        recentClient = event.client
        menuBar.itemProtocol.isEnabled = true
    }

    @Subscribe fun onClientUnselectedEvent(@Suppress("UNUSED_PARAMETER") event: ClientUnselectedEvent) {
        recentClient = null
        menuBar.itemProtocol.isEnabled = false
    }

}

class GadsuMenuBar @Inject constructor(
        private val bus: EventBus,
        private val mac: MacHandler) : JMenuBar() {

    val itemProtocol = JMenuItem("Protokoll erstellen")

    init {
        menuApp()
        menuReports()

        Development.fiddleAroundWithMenuBar(this, bus)
    }

    private fun menuApp() {
        val menuApp = JMenu("Datei")

        if (!mac.isEnabled()) {
            menuApp.addItem("\u00DCber Gadsu", ShowAboutDialogEvent())
            menuApp.addItem("Einstellungen", ShowPreferencesEvent())
        }
        val itemExport = JMenuItem("Export")
        itemExport.isEnabled = false
        menuApp.add(itemExport)

        if (!mac.isEnabled()) {
            menuApp.addSeparator()
            menuApp.addItem("Beenden", QuitUserEvent())
        }
        add(menuApp)
    }

    private fun menuReports() {
        val menuReports = JMenu("Berichte")

        itemProtocol.isEnabled = false
        itemProtocol.addActionListener { bus.post(MenuBarEntryClickedEvent(MenuBarEntry.REPORT_PROTOCOL)) }
        menuReports.add(itemProtocol)

        add(menuReports)
    }

    private fun JMenu.addItem(label: String, event: Any) {
        val item = JMenuItem(label)
        item.addActionListener { e -> bus.post(event) }
        add(item)
    }

}
