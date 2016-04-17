package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.UserEvent


class CreateTreatmentEvent() : UserEvent()

/**
 * Return back to the client view.
 */
class TreatmentBackEvent() : UserEvent()

class TreatmentCreatedEvent(val treatment: Treatment) : AppEvent()

class TreatmentDeletedEvent(val treatment: Treatment) : AppEvent()
