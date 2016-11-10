package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.treatment.PrepareNewTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.ListyView
import at.cpickl.gadsu.view.components.newEventButton
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import javax.inject.Inject

class TreatmentsInClientView @Inject constructor(
        swing: SwingFactory,
        private val treatmentsList: TreatmentList
): ListyView<Treatment>(
        treatmentsList,
        swing.newEventButton("Neue Behandlung erstellen", ViewNames.Treatment.OpenNewButton, { PrepareNewTreatmentEvent() })
) {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        createButton.isEnabled = false // disabled by default at startup
    }

    fun insert(treatment: Treatment) {
        log.trace("insert(treatment={})", treatment)
        // insert new treatment at top, as expecting it to have the highest number (which is ordered by DESC)
        treatmentsList.addElementAtTop(treatment)
    }

    fun delete(treatment: Treatment) {
        log.trace("delete(treatment={})", treatment)
        treatmentsList.removeElementByComparator(treatment.idComparator)
    }

    fun change(treatment: Treatment) {
        log.trace("change(treatment={})", treatment)
        treatmentsList.setElementByComparator(treatment, treatment.idComparator)
    }

    fun enableData(treatments: List<Treatment>) {
        log.trace("enableData(treatments={})", treatments)
        // data will be ordered by number DESC

        treatmentsList.resetData(treatments)
        createButton.isEnabled = true
    }

    fun disableData() {
        log.debug("disableData()")

        treatmentsList.clear()
        createButton.isEnabled = false
    }

}
