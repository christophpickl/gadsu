package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.UserEvent


class CreateTreatmentEvent() : UserEvent()

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

class TreatmentCreatedEvent(val treatment: Treatment) : AppEvent()

class TreatmentChangedEvent(val treatment: Treatment) : AppEvent()

class TreatmentDeletedEvent(val treatment: Treatment) : AppEvent()
