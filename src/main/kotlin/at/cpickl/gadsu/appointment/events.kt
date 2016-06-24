package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.Event
import at.cpickl.gadsu.UserEvent
import com.google.common.base.MoreObjects

class CreateAppointmentEvent() : UserEvent()

class SaveAppointment(val appointment: Appointment) : UserEvent() {
    override fun toString() = MoreObjects.toStringHelper(this).add("appointment", appointment).toString()
}

class OpenAppointmentEvent(val appointment: Appointment) : UserEvent() {
    override fun toString() = MoreObjects.toStringHelper(this).add("appointment", appointment).toString()
}

class DeleteAppointmentEvent(val appointment: Appointment) : UserEvent() {
    override fun toString() = MoreObjects.toStringHelper(this).add("appointment", appointment).toString()
}
class AppointmentDeletedEvent(val appointment: Appointment) : UserEvent() {
    override fun toString() = MoreObjects.toStringHelper(this).add("appointment", appointment).toString()
}


// TODO register for in ListController
class AppointmentSavedEvent(val appointment: Appointment) : Event() {
    override fun toString() = MoreObjects.toStringHelper(this).add("appointment", appointment).toString()
}
class AppointmentChangedEvent(val appointment: Appointment) : Event() {
    override fun toString() = MoreObjects.toStringHelper(this).add("appointment", appointment).toString()
}

class AbortAppointmentDialogEvent() : UserEvent()
