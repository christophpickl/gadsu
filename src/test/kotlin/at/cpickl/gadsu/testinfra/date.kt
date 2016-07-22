package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.service.*
import org.joda.time.DateTime


val TEST_UUID1 = "1"//"11111111-1234-1234-1234-000000000000"
val TEST_UUID2 = "2"//"22222222-1234-1234-1234-000000000000"
val TEST_DATETIME1 = "01.01.2000 00:15:20".parseDateTime()
val TEST_DATETIME_FOR_TREATMENT_DATE = TEST_DATETIME1.clearMinutes()
val TEST_DATETIME2 = "31.12.2002 23:59:58".parseDateTime()
val TEST_DATETIME2_WITHOUT_SECONDS = TEST_DATETIME2.clearSeconds()

val TEST_DATE_1985 = DateFormats.DATE.parseDateTime("10.03.1985").clearTime()


class SimpleTestableClock(_now: DateTime? = null): Clock {

    var now: DateTime = _now ?: TEST_DATETIME1

    override fun now() = now
    override fun nowWithoutSeconds() = now.clearSeconds()

}
