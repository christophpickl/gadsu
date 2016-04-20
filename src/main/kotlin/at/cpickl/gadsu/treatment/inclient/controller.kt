package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.ClientUnselectedEvent
import at.cpickl.gadsu.treatment.DeleteTreatmentEvent
import at.cpickl.gadsu.treatment.TreatmentChangedEvent
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentDeletedEvent
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject


class TreatmentsInClientController @Inject constructor(
        private val view: TreatmentsInClientView,
        private val service: TreatmentService,
        private val dialogs: Dialogs
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private var recentClient: Client? = null // MINOR change to Current infra

    @Subscribe fun onClientSelectedEvent(event: ClientSelectedEvent) {
        log.debug("onClientSelectedEvent(event={})", event)
        recentClient = event.client

        view.enableData(service.findAllFor(event.client))
    }

    @Subscribe fun onClientUnselectedEvent(event: ClientUnselectedEvent) {
        log.debug("onClientUnselectedEvent(event={})", event)
        view.disableData()
    }

    @Subscribe fun onTreatmentCreatedEvent(event: TreatmentCreatedEvent) {
        if (!event.treatment.clientId.equals(recentClient?.id)) {
            return
        }
        view.insert(event.treatment)
    }

    @Subscribe fun onTreatmentDeletedEvent(event: TreatmentDeletedEvent) {
        if (!event.treatment.clientId.equals(recentClient?.id)) {
            return
        }
        view.delete(event.treatment)
    }

    @Subscribe fun onTreatmentChangedEvent(event: TreatmentChangedEvent) {
        log.debug("onTreatmentChangedEvent(event={})", event)
        view.change(event.treatment)
    }

    @Subscribe fun onDeleteTreatmentEvent(event: DeleteTreatmentEvent) {
        log.debug("onDeleteTreatmentEvent(event={})", event)

        dialogs.confirmedDelete("die Behandlung Nr. ${event.treatment.number}", {
            service.delete(event.treatment)

            // FIXME deselect??
        })
    }

}
