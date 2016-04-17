package at.cpickl.gadsu.service

import at.cpickl.gadsu.client.Client
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
import org.slf4j.LoggerFactory
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.LinkedList


@Test class CurrentTest {

    private var initialClient: Client = Client.INSERT_PROTOTYPE
    private val client1 = initialClient.copy(id = "testClient1")

    private var bus: EventBus = EventBus()
    private var testListener: CountingCurrentChangedEventListener = CountingCurrentChangedEventListener()

    @BeforeMethod
    fun init() {
        initialClient = Client.INSERT_PROTOTYPE
        bus = EventBus()
        testListener = CountingCurrentChangedEventListener()
        bus.register(testListener)
    }

    fun setData__changeToDifferentId_shouldDispatchEvent() {
        testee().data = client1
        testListener.assertContains(CurrentChangedEvent("client", initialClient, client1))
    }

    fun setData__changeToSame_shouldDispatchNothing() {
        testee().data = initialClient
        testListener.assertEmpty()
    }

    fun setData__changeToDifferentName_shouldDispatchNothingAsOnlyChecksForId() {
        testee().data = initialClient.copy(firstName = "something")
        testListener.assertEmpty()
    }

    private fun testee(): CurrentClient {
        return CurrentClient(initialClient, bus)
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
        assertThat(dispatchedEvents, empty())
    }

    fun assertContains(vararg expectedEvents: CurrentChangedEvent) {
        assertThat(dispatchedEvents, contains(*expectedEvents))
    }

}