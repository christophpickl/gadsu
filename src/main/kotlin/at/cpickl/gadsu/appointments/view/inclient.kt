package at.cpickl.gadsu.appointments.view

import at.cpickl.gadsu.appointments.Appointment
import at.cpickl.gadsu.appointments.AppointmentService
import at.cpickl.gadsu.appointments.CreateAppointmentEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.CurrentChangedEvent
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.forClient
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.Subscribe
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JPanel

@Logged
open class AppoinmentsInClientViewController @Inject constructor(
        private val view: AppoinmentsInClientView,
        private val service: AppointmentService
) {

    @Subscribe open fun onCurrentChangedEvent(event: CurrentChangedEvent) {
        event.forClient {
            if (it == null || !it.yetPersisted) {
                view.disableUi()
            } else {
                view.updateUiByClient(it, service.findAllFutureFor(it))
            }
        }
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
        add(btnCreateAppointment, BorderLayout.SOUTH)
    }

    fun updateUiByClient(client: Client, appointments: List<Appointment>) {
        list.resetData(appointments)
        btnCreateAppointment.isEnabled = client.yetPersisted
    }

    fun disableUi() {
        list.clear()
        btnCreateAppointment.isEnabled = false
    }


}
