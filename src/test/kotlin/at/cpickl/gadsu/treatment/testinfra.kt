package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.testinfra.TEST_DATE
import at.cpickl.gadsu.testinfra.TEST_DATE_WITHOUT_SECONDS
import at.cpickl.gadsu.testinfra.TEST_UUID


@Suppress("UNUSED")
fun Treatment.Companion.unsavedValidInstance(clientId: String) = Treatment(null, clientId, TEST_DATE, 1, TEST_DATE_WITHOUT_SECONDS, "note")

@Suppress("UNUSED")
fun Treatment.Companion.savedValidInstance(clientId: String) = unsavedValidInstance(clientId).copy(id = TEST_UUID)
