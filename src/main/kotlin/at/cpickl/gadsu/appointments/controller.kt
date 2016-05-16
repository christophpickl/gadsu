package at.cpickl.gadsu.appointments

import at.cpickl.gadsu.appointments.view.AppointmentWindow
import at.cpickl.gadsu.persistence.ensurePersisted
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.clearMinutes
import com.google.common.eventbus.Subscribe
import javax.inject.Inject

interface AppointmentController {

}

@Logged
open class AppointmentControllerImpl @Inject constructor(
    private val window: AppointmentWindow,
    private val clock: Clock,
    private val currentClient: CurrentClient,
    private val service: AppointmentService
) : AppointmentController {

    @Subscribe open fun onCreateAppointmentEvent(event: CreateAppointmentEvent) {
        currentClient.data.ensurePersisted()
        window.changeCurrent(Appointment.insertPrototype(currentClient.data.id!!, clock.now().clearMinutes().withHourOfDay(12)))
        window.showWindow()
    }

    @Subscribe open fun onSaveAppointment(event: SaveAppointment) {
        service.insertOrUpdate(event.appointment)
        window.hideWindow()
    }

    @Subscribe open fun onAbortAppointmentDialog(event: AbortAppointmentDialog) {
        window.hideWindow()
    }
}
