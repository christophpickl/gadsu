package at.cpickl.gadsu.development

import at.cpickl.gadsu.GadsuSystemPropertyKeys
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.spReadBoolean
import at.cpickl.gadsu.view.GadsuMenuBar
import com.google.common.eventbus.EventBus
import javax.swing.JMenu
import javax.swing.JMenuItem


class Development {
    companion object {
        private val systemProperty = GadsuSystemPropertyKeys.development

        val ENABLED: Boolean = systemProperty.spReadBoolean()
        val COLOR_ENABLED = ENABLED && false

        init {
            if (ENABLED) {
                println("Development mode is enabled via '-D$systemProperty=true'")
            }
        }

        fun fiddleAroundWithMenuBar(menu: GadsuMenuBar, bus: EventBus) {
            if (!ENABLED) {
                return
            }
            val menuDevelopment = JMenu("Development")
            menu.add(menuDevelopment)



            addItemTo(menuDevelopment, "Development Window", ShowDevWindowEvent(), bus)
            menuDevelopment.addSeparator()
            addItemTo(menuDevelopment, "Reset Data", DevelopmentResetDataEvent(), bus)
            addItemTo(menuDevelopment, "Clear Data", DevelopmentClearDataEvent(), bus)
        }

        private fun addItemTo(menu: JMenu, label: String, event: UserEvent, bus: EventBus) {
            val item = JMenuItem(label)
            item.addActionListener { bus.post(event) }
            menu.add(item)
        }

    }

}

