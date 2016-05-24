package at.cpickl.gadsu.appointments.view

import at.cpickl.gadsu.appointments.AppointmentChangedEvent
import at.cpickl.gadsu.appointments.AppointmentDeletedEvent
import at.cpickl.gadsu.appointments.AppointmentSavedEvent
import at.cpickl.gadsu.appointments.AppointmentService
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentChangedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.forClient
import com.google.common.eventbus.Subscribe
import javax.inject.Inject


// FIXME s in general for #19 appointment
// !!! view size distribution of appointment vs treatment
// - cleanup window form:
//   * start end time in separate row
//   * check if end > start
//   * bigger
// - center window on show
// - be able to save at very first show
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

    @Subscribe open fun onAppointmentDeletedEvent(event: AppointmentDeletedEvent) {
        view.delete(event.appointment)
    }

    @Subscribe open fun onAppointmentChangedEvent(event: AppointmentChangedEvent) {
        view.change(event.appointment)
    }
}
