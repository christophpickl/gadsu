package at.cpickl.gadsu._main_

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.view.SwingAppointmentWindow
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.view.components.DummyMainFrame
import at.cpickl.gadsu.view.components.Framed

fun main(args: Array<String>) {
    Framed.showFrameWithContext({ context ->
        val currentClient = CurrentClient(context.bus)
        currentClient.data = Client.savedValidInstance()
        val window = SwingAppointmentWindow(context.swing, context.bus, currentClient, DummyMainFrame)

        window.changeCurrent(Appointment.savedValidInstance("dummyClientId").copy(
                start = "31.12.2001 14:15:00".parseDateTime(),
                end = "31.12.2001 14:30:00".parseDateTime()
        ))

        window
    })
}
