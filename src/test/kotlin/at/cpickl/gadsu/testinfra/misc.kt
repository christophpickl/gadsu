package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.service.DateFormats
import org.testng.SkipException

val TEST_UUID = "11111111-2222-3333-4444-555555555555"
val TEST_DATE = DateFormats.DATE_TIME.parseDateTime("01.12.2001 12:59:59")

fun skip(reason: String) {
    throw SkipException(reason)
}
