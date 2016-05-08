package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.treatment.Treatment


@Suppress("UNUSED")
fun Treatment.Companion.unsavedValidInstance(clientId: String) =
        Treatment(null, clientId, TEST_DATETIME1, 1, TEST_DATETIME_FOR_TREATMENT_DATE, minutes(60), "", "", "", "note")


fun Treatment.Companion.unsavedValidInstance(client: Client) =
        unsavedValidInstance(client.id!!)

fun Treatment.Companion.savedValidInstance(
        clientId: String,
        treatmentId: String = TEST_UUID1,
        number: Int = 1
) =
        unsavedValidInstance(clientId).copy(id = treatmentId, number = number)
