package at.cpickl.gadsu.development

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.MyFrame
import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.Point
import java.util.Timer
import java.util.TimerTask
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel


class DevFrame(initLocation: Point): JFrame() {

    private val txtClient = JLabel()

    init {
        title = "Development Console"
        txtClient.isOpaque = true

        val panel = GridPanel()
        panel.border = MyFrame.BORDER_GAP

        panel.c.anchor = GridBagConstraints.NORTHWEST
        panel.c.fill = GridBagConstraints.HORIZONTAL
        panel.c.weightx = 1.0

        panel.add(JLabel("Current client: "))

        panel.c.gridy++
        panel.c.fill = GridBagConstraints.BOTH
        panel.add(txtClient)

        panel.c.gridy++
        panel.addLastRowFilled()

        contentPane.add(panel)

        size = Dimension(400, 300)
        location = initLocation
    }

    fun updateClient(client: Client?) {
        val text: String
        if (client == null) {
            text = "NULL"
        } else if (client === Client.INSERT_PROTOTYPE) {
            text = "Client.INSERT_PROTOTYPE"
        } else {
            text = """<html><b>Name</b>: ${client.fullName}<br/><b>Note</b>: ${client.note}</html>"""
        }
        txtClient.text = text

        txtClient.background = Color.YELLOW
        val timer = Timer("dev-blinking", true)
        timer.schedule(object : TimerTask() {
            override fun run() {
                txtClient.background = null
            }
        }, 1000L)
    }

    fun start() {
        isVisible = true
    }

    fun close() {
        isVisible = false
        dispose()
    }

}

var JComponent.debugColor: Color?
    get() = null
    set(value) {
        if (Development.COLOR_ENABLED) {
            background = value
        }
    }

