package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.service.parseDateTime
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test class ClientCellTest {


    private val defaultNow = "1.1.2012 00:00:00"

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

}
