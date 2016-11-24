package at.cpickl.gadsu._main_

import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.appointment.gcal.sync.RequestImportSyncEvent
import at.cpickl.gadsu.appointment.gcal.sync.SyncReport
import at.cpickl.gadsu.appointment.gcal.sync.SyncReportSwingWindow
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.testinfra.savedValidInstance2
import at.cpickl.gadsu.view.DisabledMacHandler
import at.cpickl.gadsu.view.GadsuMenuBar
import at.cpickl.gadsu.view.SwingMainFrame
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.joda.time.DateTime

fun main(args: Array<String>) {
    val client1 = Client.savedValidInstance()
    val client2 = Client.savedValidInstance2()

    val bus = EventBus()
    val mainFrame = SwingMainFrame(bus, GadsuMenuBar(bus, DisabledMacHandler()))
    val window = SyncReportSwingWindow(mainFrame, bus)
    window.initReport(
            SyncReport(mapOf(
                    entry(gcal("g1"), client1),
                    entry(gcal("g2"), client2),
                    entry(gcal("unknown"))
            )),
            listOf(client1, client2)
    )
    window.start()

    bus.register(RequestImportSyncEventListener(window))
}

private class RequestImportSyncEventListener(private val window: SyncReportSwingWindow) {
    @Subscribe fun onRequestImportSyncEvent(event: RequestImportSyncEvent) {
        val foo = window.readSelectedEvents().map { "- " + it.event.summary }.joinToString("\n")
        println("import: $foo")
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
    return GCalEvent("gid${gidCounter++}", summary, "",
            DateTime.now(), DateTime.now().plusMinutes(30), "http//gcal.at/id")
}
