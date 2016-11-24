package at.cpickl.gadsu.appointment.view

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.CreateAppointmentEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.ListyView
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.logic.ElementNotFoundInIndexableModel
import javax.inject.Inject

class AppoinmentsInClientView @Inject constructor(
        swing: SwingFactory,
        list: AppointmentList
) : ListyView<Appointment>(list, swing.newEventButton("Neuen Termin erstellen", "ViewName_TODO", { CreateAppointmentEvent() })) {

    private val log = LOG(javaClass)

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
        try {
            list.removeElementByComparator(appointment.idComparator)
        } catch (e: ElementNotFoundInIndexableModel) {
            log.debug("Not removed appointment ($appointment), as it might be in past and was deleted by gcal sync (or simply for a different user).")
        }
    }

}
