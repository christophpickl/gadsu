package at.cpickl.gadsu.service

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test
class DateFormatsTest {

    private val date = DateTime.parse("2016-04-20T19:21:13.123")

    fun `formatDateTimeLong`() {
        assertThat(date.formatDateTimeLong(), equalTo("Mittwoch, 20.04.16, 19:21 Uhr"))
    }

}

@Test
class DateTimeExtensionTest {

    private val birthday = DateTime.parse("1985-06-16T12:00:00.000")
    private val now = DateTime.parse("2015-06-15T12:00:00.000")

    fun differenceDaysWithinYear() {
        assertThat(now.differenceDaysWithinYear(birthday), equalTo(1))
        assertThat(now.minusDays(1).differenceDaysWithinYear(birthday), equalTo(2))
        assertThat(now.plusDays(1).differenceDaysWithinYear(birthday), equalTo(0))
        assertThat(now.plusDays(2).differenceDaysWithinYear(birthday), equalTo(-1)) // birthday was in past
    }
}

@Test class DurationTest {

    @DataProvider
    fun provideDurationFormats(): Array<Array<out Any>> = arrayOf(
            arrayOf(0, "00"),
            arrayOf(1, "01"),
            arrayOf(10, "10"),
            arrayOf(59, "59"),
            arrayOf(60, "1:00"),
            arrayOf(61, "1:01"),
            arrayOf(70, "1:10"),
            arrayOf(119, "1:59"),
            arrayOf(120, "2:00"),
            arrayOf(600, "10:00")
    )

    @Test(dataProvider = "provideDurationFormats")
    fun `formatHourMinutes`(minutes: Int, expectedFormat: String) {
        assertThat(minutes(minutes).formatHourMinutes(), equalTo(expectedFormat))
    }

}
