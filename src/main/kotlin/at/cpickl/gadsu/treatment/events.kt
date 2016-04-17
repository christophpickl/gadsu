package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client


class CreateTreatmentEvent() : UserEvent()

/**
 * Return back to the client view.
 */
class TreatmentBackEvent() : UserEvent()

class TreatmentSaveEvent(val treatment: Treatment, val client: Client) : UserEvent() {
    override fun toString(): String{
        return "TreatmentSaveEvent(treatment=$treatment, client=$client)"
    }
}

class TreatmentCreatedEvent(val treatment: Treatment) : AppEvent()

class TreatmentDeletedEvent(val treatment: Treatment) : AppEvent()
