package non_test._main_.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.client.view.ClientList
import at.cpickl.gadsu.client.view.ExtendedClient
import at.cpickl.gadsu.client.view.ThresholdCalculatorImpl
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.testinfra.SimpleTestableClock
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.MyListModel
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.joda.time.DateTime
import javax.swing.JPanel

fun main(args: Array<String>) {
    val prefs = mock<Prefs>()
    whenever(prefs.preferencesData).thenReturn(PreferencesData.DEFAULT)
    Framed.show(JPanel().apply {
        add(ClientList(MyListModel<ExtendedClient>().apply {
            resetData(listOf(
                    ExtendedClient(Client.savedValidInstance(), 0, DateTime.now(), null),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Rise", lastName = "Shine",
                            picture = MyImage.DEFAULT_PROFILE_WOMAN, category = ClientCategory.A), 1, null, 0),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Oxy", category = ClientCategory.C), 3, null, 1),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Anna", lastName = "Nym", picture = MyImage.DEFAULT_PROFILE_ALIEN), 5, null, 2),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Thomas", donation = ClientDonation.MONEY), 9, null, 3),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Rolf", donation = ClientDonation.PRESENT), 10, null, 8),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Adolf", donation = ClientDonation.UNKNOWN), 14, null, 21),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Oft", donation = ClientDonation.NONE), 16, null, 40),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Ur oft", birthday = DateTime.now().plusDays(1)), 39, null, 50),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Long Ago"), 1, null, 500),
                    ExtendedClient(
                            client = Client.savedValidInstance().copy(firstName = "With Appointment"),
                            countTreatments = 1,
                            upcomingAppointment = DateTime.now().plusDays(1),
                            differenceDaysToRecentTreatment = 390)
            ))
        }, ThresholdCalculatorImpl(prefs), SimpleTestableClock(DateTime.now())))
    })
}
