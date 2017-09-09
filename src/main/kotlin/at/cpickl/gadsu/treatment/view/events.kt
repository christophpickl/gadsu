package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.global.UserEvent

class DynTreatmentRequestAddEvent(val popupSpec: PopupSpec) : UserEvent()

class DynTreatmentRequestDeleteEvent(val tabIndex: Int) : UserEvent()

