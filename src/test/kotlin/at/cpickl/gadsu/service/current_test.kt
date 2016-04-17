package at.cpickl.gadsu.service

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.savedValidInstance
import at.cpickl.gadsu.testinfra.AnyBusListener
import at.cpickl.gadsu.testinfra.TEST_UUID2
import com.google.common.eventbus.EventBus
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test class CurrentClientTest {

    private val eventId = "client"
    private val client1 = Client.savedValidInstance().copy(firstName = "client1")
    private val client1withoutId = client1.copy(id = null)
    private val client1differentProps = client1.copy(lastName = "set to different")
    private val client2 = client1.copy(id = TEST_UUID2, firstName = "client2")

    private var bus: EventBus = EventBus()
    private var busListener = AnyBusListener()
    private var testee = CurrentClient(bus)

    @BeforeMethod
    fun init() {
        bus = EventBus()
        busListener = AnyBusListener()
        bus.register(busListener)
        testee = CurrentClient(bus)
    }


    // --------------------------------------------------------------------------- init set tests

    fun `Given no data set, when set to null, then no events dispatched`() {
        testee.data = null
        busListener.assertEmpty()
    }

    fun `Given no data set, when set to a client, then changed event`() {
        testee.data = client1
        busListener.assertContains(CurrentChangedEvent(eventId, null, client1))
    }

    fun `Given no data set, when set to a client without an ID, then changed event`() {
        val savedClient = client1.copy(id = null)
        testee.data = savedClient
        busListener.assertContains(CurrentChangedEvent(eventId, null, savedClient))
    }

    // --------------------------------------------------------------------------- override set tests

    fun `Given client ist set, when changing client with different id, then changed event`() {
        testee.data = client1
        busListener.clear()

        testee.data = client2
        busListener.assertContains(CurrentChangedEvent(eventId, client1, client2))
    }

    fun `Given client ist set, when set to very same, then nothing`() {
        testee.data = client1
        busListener.clear()

        testee.data = client1
        busListener.assertEmpty()
    }

    fun `Given client ist set, when set to NULL, then changed event`() {
        testee.data = client1
        busListener.clear()

        testee.data = null
        busListener.assertContains(CurrentChangedEvent(eventId, client1, null))
    }

    fun `Given client without ID is set, when changing to same client but with a set ID, then changed event dispatched`() {
        testee.data = client1withoutId
        busListener.clear()

        val changedClient = client1withoutId.copy(id = "some other id")
        testee.data = changedClient
        busListener.assertContains(CurrentChangedEvent(eventId, client1withoutId, changedClient))
    }


    // --------------------------------------------------------------------------- property changed tests

    fun `Given client is set, when changing client with same id but different properties, then changed event dispatched`() {
        testee.data = client1
        busListener.clear()

        val changedClient = client1.copy(lastName = "some new property")
        testee.data = changedClient
        busListener.assertContains(CurrentPropertiesChangedEvent(eventId, client1, changedClient))
    }

    fun `Given client with out ID, when changing properties, then properties changed event dispatched`() {
        testee.data = client1withoutId
        busListener.clear()

        val changedClient = client1withoutId.copy(lastName = "something other property")
        testee.data = changedClient
        busListener.assertContains(CurrentPropertiesChangedEvent(eventId, client1withoutId, changedClient))
    }

}