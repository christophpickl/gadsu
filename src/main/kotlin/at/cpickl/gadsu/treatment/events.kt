package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.AppEvent

class TreatmentCreatedEvent(val treatment: Treatment) : AppEvent()

class TreatmentDeletedEvent(val treatment: Treatment) : AppEvent()
