package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.Event
import com.google.common.eventbus.Subscribe
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.slf4j.LoggerFactory
import java.util.LinkedList


class AnyBusListener {
    private val log = LoggerFactory.getLogger(javaClass)

    val dispatchedEvents: LinkedList<Any> = LinkedList()

    @Subscribe fun onAny(event: Any) {
        log.debug("onAny(event={})", event)
        dispatchedEvents.add(event)
    }

    fun clear() {
        dispatchedEvents.clear()
    }

    fun assertEmpty() {
        MatcherAssert.assertThat(dispatchedEvents, Matchers.empty())
    }

    fun <E : Event> assertContains(vararg expecteds: E) {
        MatcherAssert.assertThat("\nExpected: ${expecteds.joinToString()},\nActual: $dispatchedEvents",
                dispatchedEvents, Matchers.hasSize(expecteds.size))
        for (i in 0.rangeTo(expecteds.size - 1)) {
            val actual = dispatchedEvents[i]
            val expected = expecteds[i]

            // MINOR copy and pasted, as dont know how to properly check for class equalness with hamcrest
            MatcherAssert.assertThat("Type mismatch! Expected: ${expected.javaClass.name}, Actual: ${actual.javaClass.name}",
                    actual.javaClass === expected.javaClass,
                    Matchers.equalTo(true))

            MatcherAssert.assertThat("Expected to be equal! Expected: $expected, Actual: $actual",
                    actual as Event,
                    Matchers.equalTo(expected as Event))
        }
    }
}
