package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.ShowClientViewEvent
import at.cpickl.gadsu.treatment.CreateTreatmentEvent
import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.view.MainWindow
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject

class TreatmentController @Inject constructor(
        private val window: MainWindow,
        private val treatmentView: TreatmentView,
        private val bus: EventBus
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onCreateTreatmentEvent(event: CreateTreatmentEvent) {
        log.debug("onCreateTreatmentEvent(event={})", event)

        window.changeContent(treatmentView.asComponent())
    }

    @Subscribe fun onTreatmentBackEvent(event: TreatmentBackEvent) {
        log.debug("onTreatmentBackEvent(event={})", event)

        // TODO check changes

        bus.post(ShowClientViewEvent())
    }
}
