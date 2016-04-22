package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.ClientUnselectedEvent
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.treatment.DeleteTreatmentEvent
import at.cpickl.gadsu.treatment.TreatmentChangedEvent
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentDeletedEvent
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject


@Logged
open class TreatmentsInClientController @Inject constructor(
        private val view: TreatmentsInClientView,
        private val service: TreatmentService,
        private val dialogs: Dialogs
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private var recentClient: Client? = null // MINOR change to Current infra

    @Subscribe open fun onClientSelectedEvent(event: ClientSelectedEvent) {
        recentClient = event.client

        view.enableData(service.findAllFor(event.client))
    }

    @Subscribe open fun onClientUnselectedEvent(event: ClientUnselectedEvent) {
        view.disableData()
    }

    @Subscribe open fun onTreatmentCreatedEvent(event: TreatmentCreatedEvent) {
        if (!event.treatment.clientId.equals(recentClient?.id)) {
            return
        }
        view.insert(event.treatment)
    }

    @Subscribe open fun onTreatmentDeletedEvent(event: TreatmentDeletedEvent) {
        if (!event.treatment.clientId.equals(recentClient?.id)) {
            return
        }
        view.delete(event.treatment)
    }

    @Subscribe open fun onTreatmentChangedEvent(event: TreatmentChangedEvent) {
        view.change(event.treatment)
    }

    @Subscribe open fun onDeleteTreatmentEvent(event: DeleteTreatmentEvent) {
        dialogs.confirmedDelete("die Behandlung Nr. ${event.treatment.number}", {
            service.delete(event.treatment)

            // FIXME deselect??
        })
    }

}
