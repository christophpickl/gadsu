package at.cpickl.gadsu.development

import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.formatDateTime
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.bold
import at.cpickl.gadsu.view.swing.changeBackgroundForASec
import at.cpickl.gadsu.view.swing.opaque
import at.cpickl.gadsu.view.swing.scrolled
import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.Point
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextArea


class DevelopmentFrame(
        initLocation: Point
): MyFrame("Development Console") {

    private val txtClient = JLabel()
    private val txtTreatment = JLabel()
    private val events = JTextArea().apply {
        name = "Development.EventsTextArea"
        lineWrap = true
        if (IS_OS_WIN) {
            font = JLabel().font
        }
    }

    init {
        addCloseListener { close() }

        txtClient.opaque()
        txtTreatment.opaque()

        val panel = GridPanel()
        panel.border = MyFrame.BORDER_GAP

        panel.c.anchor = GridBagConstraints.NORTHWEST
        panel.c.fill = GridBagConstraints.HORIZONTAL
        panel.c.weightx = 1.0
        panel.c.weighty = 0.0
        panel.add(JLabel("Current client: ").bold())

        panel.c.gridy++
        panel.add(txtClient)

        panel.c.gridy++
        panel.add(JLabel("Current treatment: ").bold())

        panel.c.gridy++
        panel.add(txtTreatment)

        panel.c.gridy++
        panel.c.weighty = 1.0
        panel.c.fill = GridBagConstraints.BOTH
        panel.add(events.scrolled())

//        panel.c.gridy++
//        panel.c.weighty = 0.0
//        panel.c.fill = GridBagConstraints.NONE
//        panel.c.anchor = GridBagConstraints.CENTER
//        panel.add(ElementsStarView(bus, 40).apply { enforceSize(200, 180) })

        contentPane.add(panel)
        size = Dimension(400, 600)
        location = initLocation
    }

    fun updateClient(client: Client?) {
        update(txtClient, client, {
            """<html>${if (!it.yetPersisted) "INSERT_PROTOTYPE<br/>" else ""}Note: ${it.note}</html>"""
        })
    }

    fun updateTreatment(treatment: Treatment?) {
        update(txtTreatment, treatment, {
            """<html>${if (!it.yetPersisted) "INSERT_PROTOTYPE<br/>" else ""}Nr: ${it.number}<br/>Datum: ${it.date.formatDateTime()}</html>"""
        })
    }

    fun start() {
        isVisible = true
    }

    fun addEvent(event: Any) {
        events.text = "${events.text}${event.javaClass.simpleName} ====> $event\n"
    }

    fun close() {
        isVisible = false
        dispose()
    }

    private fun <T> update(txt: JLabel, value: T?, function: (T) -> String) {
        val text: String
        if (value == null) {
            text = "NULL"
        } else {
            text = function(value)
        }
        txt.text = text
        txt.changeBackgroundForASec(Color.YELLOW)
    }

}

var JComponent.debugColor: Color?
    get() = null
    set(value) {
        if (Development.COLOR_ENABLED) {
            opaque()
            background = value
        }
    }

