package at.cpickl.gadsu.service

import at.cpickl.gadsu.global.GadsuException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.joda.time.DateTime
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.*

@Test
class DateFormatsTest {

    private val date = DateTime.parse("2016-04-20T19:21:13.123")

    // ---------------------------------------------------------------------------

    //<editor-fold desc="format">

    fun `formatTimeWithoutSeconds`() {
        assertThat(date.formatTimeWithoutSeconds(), equalTo("19:21"))
    }

    fun `formatDate`() {
        assertThat(date.formatDate(), equalTo("20.04.2016"))
    }

    fun `formatDateNoYear`() {
        assertThat(date.formatDateNoYear(), equalTo("20.4."))
    }

    fun `formatDateLong`() {
        assertThat(date.formatDateLong(Locale.GERMAN), equalTo("Mittwoch, 20.04.2016"))
    }

    fun `formatDateTime`() {
        assertThat(date.formatDateTime(), equalTo("20.04.2016 19:21:13"))
    }

    fun `formatDateTimeTalkative`() {
        assertThat(date.formatDateTimeTalkative(), equalTo("20.04. um 19:21 Uhr"))
    }

    fun `formatDateTimeSemiLong`() {
        assertThat(date.formatDateTimeSemiLong(Locale.GERMAN), equalTo("Mi, 20.04., 19:21"))
    }

    fun `formatDateTimeLong`() {
        assertThat(date.formatDateTimeLong(Locale.GERMAN), equalTo("Mittwoch, 20.04.16, 19:21 Uhr"))
    }

    fun `formatDateTimeFile`() {
        assertThat(date.formatDateTimeFile(), equalTo("2016_04_20_19_21_13"))
    }

    //</editor-fold>

    // ---------------------------------------------------------------------------

    //<editor-fold desc="parse">

    fun `parseTimeWithoutSeconds`() {
        val actual = "19:21".parseTimeWithoutSeconds()
        assertThat(actual.hourOfDay, equalTo(19))
        assertThat(actual.minuteOfHour, equalTo(21))
    }

    @DataProvider
    fun provideParseDate(): Array<Array<out Any>> = arrayOf(
            arrayOf("1.1.1999", "01.01.1999"),
            arrayOf("31.12.2000", "31.12.2000"),

            arrayOf("28.8.0", "28.08.2000"),
            arrayOf("28.8.00", "28.08.2000"),
            arrayOf("28.8.49", "28.08.2049"),
            arrayOf("28.8.50", "28.08.1950"),
            arrayOf("28.8.99", "28.08.1999"),
            arrayOf("28.8.5", "28.08.2005"),
            arrayOf("28.8.05", "28.08.2005")
    )

    @Test(dataProvider = "provideParseDate")
    fun `parseDate`(given: String, expected: String) {
        assertThat(given.parseDate().formatDate(), equalTo(expected))
    }

    @DataProvider
    fun provideInvalidParseDate(): Array<Array<out Any>> = arrayOf(
            arrayOf(""),
            arrayOf("1"),
            arrayOf("1.1"),
            arrayOf("a.b.c")
    )

    @Test(dataProvider = "provideInvalidParseDate", expectedExceptions = arrayOf(IllegalArgumentException::class))
    fun `parseDateInvalid`(given: String) {
        given.parseDate()
    }

    //</editor-fold>

}

@Test
class DateTimeExtensionTest {

    private val birthday = DateTime.parse("1985-06-16T12:00:00.000")
    private val now = DateTime.parse("2015-06-15T12:00:00.000")
    private val zero = DateTime(0L)

    fun differenceDaysWithinYear() {
        assertThat(now.differenceDaysWithinYear(birthday), equalTo(1))
        assertThat(now.minusDays(1).differenceDaysWithinYear(birthday), equalTo(2))
        assertThat(now.plusDays(1).differenceDaysWithinYear(birthday), equalTo(0))
        assertThat(now.plusDays(2).differenceDaysWithinYear(birthday), equalTo(-1)) // birthday was in past
    }

    fun differenceDaysTo() {
        assertThat(now.differenceDaysTo(now), equalTo(0))
        assertThat(now.differenceDaysTo(now.plusDays(3)), equalTo(3))
        assertThat(now.differenceDaysTo(now.minusDays(3)), equalTo(-3))
        assertThat(now.differenceDaysTo(now.plusYears(1).plusDays(4)), equalTo(370))
    }

    fun `ensureNoSeconds ok`() {
        now.withSecondOfMinute(0).withMillisOfSecond(0).ensureNoSeconds()
    }

    @Test(expectedExceptions = arrayOf(GadsuException::class), expectedExceptionsMessageRegExp = ".*must not have seconds.*")
    fun `ensureNoSeconds with second fail`() {
        now.withSecondOfMinute(1).ensureNoSeconds()
    }

    @Test(expectedExceptions = arrayOf(GadsuException::class), expectedExceptionsMessageRegExp = ".*must not have seconds.*")
    fun `ensureNoSeconds with millisecond fail`() {
        now.withMillisOfSecond(1).ensureNoSeconds()
    }

    fun `ensureQuarterMinute ok`() {
        now.withMinuteOfHour(0)
        now.withMinuteOfHour(15)
        now.withMinuteOfHour(30)
        now.withMinuteOfHour(45)
    }

    @Test(expectedExceptions = arrayOf(GadsuException::class), expectedExceptionsMessageRegExp = ".*expected minute to be a quarter part.*")
    fun `ensureQuarterMinute fail`() {
        now.withMinuteOfHour(12).ensureQuarterMinute()
    }

    fun `timesListQuarter ok`() {
        val actual = timesListQuarter
        assertThat(actual, hasSize(96))
        assertThat(actual, hasItem(zero))
        assertThat(actual, hasItem(zero.plusMinutes(15)))
        assertThat(actual, hasItem(zero.plusHours(22).plusMinutes(45)))
    }

    fun `timesLabeledListQuarter ok`() {
        val actual = timesLabeledListQuarter
        assertThat(actual, hasSize(96))
        assertThat(actual[0].label, equalTo("00:00"))
        assertThat(actual[95].label, equalTo("23:45"))
    }

    fun `timesListHalf ok`() {
        val actual = timesListHalf
        assertThat(actual, hasSize(48))
        assertThat(actual, hasItem(zero))
        assertThat(actual, hasItem(zero.plusMinutes(30)))
        assertThat(actual, hasItem(zero.plusHours(22).plusMinutes(30)))
    }

    fun `timesLabeledListHalf ok`() {
        val actual = timesLabeledListHalf
        assertThat(actual, hasSize(48))
        assertThat(actual[0].label, equalTo("00:00"))
        assertThat(actual[47].label, equalTo("23:30"))
    }

    fun `equalsHoursAndMinute ok`() {
        assertThat(now.equalsHoursAndMinute(now.plusYears(1).plusMonths(1).plusDays(1).plusSeconds(1).plusMillis(1)), equalTo(true))
        assertThat(now.equalsHoursAndMinute(now.plusHours(1)), equalTo(false))
        assertThat(now.equalsHoursAndMinute(now.plusMinutes(1)), equalTo(false))
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

    @DataProvider
    fun provideDurationLongFormats(): Array<Array<out Any>> = arrayOf(
            arrayOf(0, "0 Stunden 0 Minuten"),
            arrayOf(1, "0 Stunden 1 Minute"),
            arrayOf(10, "0 Stunden 10 Minuten"),
            arrayOf(59, "0 Stunden 59 Minuten"),
            arrayOf(60, "1 Stunde 0 Minuten"),
            arrayOf(61, "1 Stunde 1 Minute"),
            arrayOf(70, "1 Stunde 10 Minuten"),
            arrayOf(119, "1 Stunde 59 Minuten"),
            arrayOf(120, "2 Stunden 0 Minuten"),
            arrayOf(600, "10 Stunden 0 Minuten")
    )

    @Test(dataProvider = "provideDurationLongFormats")
    fun `formatHourMinutesLong`(minutes: Int, expectedFormat: String) {
        assertThat(minutes(minutes).formatHourMinutesLong(), equalTo(expectedFormat))
    }

    @DataProvider
    fun provideQuarterly(): Array<Array<out Any>> = arrayOf(
            arrayOf("12:00", "12:00", 0),
            arrayOf("12:01", "12:00", 0),
            arrayOf("12:07", "12:00", 0),
            arrayOf("12:08", "12:15", 0),
            arrayOf("12:22", "12:15", 0),
            arrayOf("12:23", "12:30", 0),
            arrayOf("12:37", "12:30", 0),
            arrayOf("12:38", "12:45", 0),
            arrayOf("12:52", "12:45", 0),
            arrayOf("12:53", "13:00", 0),
            arrayOf("12:59", "13:00", 0),
            arrayOf("23:59", "00:00", 1)
    )

    @Test(dataProvider = "provideQuarterly")
    fun `toClosestQuarter`(timeRaw: String, timeExpected: String, dayMondifier: Int) {
        assertThat(timeRaw.parseTimeWithoutSeconds().toClosestQuarter(),
                equalTo(timeExpected.parseTimeWithoutSeconds().plusDays(dayMondifier)))
    }

}
