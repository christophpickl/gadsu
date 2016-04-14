package at.cpickl.gadsu.client

import at.cpickl.gadsu.testinfra.TEST_DATE
import at.cpickl.gadsu.testinfra.TEST_UUID


@Suppress("UNUSED")
fun Client.Companion.unsavedValidInstance() = Client(null, "testFirstName", "testLastName", TEST_DATE)

@Suppress("UNUSED")
fun Client.Companion.savedValidInstance() = unsavedValidInstance().withId(TEST_UUID)
