package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.Event
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.IdGenerator
import com.google.common.eventbus.Subscribe
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.testng.SkipException
import java.util.LinkedList

val TEST_UUID = "11111111-2222-3333-4444-555555555555"
val TEST_DATE = DateFormats.DATE_TIME.parseDateTime("01.12.2001 12:59:59")

fun skip(reason: String) {
    throw SkipException(reason)
}


class SimpleTestableClock(_now: DateTime? = null): Clock {
    var now = _now ?: TEST_DATE
    override fun now() = now
}

class SimpleTestableIdGenerator(_id: String? = null) : IdGenerator {
    var id = _id ?: TEST_UUID
    override fun generate() = id
}

class AnyBusListener {
    private val log = LoggerFactory.getLogger(javaClass)

    val dispatchedEvents: LinkedList<Any> = LinkedList()

    @Subscribe fun onAny(event: Any) {
        log.debug("onAny(event={})", event)
        dispatchedEvents.add(event)
    }

    fun assertEmpty() {
        assertThat(dispatchedEvents, empty())
    }

    fun assertContains(vararg expecteds: Event) {
        assertThat(dispatchedEvents, hasSize(expecteds.size))
        for (i in 0.rangeTo(expecteds.size - 1)) {
            val actual = dispatchedEvents[i]
            val expected = expecteds[i]
            // MINOR copy and pasted, as dont know how to properly check for class equalness with hamcrest
            assertThat("Type mismatch! Expected: ${expected.javaClass.name}, Actual: ${actual.javaClass.name}",
                    actual.javaClass === expected.javaClass, equalTo(true))
            assertThat(actual as Event, equalTo(expected))
        }
    }
}
