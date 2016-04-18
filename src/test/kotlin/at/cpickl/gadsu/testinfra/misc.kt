package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.service.clearSeconds
import org.joda.time.DateTime
import org.testng.SkipException

val TEST_UUID =  "11111111-1234-1234-1234-000000000000"
val TEST_UUID2 = "22222222-1234-1234-1234-000000000000"
val TEST_DATE = DateFormats.DATE_TIME.parseDateTime("01.01.2000 00:10:20")
val TEST_DATE_WITHOUT_SECONDS = TEST_DATE.clearSeconds()
val TEST_DATE2 = DateFormats.DATE_TIME.parseDateTime("31.12.2002 23:59:59")
val TEST_DATE2_WITHOUT_SECONDS = TEST_DATE2.clearSeconds()


fun skip(reason: String) {
    throw SkipException(reason)
}


class SimpleTestableClock(_now: DateTime? = null): Clock {

    var now = _now ?: TEST_DATE

    override fun now() = now
    override fun nowWithoutSeconds() = now.clearSeconds()
}

class SimpleTestableIdGenerator(_id: String? = null) : IdGenerator {
    var id = _id ?: TEST_UUID
    override fun generate() = id
}
