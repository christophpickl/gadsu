package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.Event
import com.google.common.eventbus.Subscribe
import org.exparity.hamcrest.BeanMatchers.theSameAs
import org.hamcrest.MatcherAssert.assertThat
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
        assertThat(dispatchedEvents, Matchers.empty())
    }


    fun assertHasItems(vararg expecteds: Any) {
        assertThat(dispatchedEvents, Matchers.hasItems(*expecteds))
    }

    fun <E : Event> assertContains(vararg expecteds: E) {
        assertThat("\nExpected: ${expecteds.joinToString()},\nActual: $dispatchedEvents",
                dispatchedEvents, Matchers.hasSize(expecteds.size))

        for (i in 0.rangeTo(expecteds.size - 1)) {
            val actualRaw = dispatchedEvents[i]
            val expectedRaw = expecteds[i]

            // MINOR copy and pasted, as dont know how to properly check for class equalness with hamcrest
            assertThat("Type mismatch! Expected: ${expectedRaw.javaClass.name}, Actual: ${actualRaw.javaClass.name}",
                    actualRaw.javaClass === expectedRaw.javaClass,
                    Matchers.equalTo(true))


            val actual = actualRaw as Event
            val expected = expectedRaw as Event
            // FIXME nope, this is a generic thing here, dont do this here
            assertThat(actual, theSameAs(expected).excludeProperty("created").excludeProperty("birthday"))
        }
    }
}
