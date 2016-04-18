package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.ShowClientViewEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.treatment.CreateTreatmentEvent
import at.cpickl.gadsu.treatment.OpenTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.treatment.TreatmentChangedEvent
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentSaveEvent
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.treatment.TreatmentViewFactory
import at.cpickl.gadsu.view.MainWindow
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject

class TreatmentController @Inject constructor(
        private val window: MainWindow,
        private val treatmentViewFactory: TreatmentViewFactory,
        private val treatmentService: TreatmentService,
        private val currentClient: CurrentClient,
        private val bus: EventBus,
        private val clock: Clock
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private var treatmentView: TreatmentView? = null


    @Subscribe fun onCreateTreatmentEvent(event: CreateTreatmentEvent) {
        log.debug("onCreateTreatmentEvent(event={})", event)
        changeToTreatmentView(null)
    }

    @Subscribe fun onOpenTreatmentEvent(event: OpenTreatmentEvent) {
        log.debug("onOpenTreatmentEvent(event={})", event)
        changeToTreatmentView(event.treatment)
    }

    private fun changeToTreatmentView(treatment: Treatment?) {
        val client = currentClient.data

        val number = treatmentService.calculateNextNumber(client)
        val nullSafeTreatment = treatment ?: Treatment.insertPrototype(client.id!!, number, clock.now())

        treatmentView = treatmentViewFactory.create(client, nullSafeTreatment)
        window.changeContent(treatmentView!!.asComponent())
    }

    @Subscribe fun onTreatmentSaveEvent(event: TreatmentSaveEvent) {
        log.info("onTreatmentSaveEvent(event={})", event)

        val treatmentAfterSave: Treatment
        val treatmentToSave = event.treatment
        if (!treatmentToSave.yetPersisted) {
            val insertedTreatment = treatmentService.insert(treatmentToSave, event.client)
            bus.post(TreatmentCreatedEvent(insertedTreatment))
            treatmentAfterSave = insertedTreatment
        } else {
            treatmentService.update(treatmentToSave)
            bus.post(TreatmentChangedEvent(treatmentToSave))
            treatmentAfterSave = treatmentToSave

        }
        treatmentView!!.wasSaved(treatmentAfterSave)
    }

    @Subscribe fun onTreatmentBackEvent(event: TreatmentBackEvent) {
        log.debug("onTreatmentBackEvent(event={})", event)

        // FIXME check changes

        bus.post(ShowClientViewEvent())
    }
}
