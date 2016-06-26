package at.cpickl.gadsu.service

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.xprops.model.*
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.testinfra.TEST_UUID2
import at.cpickl.gadsu.testinfra.TestBusListener
import at.cpickl.gadsu.testinfra.savedValidInstance
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
    private var busListener = TestBusListener()
    private var testee = CurrentClient(bus)

    @BeforeMethod
    fun init() {
        bus = EventBus()
        busListener = TestBusListener()
        bus.register(busListener)
        testee = CurrentClient(bus)
    }


    // --------------------------------------------------------------------------- init set tests

    fun `Given no data set, when set to a client, then changed event`() {
        testee.data = client1
        busListener.assertContains(CurrentChangedEvent(eventId, CurrentClient.INITIAL_VALUE, client1))
    }

    fun `Given no data set, when set to a client without an ID, then changed property event because initial value has no ID set too`() {
        val savedClient = client1.copy(id = null)
        testee.data = savedClient
        busListener.assertContains(CurrentPropertiesChangedEvent(eventId, CurrentClient.INITIAL_VALUE, savedClient))
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

    fun `Given client without ID is set, when changing to same client but with a set ID, then changed event dispatched`() {
        testee.data = client1withoutId
        busListener.clear()

        val changedClient = client1withoutId.copy(id = "some other id")
        testee.data = changedClient
        busListener.assertContains(CurrentChangedEvent(eventId, client1withoutId, changedClient))
    }


    // --------------------------------------------------------------------------- property changed tests

    fun `Given client is set, when changing client with same id but different properties, then changed event dispatched`() {
        val initialClient = client1.copy(lastName = "initial")
        testee.data = initialClient
        busListener.clear()

        val changedClient = client1.copy(lastName = "changed")
        testee.data = changedClient
        //      but: CurrentPropertiesChangedEvent.OldData.FullName is "client1 some new property" instead of "client1 testLastName"
        busListener.assertContains(CurrentPropertiesChangedEvent(eventId, initialClient, changedClient))
    }

    fun `Given client with out ID, when changing properties, then properties changed event dispatched`() {
        testee.data = client1withoutId
        busListener.clear()

        val changedClient = client1withoutId.copy(lastName = "something other property")
        testee.data = changedClient
        busListener.assertContains(CurrentPropertiesChangedEvent(eventId, client1withoutId, changedClient))
    }

    fun `props change`() {
        val tiredClient = client1.copy(cprops = buildCPropsSingleEnum(XProps.Sleep, XProps.SleepOpts.TiredInMorning.opt))
        val asleepClient = client1.copy(cprops = buildCPropsSingleEnum(XProps.Sleep, XProps.SleepOpts.ProblemsFallAsleep.opt))

        testee.data = tiredClient
        busListener.clear()

        testee.data = asleepClient
        busListener.assertContains(CurrentPropertiesChangedEvent(eventId, tiredClient, asleepClient))

    }

}

fun buildCPropsSingleEnum(xprop: XPropEnum, vararg opts: XPropEnumOpt): CProps {
    return CProps(mapOf(buildCPropEnumPair(xprop, *opts)))
}

// XProp, CProp
fun buildCPropEnumPair(xprop: XPropEnum, vararg opts: XPropEnumOpt): Pair<XProp, CPropEnum> {
    return xprop to buildCPropEnum(xprop, *opts)
}

fun buildCPropEnum(xprop: XPropEnum, vararg opts: XPropEnumOpt): CPropEnum {
    return CPropEnum(xprop, listOf(*opts))
}
