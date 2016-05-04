package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.toMyImage
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.service.clearMinutes
import at.cpickl.gadsu.service.clearSeconds
import at.cpickl.gadsu.service.clearTime
import at.cpickl.gadsu.service.parseDateTime
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.testng.SkipException

// https://docs.travis-ci.com/user/environment-variables/#Default-Environment-Variables
val IS_TRAVIS: Boolean = System.getProperty("user.name", "").equals("travis")
// does NOT work!!! System.getProperty("TRAVIS", "").equals("true")

val TEST_UUID1 = "1"//"11111111-1234-1234-1234-000000000000"
val TEST_UUID2 = "2"//"22222222-1234-1234-1234-000000000000"
val TEST_DATETIME1 = "01.01.2000 00:15:20".parseDateTime()
val TEST_DATETIME_FOR_TREATMENT_DATE = TEST_DATETIME1.clearMinutes()
val TEST_DATETIME2 = "31.12.2002 23:59:59".parseDateTime()
val TEST_DATETIME2_WITHOUT_SECONDS = TEST_DATETIME2.clearSeconds()

val TEST_DATE_1985 = DateFormats.DATE.parseDateTime("10.03.1985").clearTime()

fun skip(reason: String) {
    throw SkipException(reason)
}


class SimpleTestableClock(_now: DateTime? = null): Clock {

    var now = _now ?: TEST_DATETIME1

    override fun now() = now
    override fun nowWithoutSeconds() = now.clearSeconds()
}

class SimpleTestableIdGenerator(_id: String? = null) : IdGenerator {
    var id = _id ?: TEST_UUID1
    override fun generate() = id
}

class ListTestableIdGenerator(private val ids: Array<String>, private val cycleThrough: Boolean = false) : IdGenerator {
    private var currentIndex = 0

    override fun generate(): String {
        val nextId = ids[currentIndex++]
        if (cycleThrough && currentIndex == ids.size) {
            currentIndex = 0 // reset generated ID
        }
        return nextId
    }

    fun assertAllConsumed() {
        assertThat(currentIndex, equalTo(ids.size))
    }
}

class SequencedTestableIdGenerator() : IdGenerator {
    private var sequence = 1
    override fun generate() = sequence++.toString()
}

val PROFILE_PICTURE_CLASSPATH_1 = "/gadsu_test/profile_pic-valid_man1.jpg"
val PROFILE_PICTURE_CLASSPATH_2 = "/gadsu_test/profile_pic-valid_man2.jpg"

val MyImage.Companion.TEST_CLIENT_PIC1: MyImage get() = PROFILE_PICTURE_CLASSPATH_1.toMyImage()
val MyImage.Companion.TEST_CLIENT_PIC2: MyImage get() = PROFILE_PICTURE_CLASSPATH_2.toMyImage()
