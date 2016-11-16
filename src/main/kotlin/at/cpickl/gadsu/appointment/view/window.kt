package at.cpickl.gadsu.appointment.view

import at.cpickl.gadsu.appointment.AbortAppointmentDialogEvent
import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.SaveAppointment
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.InternetConnectionController
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.OpenWebpageEvent
import at.cpickl.gadsu.treatment.PrefilledTreatment
import at.cpickl.gadsu.treatment.PrepareNewTreatmentEvent
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.addFormInput
import at.cpickl.gadsu.view.components.DisabledTextField
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.gadsuWidth
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.newPersistableEventButton
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.addCloseListener
import com.google.common.eventbus.EventBus
import org.joda.time.DateTime
import org.joda.time.Duration
import java.awt.BorderLayout
import java.net.URL
import javax.inject.Inject
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel

interface AppointmentWindow {
    fun changeCurrent(newCurrent: Appointment)
    fun isShowing(appointment: Appointment): Boolean
    fun showWindow()
    fun hideWindow()
    fun close()
}

class SwingAppointmentWindow @Inject constructor(
        swing: SwingFactory,
        private val bus: EventBus,
        private val currentClient: CurrentClient,
        private val mainFrame: MainFrame, // unfortunately we need the whole big mainframe to center this window
        internetController: InternetConnectionController,
        prefs: Prefs
        // TODO make it a dialog. but if do strange things happen: not closeable anymore as no event is properly dispatched to controller :-/
//) : MyDialog(mainFrame.asJFrame(), "Termin"), AppointmentWindow, ModificationAware {
) : MyFrame("Termin"), AppointmentWindow, ModificationAware {

    private var current: Appointment = Appointment.insertPrototype("xxx", DateTime(0))

    private val log = LOG(javaClass)

    private val btnSave = swing.newPersistableEventButton(ViewNames.Appointment.ButtonSave, { SaveAppointment(readInput()) }).gadsuWidth()
    private val btnCancel = swing.newEventButton("Abbrechen", ViewNames.Appointment.ButtonCancel, { AbortAppointmentDialogEvent() }).gadsuWidth()

    private val modificationChecker = ModificationChecker(this, btnSave)
    private val fields: Fields<Appointment> = Fields(modificationChecker)

    private val outClient = DisabledTextField()
    private val inpStartDate = fields.newDateAndTimePicker("Beginn", DateTime(0), { it.start }, ViewNames.Appointment.InputStartDate)
    private val inpDuration = fields.newMinutesField("Dauer", { Duration(it.start, it.end).standardMinutes.toInt()}, ViewNames.Appointment.InputDuration, 3)//fields.newTimePicker("Ende", DateTime(0), { it.end }, "Appointment.DateEnd")
    private val inpNote = fields.newTextArea("Notiz", { it.note }, ViewNames.Appointment.InputNote, 4)
    private val btnOpenGcal = JButton("Calender \u00f6ffnen").apply { addActionListener { onOpenGCal() } }

    private val btnNewTreatment = JButton().apply {
        text = "Neue Behandlung erstellen"
        name = ViewNames.Appointment.ButtonNewTreatment
        addActionListener {
            bus.post(AbortAppointmentDialogEvent())
            bus.post(PrepareNewTreatmentEvent(PrefilledTreatment(inpStartDate.selectedDate, inpDuration.numberValue)))
        }
    }

    init {
        val gcalVisible = internetController.isConnected && prefs.preferencesData.gcalName != null
        modificationChecker.disableAll()

        rootPane.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
        addCloseListener { bus.post(AbortAppointmentDialogEvent()) }
        contentPane.layout = BorderLayout()
        contentPane.add(initMainPanel(gcalVisible), BorderLayout.CENTER)
        contentPane.add(initSouthPanel(), BorderLayout.SOUTH)
        pack()
        isResizable = false
    }

    private fun initMainPanel(gcalVisible: Boolean) = FormPanel().apply {
        addFormInput("Klient", outClient)

        val durationPanel = GridPanel()
        with(durationPanel) {
            add(inpStartDate.toComponent())
            c.gridx++
            c.insets = Pad.LEFT
            add(inpDuration.toComponent())
            c.gridx++
            c.insets = Pad.NONE
            add(JLabel(" Minuten"))
        }
        addFormInput("Datum", durationPanel)

        addFormInput(inpNote)
        addFormInput("", btnNewTreatment)

        if (gcalVisible) addFormInput("Google", btnOpenGcal)
    }

    private fun initSouthPanel() = GridPanel().apply {
        with (c) {
            insets = Pad.TOP
            add(btnSave)
            gridx++
            add(btnCancel)
        }
    }

    override fun isShowing(appointment: Appointment) = current.id == appointment.id

    override fun changeCurrent(newCurrent: Appointment) {
        current = newCurrent

        outClient.text = currentClient.data.fullName
        btnOpenGcal.isEnabled = current.gcalUrl != null
        btnSave.changeLabel(current)
        btnNewTreatment.isEnabled = current.yetPersisted
        fields.updateAll(current)
        modificationChecker.disableAll()
    }

    override fun isModified(): Boolean {
        return fields.isAnyModified(current)
    }

    override fun showWindow() {
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

    private fun readInput(): Appointment {
        val startDate = inpStartDate.selectedDate
        val endTime = startDate.plusMinutes(inpDuration.numberValue)
        val endDate = startDate.withHourOfDay(endTime.hourOfDay).withMinuteOfHour(endTime.minuteOfHour)
        return Appointment(current.id, current.clientId, current.created, startDate, endDate, inpNote.text, current.gcalId, current.gcalUrl)
    }

    private fun onOpenGCal() {
        bus.post(OpenWebpageEvent(URL(current.gcalUrl!!)))
    }

}
