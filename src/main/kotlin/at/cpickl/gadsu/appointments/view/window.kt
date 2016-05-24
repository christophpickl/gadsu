package at.cpickl.gadsu.appointments.view

import at.cpickl.gadsu.appointments.AbortAppointmentDialog
import at.cpickl.gadsu.appointments.Appointment
import at.cpickl.gadsu.appointments.SaveAppointment
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.addFormInput
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.newPersistableEventButton
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.addCloseListener
import com.google.common.eventbus.EventBus
import org.joda.time.DateTime
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.BorderFactory
import javax.swing.JLabel

interface AppointmentWindow {
    fun changeCurrent(newCurrent: Appointment)
    fun showWindow()
    fun hideWindow()
    fun close()
}

class SwingAppointmentWindow @Inject constructor(
        private val swing: SwingFactory,
        private val bus: EventBus,
        private val currentClient: CurrentClient,
        private val mainFrame: MainFrame // unfortunately we need the whole big mainframe to center this window
        // FIXME make it a dialog. but if do strange things happen: not closeable anymore as no event is properly dispatched to controller :-/
//) : MyDialog(mainFrame.asJFrame(), "Termin"), AppointmentWindow, ModificationAware {
) : MyFrame("Termin"), AppointmentWindow, ModificationAware {
    private var current: Appointment = Appointment.insertPrototype("xxx", DateTime(0))

    private val log = LOG(javaClass)
    private val btnSave = swing.newPersistableEventButton("TODO_VIEWNAME", { SaveAppointment(readInput()) })

    private val modificationChecker = ModificationChecker(this, btnSave)
    private val fields: Fields<Appointment> = Fields(modificationChecker)

    private val inpStartDate = fields.newDateAndTimePicker("Beginn", DateTime(0), { it.start }, "Appointment.DateStart")

    private val inpEndDate = fields.newTimePicker("Ende", DateTime(0), { it.end }, "Appointment.DateEnd")
    private val inpNote = fields.newTextArea("Notiz", { it.note }, "Appointment.Note", 2)
    private val outClient = JLabel()
    init {
        modificationChecker.disableAll()

        rootPane.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
        addCloseListener { bus.post(AbortAppointmentDialog()) }
        contentPane.layout = BorderLayout()
        contentPane.add(initMainPanel(), BorderLayout.CENTER)
        contentPane.add(initSouthPanel(), BorderLayout.SOUTH)
        pack()
        isResizable = false
    }

    private fun initMainPanel() = FormPanel().apply {
        addFormInput("Klient", outClient)
        addFormInput(inpStartDate)
        addFormInput(inpEndDate)
        addFormInput(inpNote)
    }

    private fun initSouthPanel() = GridPanel().apply {
        with (c) {
            add(btnSave)
            gridx++
            add(swing.newEventButton("Abbrechen", "TODO_VIEWNAME", { AbortAppointmentDialog() }))
        }
    }

    private fun readInput(): Appointment {
        val startDate = inpStartDate.selectedDate
        val endTime = inpEndDate.selectedTime
        val endDate = startDate.withHourOfDay(endTime.hourOfDay).withMinuteOfHour(endTime.minuteOfHour)
        return Appointment(current.id, current.clientId, current.created, startDate, endDate, inpNote.text)
    }

    override fun changeCurrent(newCurrent: Appointment) {
        current = newCurrent

        outClient.text = currentClient.data.fullName
        btnSave.changeLabel(current)
        fields.updateAll(current)
        modificationChecker.disableAll()
    }

    override fun isModified(): Boolean {
        return fields.isAnyModified(current)
    }

    override fun showWindow() {
        // TODO position to a "good" location
        if (isVisible == false) {
            setLocationRelativeTo(mainFrame.asJFrame())
            isVisible = true
        }
    }

    override fun hideWindow() {
        log.debug("hideWindow()")
        dispose() // only works for JDialog :-/
//        isVisible = false // only works for JFrame
    }

    override fun close() {
        hideAndClose()
    }
}
