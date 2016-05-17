package at.cpickl.gadsu.appointments

import at.cpickl.gadsu.Event
import at.cpickl.gadsu.UserEvent

class CreateAppointmentEvent() : UserEvent()

class SaveAppointment(val appointment: Appointment) : UserEvent()

class OpenAppointmentEvent(val appointment: Appointment) : UserEvent()

// TODO register for in ListController
class AppointmentSavedEvent(val appointment: Appointment) : Event()
class AppointmentChangedEvent(val appointment: Appointment) : Event()

class AbortAppointmentDialog() : UserEvent()
