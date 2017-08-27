package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.service.parseDate
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.testinfra.savedValidInstance
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

fun ExtendedClient.Companion.testInstance(client: Client = Client.savedValidInstance()) = ExtendedClient(
        client = client,
        countTreatments = 0,
        upcomingAppointment = null,
        differenceDaysToRecentTreatment = null
)

@Test
class ClientCellTest {


    private val defaultNow = "1.1.2012 00:00:00"

    private lateinit var clock: Clock

    @BeforeMethod
    fun initMocks() {
        clock = mock<Clock>()
    }

    @DataProvider
    fun provideDates(): Array<Array<out Any>> = arrayOf(
            arrayOf(defaultNow, "31.12.2011 00:00:00", "N/A"),
            arrayOf(defaultNow, "1.1.2012 12:00:00", "Heute, um 12:00 Uhr"),
            arrayOf(defaultNow, "2.1.2012 13:00:00", "Morgen, um 13:00 Uhr"),
            arrayOf(defaultNow, "3.1.2012 14:00:00", "Übermorgen, um 14:00 Uhr"),
            arrayOf(defaultNow, "4.1.2012 00:00:00", "In 3 Tage, am 4.1."),
            arrayOf(defaultNow, "11.1.2012 00:00:00", "In 10 Tage, am 11.1."),

            arrayOf("1.1.2012 23:00:00", "2.1.2012 01:00:00", "Morgen, um 01:00 Uhr"),
            arrayOf("1.1.2012 23:00:00", "1.1.2012 01:00:00", "N/A")
    )

    @Test(dataProvider = "provideDates")
    fun `upcomingAppointmentLabel`(now: String, upcoming: String, expected: String) {
        assertThat(ClientCell.upcomingAppointmentLabel(now.parseDateTime(), upcoming.parseDateTime()), equalTo(expected))
    }

    private val birthdayNow = "15.01.2000".parseDate()
    private val testAssumptionBirthdayFuture = 21
    private val testAssumptionBirthdayPast = 14

    fun `has birthday today`() {
        whenever(clock.now()).thenReturn(birthdayNow)

        assertBirthdayAround(birthdayNow, true)
    }

    fun `has birthday in future`() {
        whenever(clock.now()).thenReturn(birthdayNow)

        for (i in 1..testAssumptionBirthdayFuture) {
            assertBirthdayAround(birthdayNow.plusDays(i), true)
        }
    }

    fun `has not birthday in far future`() {
        whenever(clock.now()).thenReturn(birthdayNow)

        assertBirthdayAround(birthdayNow.plusDays(testAssumptionBirthdayFuture + 1), false)
    }

    fun `has birthday in past`() {
        whenever(clock.now()).thenReturn(birthdayNow)

        for (i in 1..testAssumptionBirthdayPast) {
            assertBirthdayAround(birthdayNow.minusDays(i), true)
        }
    }

    fun `has not birthday in past`() {
        whenever(clock.now()).thenReturn(birthdayNow)

        assertBirthdayAround(birthdayNow.minusDays(testAssumptionBirthdayPast + 1), false)
    }

    private fun assertBirthdayAround(birthday: DateTime, expected: Boolean) {
        val testee = ClientCell(xclientWithBirthday(birthday), mock(), clock)
        assertThat("Birthday: ${birthday.formatDate()}", testee.birthdayAround(), equalTo(expected))
    }

    private fun xclientWithBirthday(birthday: DateTime) =
            ExtendedClient.testInstance(client = Client.savedValidInstance().copy(birthday = birthday))

}
