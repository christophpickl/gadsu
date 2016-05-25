package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.service.clearSeconds


@Suppress("UNUSED")
fun Appointment.Companion.unsavedValidInstance(clientId: String) = Appointment.insertPrototype(clientId, TEST_DATETIME1.clearSeconds()).copy(
        created = TEST_DATETIME1
)

@Suppress("UNUSED")
fun Appointment.Companion.savedValidInstance(clientId: String) = Appointment.unsavedValidInstance(clientId).copy(id = TEST_UUID1)
