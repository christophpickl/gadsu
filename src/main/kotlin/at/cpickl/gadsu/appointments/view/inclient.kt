package at.cpickl.gadsu.appointments.view

import at.cpickl.gadsu.appointments.Appointment
import at.cpickl.gadsu.appointments.AppointmentChangedEvent
import at.cpickl.gadsu.appointments.AppointmentSavedEvent
import at.cpickl.gadsu.appointments.AppointmentService
import at.cpickl.gadsu.appointments.CreateAppointmentEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentChangedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.forClient
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.SingleButtonPanel
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.Subscribe
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JPanel

@Logged
open class AppoinmentsInClientViewController @Inject constructor(
        private val view: AppoinmentsInClientView,
        private val service: AppointmentService,
        private val clock: Clock
) {

    private val log = LOG(javaClass)

    @Subscribe open fun onCurrentChangedEvent(event: CurrentChangedEvent) {
        event.forClient {
            if (it == null || !it.yetPersisted) {
                view.disableUi()
            } else {
                view.updateUiByClient(it, service.findAllFutureFor(it))
            }
        }
    }


    @Subscribe open fun onAppointmentSavedEvent(event: AppointmentSavedEvent) {
        if (event.appointment.start.isBefore(clock.now())) {
            log.trace("Not going to insert created appointment into list, as start date is in past.")
            return
        }
        view.insert(event.appointment)
    }

    @Subscribe open fun onAppointmentChangedEvent(event: AppointmentChangedEvent) {
        view.change(event.appointment)
    }
}

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


}
