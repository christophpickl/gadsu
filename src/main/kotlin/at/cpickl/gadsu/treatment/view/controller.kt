package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.ShowClientViewEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.treatment.CreateTreatmentEvent
import at.cpickl.gadsu.treatment.OpenTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.treatment.TreatmentChangedEvent
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentSaveEvent
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.treatment.TreatmentViewFactory
import at.cpickl.gadsu.view.MainFrame
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject

@Logged
open class TreatmentController @Inject constructor(
        private val frame: MainFrame,
        private val treatmentViewFactory: TreatmentViewFactory,
        private val treatmentService: TreatmentService,
        private val currentClient: CurrentClient,
        private val bus: EventBus,
        private val clock: Clock
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private var treatmentView: TreatmentView? = null


    @Subscribe open fun onCreateTreatmentEvent(event: CreateTreatmentEvent) {
        changeToTreatmentView(null)
    }

    @Subscribe open fun onOpenTreatmentEvent(event: OpenTreatmentEvent) {
        changeToTreatmentView(event.treatment)
    }

    @Subscribe open fun onTreatmentSaveEvent(event: TreatmentSaveEvent) {

        val treatmentAfterSave: Treatment
        val treatmentToSave = event.treatment
        if (!treatmentToSave.yetPersisted) {
            val insertedTreatment = treatmentService.insert(treatmentToSave)
            bus.post(TreatmentCreatedEvent(insertedTreatment))
            treatmentAfterSave = insertedTreatment
        } else {
            treatmentService.update(treatmentToSave)
            bus.post(TreatmentChangedEvent(treatmentToSave))
            treatmentAfterSave = treatmentToSave

        }
        treatmentView!!.wasSaved(treatmentAfterSave)
    }

    @Subscribe open fun onTreatmentBackEvent(event: TreatmentBackEvent) {

        // FIXME check changes
        treatmentView!!.closePreparations()

        bus.post(ShowClientViewEvent())
    }

    private fun changeToTreatmentView(treatment: Treatment?) {
        val client = currentClient.data

        val number = treatmentService.calculateNextNumber(client)
        val nullSafeTreatment = treatment ?: Treatment.insertPrototype(client.id!!, number, clock.now())

        treatmentView = treatmentViewFactory.create(client, nullSafeTreatment)
        frame.changeContent(treatmentView!!.asComponent())
    }
}
