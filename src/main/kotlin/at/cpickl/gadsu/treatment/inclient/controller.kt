package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.forClient
import at.cpickl.gadsu.service.CurrentEvent
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.treatment.DeleteTreatmentEvent
import at.cpickl.gadsu.treatment.TreatmentChangedEvent
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentDeletedEvent
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import javax.inject.Inject


@Logged
open class TreatmentsInClientController @Inject constructor(
        private val view: TreatmentsInClientView,
        private val service: TreatmentService,
        private val dialogs: Dialogs,
        private val currentClient: CurrentClient
) {

    @Subscribe open fun onCurrentEvent(event: CurrentEvent) {
        event.forClient { client ->
            if (client == null || !client.yetPersisted) {
                view.disableData()
            } else {
                view.enableData(service.findAllFor(client))
            }
        }
    }

    @Subscribe open fun onTreatmentCreatedEvent(event: TreatmentCreatedEvent) {
        if (event.treatment.clientId != currentClient.data.id) {
            return
        }
        view.insert(event.treatment)
    }

    @Subscribe open fun onTreatmentDeletedEvent(event: TreatmentDeletedEvent) {
        if (event.treatment.clientId != currentClient.data.id) {
            return
        }
        view.delete(event.treatment)
    }

    @Subscribe open fun onDeleteTreatmentEvent(event: DeleteTreatmentEvent) {
        dialogs.confirmedDelete("die Behandlung Nr. ${event.treatment.number}", {
            service.delete(event.treatment)
        })
    }

    @Subscribe open fun onTreatmentChangedEvent(event: TreatmentChangedEvent) {
        view.change(event.treatment)
    }

}
