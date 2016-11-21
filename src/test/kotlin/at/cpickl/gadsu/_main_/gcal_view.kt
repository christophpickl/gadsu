package at.cpickl.gadsu._main_

import at.cpickl.gadsu.appointment.gcal.dummyInstance
import at.cpickl.gadsu.appointment.gcal.sync.SyncReport
import at.cpickl.gadsu.appointment.gcal.sync.SyncReportSwingWindow
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.testinfra.savedValidInstance2
import com.google.common.eventbus.EventBus

fun main(args: Array<String>) {

    val bus = EventBus()
    val frame = SyncReportSwingWindow(bus)
    frame.initReport(SyncReport.dummyInstance(), listOf(Client.savedValidInstance(), Client.savedValidInstance2()))
    frame.start()

}
