package at.cpickl.gadsu.client

import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.testinfra.DUMMY_UUID


@Suppress("UNUSED")
fun Client.Companion.unsavedValidInstance() = Client(null, "foo", "bar", DateFormats.DATE_TIME.parseDateTime("01.12.2001 12:59:59"))
@Suppress("UNUSED")
fun Client.Companion.savedValidInstance() = unsavedValidInstance().withId(DUMMY_UUID)
