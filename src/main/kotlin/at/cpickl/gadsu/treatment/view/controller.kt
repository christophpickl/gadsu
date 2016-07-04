package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.ShowClientViewEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.treatment.*
import at.cpickl.gadsu.view.ChangeMainContentEvent
import at.cpickl.gadsu.view.MainContentChangedEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject

@Logged
open class TreatmentController @Inject constructor(
        private val treatmentViewFactory: TreatmentViewFactory,
        private val treatmentService: TreatmentService,
        private val currentClient: CurrentClient,
        private val currentTreatment: CurrentTreatment,
        private val bus: EventBus,
        private val clock: Clock
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private var treatmentView: TreatmentView? = null


    @Subscribe open fun onCreateTreatmentEvent(event: CreateTreatmentEvent) {
        changeToTreatmentView(null, event.prefilled)
    }

    @Subscribe open fun onOpenTreatmentEvent(event: OpenTreatmentEvent) {
        changeToTreatmentView(event.treatment, null)
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

        currentTreatment.data = treatmentAfterSave
        treatmentView!!.wasSaved(treatmentAfterSave)
    }

    @Subscribe open fun onTreatmentBackEvent(event: TreatmentBackEvent) {
        // TODO check changes for treatment

        currentTreatment.data = null
        bus.post(ShowClientViewEvent())
    }

    @Subscribe open fun onMainContentChangedEvent(event: MainContentChangedEvent) {
        if (treatmentView != null && event.oldContent === treatmentView) {
            treatmentView!!.closePreparations()
        }
    }
    
    @Subscribe open fun onPreviousTreatmentEvent(event: PreviousTreatmentEvent) {
        val newTreatment = treatmentService.prevAndNext(currentTreatment.data!!).first
        changeToTreatmentView(newTreatment, null)
    }
    
    @Subscribe open fun onNextTreatmentEvent(event: NextTreatmentEvent) {
        val newTreatment = treatmentService.prevAndNext(currentTreatment.data!!).second
        changeToTreatmentView(newTreatment, null)
    }

    private fun changeToTreatmentView(treatment: Treatment?, prefilled: PrefilledTreatment?) {
        val client = currentClient.data

        val nullSafeTreatment: Treatment
        if (treatment != null) {
            nullSafeTreatment = treatment
        } else {
            val number = treatmentService.calculateNextNumber(client)
            val startDate = if (prefilled == null) clock.now() else prefilled.start
            val duration = if (prefilled == null) Treatment.DEFAULT_DURATION else minutes(prefilled.duration)
             nullSafeTreatment = Treatment.insertPrototype(
                     clientId = client.id!!,
                     number = number,
                     date = startDate,
                     duration = duration)
        }

        currentTreatment.data = nullSafeTreatment
        treatmentView = treatmentViewFactory.create(client, nullSafeTreatment)

        if (!nullSafeTreatment.yetPersisted) {
            treatmentView!!.enablePrev(false)
            treatmentView!!.enableNext(false)
        } else {
            val prevNext = treatmentService.prevAndNext(nullSafeTreatment)
            treatmentView!!.enablePrev(prevNext.first != null)
            treatmentView!!.enableNext(prevNext.second != null)
        }

        bus.post(ChangeMainContentEvent(treatmentView!!))
    }
}
