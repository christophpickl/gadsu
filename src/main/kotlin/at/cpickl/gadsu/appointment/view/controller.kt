package at.cpickl.gadsu.appointment.view

import at.cpickl.gadsu.appointment.AppointmentChangedEvent
import at.cpickl.gadsu.appointment.AppointmentDeletedEvent
import at.cpickl.gadsu.appointment.AppointmentSavedEvent
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.forClient
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentChangedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import com.google.common.eventbus.Subscribe
import javax.inject.Inject

@Logged
open class AppoinmentsInClientViewController @Inject constructor(
        private val view: AppoinmentsInClientView,
        private val service: AppointmentService,
        private val clock: Clock,
        private val currentClient: CurrentClient
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
        if (currentClient.data.id != event.appointment.clientId) {
            return
        }
        view.insert(event.appointment)
    }

    @Subscribe open fun onAppointmentDeletedEvent(event: AppointmentDeletedEvent) {
        if (currentClient.data.id != event.appointment.clientId) {
            return
        }
        view.delete(event.appointment)
    }

    @Subscribe open fun onAppointmentChangedEvent(event: AppointmentChangedEvent) {
        if (currentClient.data.id != event.appointment.clientId) {
            return
        }
        view.change(event.appointment)
    }
}
