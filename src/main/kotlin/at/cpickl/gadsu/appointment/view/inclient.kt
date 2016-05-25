package at.cpickl.gadsu.appointment.view

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.CreateAppointmentEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.ListyView
import at.cpickl.gadsu.view.components.newEventButton
import javax.inject.Inject

class AppoinmentsInClientView @Inject constructor(
        private val swing: SwingFactory,
        list: AppointmentList
) : ListyView<Appointment>(list, swing.newEventButton("Termin Erstellen", "ViewName_TODO", { CreateAppointmentEvent() })) {

    init {
        createButton.isEnabled = false
    }

    fun updateUiByClient(client: Client, appointments: List<Appointment>) {
        list.resetData(appointments)
        createButton.isEnabled = client.yetPersisted
    }

    fun disableUi() {
        list.clear()
        createButton.isEnabled = false
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
