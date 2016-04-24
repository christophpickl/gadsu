package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.toMyImage
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.service.clearSeconds
import at.cpickl.gadsu.service.clearTime
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.testng.SkipException

val TEST_UUID1 = "11111111-1234-1234-1234-000000000000"
val TEST_UUID2 = "22222222-1234-1234-1234-000000000000"
val TEST_DATETIME1 = DateFormats.DATE_TIME.parseDateTime("01.01.2000 00:10:20")
val TEST_DATETIME1_WITHOUT_SECONDS = TEST_DATETIME1.clearSeconds()
val TEST_DATETIME2 = DateFormats.DATE_TIME.parseDateTime("31.12.2002 23:59:59")
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
