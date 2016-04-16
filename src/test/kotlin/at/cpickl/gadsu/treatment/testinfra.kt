package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.testinfra.TEST_DATE
import at.cpickl.gadsu.testinfra.TEST_UUID


@Suppress("UNUSED")
fun Treatment.Companion.unsavedValidInstance() = Treatment(null, 1, TEST_DATE, TEST_DATE)

@Suppress("UNUSED")
fun Treatment.Companion.savedValidInstance() = unsavedValidInstance().copy(id = TEST_UUID)
