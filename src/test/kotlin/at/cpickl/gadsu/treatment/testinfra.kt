package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.persistence.PersistenceErrorCode
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.testinfra.Expects
import at.cpickl.gadsu.testinfra.TEST_DATE
import at.cpickl.gadsu.testinfra.TEST_DATE_WITHOUT_SECONDS
import at.cpickl.gadsu.testinfra.TEST_UUID1


@Suppress("UNUSED")
fun Treatment.Companion.unsavedValidInstance(clientId: String) =
        Treatment(null, clientId, TEST_DATE, 1, TEST_DATE_WITHOUT_SECONDS, "note")


fun Treatment.Companion.unsavedValidInstance(client: Client) =
        unsavedValidInstance(client.id!!)

fun Treatment.Companion.savedValidInstance(clientId: String, treatmentId: String = TEST_UUID1) =
        unsavedValidInstance(clientId).copy(id = treatmentId)

@Suppress("UNUSED")
// add fields: date, ...
fun Treatment.Companion.uiInstance(note: String) = unsavedValidInstance("id_not_used").copy(note = note)


fun Expects.expectPersistenceException(errorCode: PersistenceErrorCode, executeAction: () -> Unit) {
    expect(
            type = PersistenceException::class,
            action = executeAction,
            exceptionAsserter = { e -> e.errorCode == errorCode }
    )
}
