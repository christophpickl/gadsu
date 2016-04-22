package at.cpickl.gadsu.development

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.GadsuMenuBar
import at.cpickl.gadsu.view.components.GridPanel
import com.google.common.eventbus.EventBus
import java.awt.Color
import java.util.Timer
import java.util.TimerTask
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JWindow


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

class DevWindow: JWindow() {

    private val txtClient = JLabel()

    init {
        val panel = GridPanel()
        panel.add(JLabel("Development... current client: "))
        panel.c.gridy++
        panel.add(txtClient)

        add(panel)

        pack()
    }

    fun updateClient(client: Client?) {
        txtClient.text = if (client != null) """<html><b>Name</b>: ${client.fullName}<br/><b>Note</b>: ${client.note}</html>""" else "NULL"

        txtClient.background = Color.RED
        val timer = Timer("dev-blinking", true)
        timer.schedule(object : TimerTask() {
            override fun run() {
                txtClient.background = null
            }
        }, 3000L)
    }

    fun start() {
        isVisible = true
    }

}

var JComponent.debugColor: Color?
    get() = null
    set(value) {
        if (Development.COLOR_ENABLED) {
            background = value
        }
    }

