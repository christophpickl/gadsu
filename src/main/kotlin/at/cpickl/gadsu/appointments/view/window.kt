package at.cpickl.gadsu.appointments.view

import at.cpickl.gadsu.appointments.AbortAppointmentDialog
import at.cpickl.gadsu.appointments.Appointment
import at.cpickl.gadsu.appointments.SaveAppointment
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.inputs.DateAndTimePicker
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.addCloseListener
import com.google.common.eventbus.EventBus
import org.joda.time.DateTime
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JTextArea

interface AppointmentWindow {
    fun changeCurrent(newCurrent: Appointment)
    fun showWindow()
    fun hideWindow()

}
class SwingAppointmentWindow @Inject constructor(
        private val swing: SwingFactory,
        private val bus: EventBus
) : MyFrame("Termin"), AppointmentWindow {
    private var current: Appointment = Appointment.insertPrototype("xxx", DateTime(0))

    // TODO change to EL UI infra
    private val inpStartDate = DateAndTimePicker(DateTime(0), "AppointmentS")
    private val inpEndDate = DateAndTimePicker(DateTime(0), "AppointmentE")
    // val inpNote = fields.newTextArea("NOT USED", {it.note}, ViewNames.Client.InputNote)
    private val inpNote = JTextArea()
    init {
        addCloseListener { bus.post(AbortAppointmentDialog()) }
        contentPane.layout = BorderLayout()
        contentPane.add(initMainPanel(), BorderLayout.CENTER)
        contentPane.add(initSouthPanel(), BorderLayout.SOUTH)
        pack()
        isResizable = false
    }

    private fun initMainPanel() = FormPanel().apply {
        addFormInput("Start", inpStartDate)
        addFormInput("Ende", inpEndDate)
        addFormInput("Note", inpNote)
    }

    private fun initSouthPanel() = GridPanel().apply {
        with (c) {
            add(swing.newEventButton("Speichern", "TODO_VIEWNAME", { SaveAppointment(readInput()) }))
            gridx++
            add(swing.newEventButton("Abbrechen", "TODO_VIEWNAME", { AbortAppointmentDialog() }))
        }
    }

    private fun readInput() = Appointment(current.id, current.clientId, current.created,
            inpStartDate.readDateTime(), inpEndDate.readDateTime(), inpNote.text)

    override fun changeCurrent(newCurrent: Appointment) {
        current = newCurrent
        inpStartDate.writeDateTime(current.start)
        inpEndDate.writeDateTime(current.end)
        inpNote.text = current.note
    }


    override fun showWindow() {
        // TODO position to a "good" location
        isVisible = true
    }

    override fun hideWindow() {
        isVisible = false
    }
}
