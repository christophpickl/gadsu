package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.dyn.DynTreatment


@Suppress("UNUSED")
fun Treatment.Companion.unsavedValidInstance(clientId: String) =
        Treatment(
                id = null,
                clientId = clientId,
                created = TEST_DATETIME1,
                number = 1,
                date = TEST_DATETIME_FOR_TREATMENT_DATE,
                duration = minutes(60),
                aboutDiscomfort = "",
                aboutDiagnosis = "",
                aboutContent = "",
                aboutFeedback = "",
                aboutHomework = "",
                aboutUpcoming = "",
                note = "note",
                dynTreatments = mutableListOf<DynTreatment>())


fun Treatment.Companion.unsavedValidInstance(client: Client) =
        unsavedValidInstance(client.id!!)

fun Treatment.Companion.savedValidInstance(
        clientId: String,
        treatmentId: String = TEST_UUID1,
        number: Int = 1
) =
        unsavedValidInstance(clientId).copy(id = treatmentId, number = number)
