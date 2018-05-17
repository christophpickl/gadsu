package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.global.AppEvent
import at.cpickl.gadsu.global.UserEvent
import com.google.common.base.MoreObjects
import org.joda.time.DateTime

data class PrefillByAppointmentTreatment(val start: DateTime, val duration: Int)

data class PrepareNewTreatmentEvent(val prefillByAppointment: PrefillByAppointmentTreatment? = null) : UserEvent()

class OpenTreatmentEvent(val treatment: Treatment) : UserEvent() {
    override fun toString(): String{
        return "OpenTreatmentEvent(treatment=$treatment)"
    }
}

class DeleteTreatmentEvent(val treatment: Treatment) : UserEvent() {
    override fun toString(): String{
        return "DeleteTreatmentEvent(treatment=$treatment)"
    }
}

/**
 * Return back to the client view.
 */
class TreatmentBackEvent() : UserEvent()

/**
 * Save the currently displayed treatment in TreatmentView.
 */
class TreatmentSaveEvent() : UserEvent()

class TreatmentCreatedEvent(val treatment: Treatment) : AppEvent() {
    override fun toString() = MoreObjects.toStringHelper(this)
            .add("treatment", treatment)
            .toString()
}

class TreatmentChangedEvent(val treatment: Treatment) : AppEvent()

class TreatmentDeletedEvent(
        val treatment: Treatment,
        val treatmentHasBeenProtocolizedYet: Boolean // dont calculate when receiving this event, as the treatment has been deleted already in DB ;)
) : AppEvent() {
}

class PreviousTreatmentEvent() : UserEvent()
class NextTreatmentEvent() : UserEvent()
