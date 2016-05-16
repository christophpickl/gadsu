package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.appointments.Appointment


@Suppress("UNUSED")
fun Appointment.Companion.unsavedValidInstance(clientId: String) = Appointment.insertPrototype(clientId, TEST_DATETIME1).copy(
        created = TEST_DATETIME1
)
