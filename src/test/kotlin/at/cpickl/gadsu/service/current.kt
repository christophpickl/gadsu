package at.cpickl.gadsu.service

import at.cpickl.gadsu.client.Client
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.slf4j.LoggerFactory
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.LinkedList


@Test class CurrentTest {

    private var initialClient: Client = CurrentClient.INITIAL_DATA
    private val client1 = initialClient.copy(id = "testClient1")

    private var bus: EventBus = EventBus()
    private var busListener: CountingCurrentChangedEventListener = CountingCurrentChangedEventListener()

    @BeforeMethod
    fun init() {
        bus = EventBus()
        busListener = CountingCurrentChangedEventListener()
        bus.register(busListener)
    }

    fun setData__changeToDifferentId_shouldDispatchEvent() {
        testee().data = client1
        busListener.assertContains(CurrentChangedEvent("client", initialClient, client1))
    }

    fun setData__changeToSame_shouldDispatchNothing() {
        testee().data = initialClient
        busListener.assertEmpty()
    }

    fun setData__changeToDifferentName_shouldDispatchNothingAsOnlyChecksForId() {
        testee().data = initialClient.copy(firstName = "something")
        busListener.assertEmpty()
    }

    private fun testee(): CurrentClient {
        return CurrentClient(bus)
    }

}

class CountingCurrentChangedEventListener {
    private val log = LoggerFactory.getLogger(javaClass)

    val dispatchedEvents: LinkedList<CurrentChangedEvent> = LinkedList()

    @Subscribe fun onCurrentChanged(event: CurrentChangedEvent) {
        log.debug("onCurrentChanged(event={})", event)
        dispatchedEvents.add(event)
    }

    fun assertEmpty() {
        assertThat(dispatchedEvents, Matchers.empty())
    }

    fun assertContains(vararg expectedEvents: CurrentChangedEvent) {
        assertThat(dispatchedEvents, Matchers.contains(*expectedEvents))
    }

}
