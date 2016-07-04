package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.UserEvent
import com.google.common.base.MoreObjects
import org.joda.time.DateTime

data class PrefilledTreatment(val start: DateTime, val duration: Int)

class CreateTreatmentEvent(val prefilled: PrefilledTreatment? = null) : UserEvent()

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

class TreatmentSaveEvent(val treatment: Treatment) : UserEvent() {
    override fun toString(): String{
        return "TreatmentSaveEvent(treatment=$treatment)"
    }
}

class TreatmentCreatedEvent(val treatment: Treatment) : AppEvent() {
    override fun toString() = MoreObjects.toStringHelper(this)
            .add("treatment", treatment)
            .toString()
}

class TreatmentChangedEvent(val treatment: Treatment) : AppEvent()

class TreatmentDeletedEvent(val treatment: Treatment) : AppEvent()

class PreviousTreatmentEvent() : UserEvent()
class NextTreatmentEvent() : UserEvent()
