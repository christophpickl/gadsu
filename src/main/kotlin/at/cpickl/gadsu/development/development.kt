package at.cpickl.gadsu.development

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.GadsuMenuBar
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.MyFrame
import com.google.common.eventbus.EventBus
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.util.Timer
import java.util.TimerTask
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuItem


class Development {
    companion object {
        private val SYSPROPERTY_KEY = "gadsu.development"

        val ENABLED: Boolean = System.getProperty(SYSPROPERTY_KEY, "").toLowerCase().equals("true") || System.getProperty(SYSPROPERTY_KEY, "").equals("1")
        val COLOR_ENABLED = ENABLED && false

        init {
            if (ENABLED) {
                println("Development mode is enabled via '-D${SYSPROPERTY_KEY}=true'")
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
