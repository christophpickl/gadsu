package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.ShowClientViewEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.treatment.CreateTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentRepository
import at.cpickl.gadsu.treatment.TreatmentSaveEvent
import at.cpickl.gadsu.treatment.TreatmentViewFactory
import at.cpickl.gadsu.view.MainWindow
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject

class TreatmentController @Inject constructor(
        private val window: MainWindow,
        private val treatmentViewFactory: TreatmentViewFactory,
        private val treatmentRepository: TreatmentRepository,
        private val currentClient: CurrentClient,
        private val bus: EventBus,
        private val clock: Clock
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onCreateTreatmentEvent(event: CreateTreatmentEvent) {
        log.debug("onCreateTreatmentEvent(event={})", event)

        val client = currentClient.data!!
        val treatmentView = treatmentViewFactory.create(client, Treatment.insertPrototype(client.id!!, 42, clock.now()))

        window.changeContent(treatmentView.asComponent())
    }

    @Subscribe fun onTreatmentSaveEvent(event: TreatmentSaveEvent) {
        log.info("onTreatmentSaveEvent(event={})", event)

        val treatmentToSave = event.treatment
        if (treatmentToSave.yetPersisted) {
            // FIXME treatmentRepository.update(treatmentToSave)
            // bus.post(TreatmentChangedEvent(treatmentToSave))

        } else {
            val insertedTreatment = treatmentRepository.insert(treatmentToSave, event.client)
            bus.post(TreatmentCreatedEvent(insertedTreatment))
        }
    }

    @Subscribe fun onTreatmentBackEvent(event: TreatmentBackEvent) {
        log.debug("onTreatmentBackEvent(event={})", event)

        // TODO check changes

        bus.post(ShowClientViewEvent())
    }
}
