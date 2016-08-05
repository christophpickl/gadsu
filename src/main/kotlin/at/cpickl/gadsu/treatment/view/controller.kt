package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.ShowClientViewEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.treatment.CurrentTreatment
import at.cpickl.gadsu.treatment.NextTreatmentEvent
import at.cpickl.gadsu.treatment.OpenTreatmentEvent
import at.cpickl.gadsu.treatment.PrefilledTreatment
import at.cpickl.gadsu.treatment.PrepareNewTreatmentEvent
import at.cpickl.gadsu.treatment.PreviousTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.treatment.TreatmentChangedEvent
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentSaveEvent
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.treatment.TreatmentViewFactory
import at.cpickl.gadsu.view.ChangeMainContentEvent
import at.cpickl.gadsu.view.GadsuMenuBar
import at.cpickl.gadsu.view.MainContentChangedEvent
import at.cpickl.gadsu.view.MainContentType
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.components.Dialogs
import at.cpickl.gadsu.view.logic.ChangeBehaviour
import at.cpickl.gadsu.view.logic.ChangesChecker
import at.cpickl.gadsu.view.logic.ChangesCheckerCallback
import at.cpickl.gadsu.view.swing.MyKeyListener
import at.cpickl.gadsu.view.swing.RegisteredKeyListener
import at.cpickl.gadsu.view.swing.registerMyKeyListener
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.swing.JComponent

@Logged
open class TreatmentController @Inject constructor(
        private val treatmentViewFactory: TreatmentViewFactory,
        private val treatmentService: TreatmentService,
        private val currentClient: CurrentClient,
        private val currentTreatment: CurrentTreatment,
        private val bus: EventBus,
        private val clock: Clock,
        private val mainFrame: MainFrame, // need a proper handle to register keyboard listener
        private val menuBar: GadsuMenuBar, // do it the simple way: hard wire the dependency ;) could use events instead...
        dialogs: Dialogs
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private var treatmentView: TreatmentView? = null
    private var registeredEscapeListener: RegisteredKeyListener? = null

    private val changesChecker = ChangesChecker(dialogs, object : ChangesCheckerCallback {
        override fun isModified() = treatmentView?.isModified() ?: false
        override fun save() = saveCurrentTreatment()
    })

    @Subscribe open fun onPrepareNewTreatmentEvent(event: PrepareNewTreatmentEvent) {
        if (changesChecker.checkChanges() == ChangeBehaviour.ABORT) {
            return
        }
        changeToTreatmentView(null, event.prefilled)
    }

    @Subscribe open fun onOpenTreatmentEvent(event: OpenTreatmentEvent) {
        if (changesChecker.checkChanges() == ChangeBehaviour.ABORT) {
            return
        }
        changeToTreatmentView(event.treatment, null)
    }

    @Subscribe open fun onTreatmentSaveEvent(event: TreatmentSaveEvent) {
        saveCurrentTreatment()
    }

    @Subscribe open fun onTreatmentBackEvent(event: TreatmentBackEvent) {
        if (changesChecker.checkChanges() == ChangeBehaviour.ABORT) {
            return
        }

        currentTreatment.data = null
        bus.post(ShowClientViewEvent())
    }

    @Subscribe open fun onMainContentChangedEvent(event: MainContentChangedEvent) {
        if (event.oldContent?.type !=  MainContentType.TREATMENT && event.newContent.type == MainContentType.TREATMENT) {
            log.trace("Navigating TO treatment view.")
            registeredEscapeListener = (mainFrame.asJFrame().contentPane as JComponent).registerMyKeyListener(MyKeyListener.onEscape("abortTreatmentView", {
                log.debug("Escape was hit in treatment view. Dispatching TreatmentBackEvent.")
                bus.post(TreatmentBackEvent())
            }))
        }
        val moveAway = event.oldContent?.type == MainContentType.TREATMENT && event.newContent.type != MainContentType.TREATMENT
        if (moveAway) {
            log.trace("Navigating AWAY from treatment view")
            // check changes already covered by onTreatmentBackEvent
            if (registeredEscapeListener != null) {
                registeredEscapeListener!!.deregisterYourself()
                registeredEscapeListener = null
            }
        }
        if (treatmentView != null && event.oldContent?.type == MainContentType.TREATMENT) {
            treatmentView!!.closePreparations()
        }
        if (moveAway) {
            treatmentView = null
        }
    }
    
    @Subscribe open fun onPreviousTreatmentEvent(event: PreviousTreatmentEvent) {
        if (changesChecker.checkChanges() == ChangeBehaviour.ABORT) {
            return
        }
        val newTreatment = treatmentService.prevAndNext(currentTreatment.data!!).first
        changeToTreatmentView(newTreatment, null)
    }
    
    @Subscribe open fun onNextTreatmentEvent(event: NextTreatmentEvent) {
        if (changesChecker.checkChanges() == ChangeBehaviour.ABORT) {
            return
        }
        val newTreatment = treatmentService.prevAndNext(currentTreatment.data!!).second
        changeToTreatmentView(newTreatment, null)
    }

    fun checkChanges(): ChangeBehaviour {
        return changesChecker.checkChanges()
    }

    private fun saveCurrentTreatment() {
        val treatmentAfterSave: Treatment
        val treatmentToSave = treatmentView!!.readTreatment()
        log.debug("saveCurrentTreatment() ... treatmentToSave={}", treatmentToSave)

        if (!treatmentToSave.yetPersisted) {
            val insertedTreatment = treatmentService.insert(treatmentToSave)
            bus.post(TreatmentCreatedEvent(insertedTreatment))
            treatmentAfterSave = insertedTreatment
            updatePrevNextStateFor(insertedTreatment)
        } else {
            treatmentService.update(treatmentToSave)
            bus.post(TreatmentChangedEvent(treatmentToSave))
            treatmentAfterSave = treatmentToSave
        }

        currentTreatment.data = treatmentAfterSave
        treatmentView!!.wasSaved(treatmentAfterSave)
    }

    private fun changeToTreatmentView(treatment: Treatment?, prefilled: PrefilledTreatment?) {
        log.debug("changeToTreatmentView(treatment={}, prefilled={})", treatment, prefilled)
        val client = currentClient.data

        treatmentView?.closePreparations()

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
            updatePrevNextState(false, false)
        } else {
            updatePrevNextStateFor(nullSafeTreatment)
        }

        bus.post(ChangeMainContentEvent(treatmentView!!))
    }

    private fun updatePrevNextStateFor(treatment: Treatment) {
        val prevNext = treatmentService.prevAndNext(treatment)
        updatePrevNextState(prevNext.first != null, prevNext.second != null)
    }

    private fun updatePrevNextState(enablePrev: Boolean, enableNext: Boolean) {
        treatmentView!!.enablePrev(enablePrev)
        treatmentView!!.enableNext(enableNext)
        menuBar.treatmentPrevious.isEnabled = enablePrev
        menuBar.treatmentNext.isEnabled = enableNext
    }
}
