package non_test._main_.view

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.view.SwingAppointmentWindow
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.preferences.ThresholdPrefData
import at.cpickl.gadsu.service.InternetConnectionController
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.testinfra.savedValidInstance
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import non_test.DummyMainFrame
import non_test.Framed

fun main(args: Array<String>) {
    val internetAvailable = false
    val gcalName = "foobar"

    val internet = mock<InternetConnectionController>()
    val prefs = mock<Prefs>()
    whenever(internet.isConnected).thenReturn(internetAvailable)
    whenever(prefs.preferencesData).thenReturn(PreferencesData("", false, "", gcalName,
            null, null, null, ThresholdPrefData.DEFAULT, null, null))

    Framed.showFrameWithContext({ context ->
        val currentClient = CurrentClient(context.bus)
        currentClient.data = Client.savedValidInstance()
        val window = SwingAppointmentWindow(context.swing, context.bus, currentClient, DummyMainFrame, internet, prefs)

        window.changeCurrent(Appointment.savedValidInstance("dummyClientId").copy(
                start = "31.12.2001 14:15:00".parseDateTime(),
                end = "31.12.2001 15:45:00".parseDateTime()
        ))

        window
    })
}
