package at.cpickl.gadsu.view

import at.cpickl.gadsu.QuitUserEvent
import com.google.common.eventbus.EventBus
import javax.inject.Inject
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem


class GadsuMenuBar @Inject constructor(
        private val bus: EventBus) : JMenuBar() {

    init {
        val isMacApp = MacHandler.isMacApp()

        val menuApp = JMenu("Gadsu")
        if (!isMacApp) {
            menuApp.addItem("\u00DCber Gadsu", ShowAboutDialogEvent())
            // add entry preferences
            menuApp.addSeparator()
            menuApp.addItem("Beenden", QuitUserEvent())

        }
        menuApp.addItem("Dummy", "unhandled")
        add(menuApp)
    }

    fun JMenu.addItem(label: String, event: Any) {
        val item = JMenuItem(label)
        item.addActionListener { e -> bus.post(event) }
        add(item)
    }

}
