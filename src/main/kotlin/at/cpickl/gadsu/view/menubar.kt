package at.cpickl.gadsu.view

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.development.Development
import at.cpickl.gadsu.preferences.ShowPreferencesEvent
import at.cpickl.gadsu.report.CreateProtocolEvent
import at.cpickl.gadsu.service.CurrentChangedEvent
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.forClient
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem


enum class MenuBarEntry {
    REPORT_PROTOCOL
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
        when (event.entry) {
            // client must never be null, as menu item will be disabled if there is no client
            MenuBarEntry.REPORT_PROTOCOL -> bus.post(CreateProtocolEvent())

            else -> throw GadsuException("Unhandled menu bar entry: ${event.entry}")
        }
    }

    @Subscribe open fun onCurrentChangedEvent(event: CurrentChangedEvent) {
        event.forClient {
            menuBar.itemProtocol.isEnabled = it.yetPersisted
        }
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

        add(menuReports)
    }

    private fun JMenu.addItem(label: String, event: Any) {
        val item = JMenuItem(label)
        item.addActionListener { e -> bus.post(event) }
        add(item)
    }

}
