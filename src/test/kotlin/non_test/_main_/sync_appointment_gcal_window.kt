package non_test._main_

import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.appointment.gcal.sync.RequestImportSyncEvent
import at.cpickl.gadsu.appointment.gcal.sync.SyncReport
import at.cpickl.gadsu.appointment.gcal.sync.SyncReportSwingWindow
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.testinfra.savedValidInstance2
import at.cpickl.gadsu.view.DisabledMacHandler
import at.cpickl.gadsu.view.GadsuMenuBar
import at.cpickl.gadsu.view.SwingMainFrame
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.joda.time.DateTime

fun main(args: Array<String>) {
    val client1 = Client.savedValidInstance().copy(firstName = "client1", contact = Contact.EMPTY.copy(mail = "client1@home.at"))
    val client2 = Client.savedValidInstance2().copy(firstName = "client2", contact = Contact.EMPTY.copy(mail = "client2@home.at"), wantReceiveMails = false)
    val client3 = Client.savedValidInstance2().copy(firstName = "client3")
    val client4 = Client.savedValidInstance2().copy(firstName = "client4")

    val bus = EventBus()
    val mainFrame = SwingMainFrame(bus, GadsuMenuBar(bus, DisabledMacHandler()))
    val window = SyncReportSwingWindow(mainFrame, bus)
    window.initReport(
            SyncReport(
                    mapOf(
                            entry(gcal(summary = "with client 3 or 1"), client3, client1),
                            entry(gcal(summary = "with client 2"), client2),
                            entry(gcal(summary = "unknown"))), // got no suggested clients, will just pick the very first one
                    emptyList(), // no delete appointments
                    emptyMap()
            ),
            listOf(client1, client2, client3, client4)
            )
    window.start()

    bus.register(RequestImportSyncEventListener(window))
}

private class RequestImportSyncEventListener(private val window: SyncReportSwingWindow) {
    @Suppress("UNUSED_PARAMETER")
    @Subscribe fun onRequestImportSyncEvent(event: RequestImportSyncEvent) {
        val importDump = window.readImportAppointments().filter { it.enabled }.map {
            "- ${it.event.summary} => ${it.selectedClient.fullName} (confirmation mail: ${it.sendConfirmation})"
        }.joinToString("\n")
        println("import:\n$importDump")
    }
}

private fun entry(gcal: GCalEvent, vararg clients: Client): Pair<GCalEvent, List<Client>> {
    if (clients.isEmpty()) {
        return Pair(gcal, emptyList())
    }
    return Pair(gcal, clients.toList())
}

private var gidCounter = 1
private fun gcal(summary: String): GCalEvent {
    return GCalEvent("gid${gidCounter++}", "gadsuId", "clientId", summary, "",
            DateTime.now(), DateTime.now().plusMinutes(30), "http//gcal.at/id")
}
