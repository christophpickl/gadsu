package at.cpickl.gadsu.appointment.view

import at.cpickl.gadsu.appointment.AbortAppointmentDialogEvent
import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.SaveAppointment
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.InternetConnectionController
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.OpenWebpageEvent
import at.cpickl.gadsu.treatment.CreateTreatmentEvent
import at.cpickl.gadsu.treatment.PrefilledTreatment
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.addFormInput
import at.cpickl.gadsu.view.components.DisabledTextField
import at.cpickl.gadsu.view.components.MyFrame
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
import javax.swing.JPanel

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
        private val mainFrame: MainFrame, // unfortunately we need the whole big mainframe to center this window
        internetController: InternetConnectionController,
        prefs: Prefs
        // TODO make it a dialog. but if do strange things happen: not closeable anymore as no event is properly dispatched to controller :-/
//) : MyDialog(mainFrame.asJFrame(), "Termin"), AppointmentWindow, ModificationAware {
) : MyFrame("Termin"), AppointmentWindow, ModificationAware {
    private var current: Appointment = Appointment.insertPrototype("xxx", DateTime(0))

    private val log = LOG(javaClass)
    private val btnSave = swing.newPersistableEventButton("TODO_VIEWNAME", { SaveAppointment(readInput()) })

    private val modificationChecker = ModificationChecker(this, btnSave)
    private val fields: Fields<Appointment> = Fields(modificationChecker)

    private val inpStartDate = fields.newDateAndTimePicker("Beginn", DateTime(0), { it.start }, "Appointment.DateStart")

    private val inpDuration = fields.newMinutesField("Dauer", { Duration(it.start, it.end).standardMinutes.toInt()}, "Appointment.Duration", 3)//fields.newTimePicker("Ende", DateTime(0), { it.end }, "Appointment.DateEnd")
    private val inpNote = fields.newTextArea("Notiz", { it.note }, "Appointment.Note", 2)
    private val outClient = DisabledTextField()
    private val btnOpenGcal = JButton("Calender \u00f6ffnen").apply { addActionListener { onOpenGCal() } }

    private val btnNewTreatment = JButton().apply {
        text = "Neue Behandlung erstellen"
        name = "Appointment.ButtonNewTreatment"
        addActionListener {
            bus.post(AbortAppointmentDialogEvent())
            bus.post(CreateTreatmentEvent(PrefilledTreatment(inpStartDate.selectedDate, inpDuration.numberValue)))
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
        addFormInput(inpStartDate)
        val durationPanel = JPanel(BorderLayout())
        durationPanel.add(inpDuration.toComponent(), BorderLayout.CENTER)
        durationPanel.add(JLabel(" Minuten"), BorderLayout.EAST)
        addFormInput("Dauer", durationPanel)

        addFormInput(inpNote)
        addFormInput("", btnNewTreatment)

        if (gcalVisible) addFormInput("Google", btnOpenGcal)
    }

    private fun initSouthPanel() = GridPanel().apply {
        with (c) {
            insets = Pad.TOP
            add(btnSave)
            gridx++
            add(swing.newEventButton("Abbrechen", "Appointment.ButtonCancel", { AbortAppointmentDialogEvent() }))
        }
    }

    private fun readInput(): Appointment {
        val startDate = inpStartDate.selectedDate
        val endTime = startDate.plusMinutes(inpDuration.numberValue)
        val endDate = startDate.withHourOfDay(endTime.hourOfDay).withMinuteOfHour(endTime.minuteOfHour)
        return Appointment(current.id, current.clientId, current.created, startDate, endDate, inpNote.text, current.gcalId, current.gcalUrl)
    }

    override fun changeCurrent(newCurrent: Appointment) {
        current = newCurrent

        outClient.text = currentClient.data.fullName
        btnOpenGcal.isEnabled = current.gcalUrl != null
        btnSave.changeLabel(current)
        fields.updateAll(current)
        modificationChecker.disableAll()
    }

    private fun onOpenGCal() {
        bus.post(OpenWebpageEvent(URL(current.gcalUrl!!)))
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
