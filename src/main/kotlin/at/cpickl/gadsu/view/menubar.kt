package at.cpickl.gadsu.view

import at.cpickl.gadsu.Development
import at.cpickl.gadsu.QuitUserEvent
import com.google.common.eventbus.EventBus
import javax.inject.Inject
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem


class GadsuMenuBar @Inject constructor(
        private val bus: EventBus,
        private val mac: MacHandler) : JMenuBar() {

    init {
        val menuApp = JMenu("Datei")

        if (!mac.isEnabled()) {
            menuApp.addItem("\u00DCber Gadsu", ShowAboutDialogEvent())
            menuApp.addItem("Einstellungen", ShowPreferencesEvent())
            menuApp.addSeparator()
            menuApp.addItem("Beenden", QuitUserEvent())

        }
        add(menuApp)

        Development.fiddleAroundWithMenuBar(this, bus)
    }

    fun JMenu.addItem(label: String, event: Any) {
        val item = JMenuItem(label)
        item.addActionListener { e -> bus.post(event) }
        add(item)
    }

}
