package at.cpickl.gadsu.appointments.view

import at.cpickl.gadsu.appointments.Appointment
import at.cpickl.gadsu.appointments.CreateAppointmentEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.SingleButtonPanel
import at.cpickl.gadsu.view.swing.scrolled
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JPanel

class AppoinmentsInClientView @Inject constructor(
        private val swing: SwingFactory,
        private val list: AppointmentList
) : JPanel() {

    private val btnCreateAppointment = swing.newEventButton("Termin Erstellen", "ViewName_TODO", { CreateAppointmentEvent() })

    init {
        layout = BorderLayout()
        btnCreateAppointment.isEnabled = false

        add(list.scrolled(), BorderLayout.CENTER)
        add(SingleButtonPanel(btnCreateAppointment), BorderLayout.SOUTH)
    }

    fun updateUiByClient(client: Client, appointments: List<Appointment>) {
        list.resetData(appointments)
        btnCreateAppointment.isEnabled = client.yetPersisted
    }

    fun disableUi() {
        list.clear()
        btnCreateAppointment.isEnabled = false
    }

    fun insert(appointment: Appointment) {
        list.addProperIndex(appointment)
    }

    fun change(appointment: Appointment) {
        list.setElementByComparator(appointment, appointment.idComparator)
    }

    fun delete(appointment: Appointment) {
        list.removeElementByComparator(appointment.idComparator)
    }

}
