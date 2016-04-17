package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.ClientUnselectedEvent
import at.cpickl.gadsu.treatment.TreatmentChangedEvent
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentDeletedEvent
import at.cpickl.gadsu.treatment.TreatmentRepository
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject


class TreatmentsInClientController @Inject constructor(
        private val view: TreatmentsInClientView,
        private val repository: TreatmentRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private var recentClient: Client? = null

    @Subscribe fun onClientSelectedEvent(event: ClientSelectedEvent) {
        recentClient = event.client

        view.initData(repository.findAllFor(event.client))
        view.enableNewButton(true)
    }

    @Subscribe fun onClientUnselectedEvent(event: ClientUnselectedEvent) {
        log.debug("onClientUnselectedEvent(event={})", event)
        view.enableNewButton(false)
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

}
