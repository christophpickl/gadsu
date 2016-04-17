package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.testinfra.TEST_DATE
import at.cpickl.gadsu.testinfra.TEST_UUID


@Suppress("UNUSED")
fun Treatment.Companion.unsavedValidInstance(clientId: String) = Treatment(null, TEST_DATE, clientId, 1, TEST_DATE, "note")

@Suppress("UNUSED")
fun Treatment.Companion.savedValidInstance(clientId: String) = unsavedValidInstance(clientId).copy(id = TEST_UUID)
