package at.cpickl.gadsu.service

import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.view.components.inputs.LabeledDateTime
import at.cpickl.gadsu.view.language.Languages
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

val ZERO = DateTime(0L).withHourOfDay(0)!! // get rid of +1 timezone thingy

class DateFormats {
    companion object {
        val TIME_WITHOUT_SECONDS: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
        val DATE: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
        val DATE_NO_YEAR: DateTimeFormatter = DateTimeFormat.forPattern("d.M.")
        val DATE_WITH_DAY_NO_YEAR: DateTimeFormatter = DateTimeFormat.forPattern("EEEE, d.M.").withLocale(Languages.locale)
        val DATE_LONG: DateTimeFormatter = DateTimeFormat.forPattern("EEEE, dd.MM.yyyy").withLocale(Languages.locale)
        val DATE_TIME: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
        val DATE_TIME_TALKATIVE: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM. 'um' HH:mm 'Uhr'")
        // needs I18N (check Language and create am/pm style for EN
        val DATE_TIME_SEMILONG: DateTimeFormatter = DateTimeFormat.forPattern("EE, dd.MM., HH:mm").withLocale(Languages.locale)
        val DATE_TIME_LONG: DateTimeFormatter = DateTimeFormat.forPattern("EEEE, dd.MM.yy, HH:mm 'Uhr'").withLocale(Languages.locale)

        val DATE_TIME_FILE: DateTimeFormatter = DateTimeFormat.forPattern("yyyy_MM_dd_HH_mm_ss")
    }
}

// --------------------------------------------------------------------------- time


fun DateTime.ensureNoSeconds() {
    if (this != this.clearSeconds()) {
        throw GadsuException("Illegal date: must not have seconds or milliseconds set! Was: $this")
    }
}

fun Int.isQuarterMinute(): Boolean = this == 0 || this == 15 || this == 30 || this == 45

fun DateTime.ensureQuarterMinute() {
    if (!minuteOfHour.isQuarterMinute()) {
        throw GadsuException("Illegal date: expected minute to be a quarter part but was: $this")
    }
}

/**
 * Return a list of 00:00, 00:15, ... 23:45 values.
 * Cache it, as list and DateTime is immutable :)
 */
val timesListQuarter: List<DateTime> = (0 until 24 * 4).map { ZERO.plusMinutes(it * 15) }
val timesListHalf: List<DateTime> = (0 until 24 * 2).map { ZERO.plusMinutes(it * 30) }

val timesLabeledListQuarter: List<LabeledDateTime> = timesListQuarter.map { LabeledDateTime(it) }.toList()
val timesLabeledListHalf: List<LabeledDateTime> = timesListHalf.map { LabeledDateTime(it) }.toList()

fun DateTime.equalsHoursAndMinute(that: DateTime): Boolean =
        this.hourOfDay == that.hourOfDay && this.minuteOfHour == that.minuteOfHour

// --------------------------------------------------------------------------- extension methods

/** HH:mm */
fun DateTime.formatTimeWithoutSeconds(): String = DateFormats.TIME_WITHOUT_SECONDS.print(this)
/** d.M. */
fun DateTime.formatDateNoYear(): String = DateFormats.DATE_NO_YEAR.print(this)
fun DateTime.formatDate(): String = DateFormats.DATE.print(this)
fun DateTime.formatDateWithDayNoYear(locale: Locale = Languages.locale): String = DateFormats.DATE_WITH_DAY_NO_YEAR.withLocale(locale).print(this)
fun DateTime.formatDateLong(locale: Locale = Languages.locale): String = DateFormats.DATE_LONG.withLocale(locale).print(this)
fun DateTime.formatDateTime(): String = DateFormats.DATE_TIME.print(this)
fun DateTime.formatDateTimeTalkative(): String = DateFormats.DATE_TIME_TALKATIVE.print(this)
fun DateTime.formatDateTimeSemiLong(locale: Locale = Languages.locale): String = DateFormats.DATE_TIME_SEMILONG.withLocale(locale).print(this)
fun DateTime.formatDateTimeLong(locale: Locale = Languages.locale): String = DateFormats.DATE_TIME_LONG.withLocale(locale).print(this)
fun DateTime.formatDateTimeFile(): String = DateFormats.DATE_TIME_FILE.print(this)

fun String.parseTimeWithoutSeconds(): DateTime = DateFormats.TIME_WITHOUT_SECONDS.parseDateTime(this)
/** dd.MM.yyyy */
fun String.parseDate(): DateTime {
    val parts = this.split(".")
    if (parts.size != 3) {
        throw IllegalArgumentException("Expected to have 3 parts separated by dots, but was ${parts.size} for date string '$this'!")
    }
    val year = parts[2].toInt()
    if (year < 100) { // two or one digit enterd as year
        val newYear = year + if (year < 50) 2000 else 1900
        return DateFormats.DATE.parseDateTime("${parts[0]}.${parts[1]}.${newYear}")
    }
    return DateFormats.DATE.parseDateTime(this)
}
/** dd.MM.yyyy HH:mm:ss **/
fun String.parseDateTime(): DateTime = DateFormats.DATE_TIME.parseDateTime(this)
fun String.parseDateTimeFile(): DateTime = DateFormats.DATE_TIME_FILE.parseDateTime(this)


fun DateTime.withAllButHourAndMinute(copyReference: DateTime): DateTime =
        copyReference.withHourOfDay(this.hourOfDay).withMinuteOfHour(this.minuteOfHour)


fun DateTime.clearSeconds(): DateTime = this.withSecondOfMinute(0).withMillisOfSecond(0)
fun DateTime.clearMinutes(): DateTime = this.withMinuteOfHour(0).clearSeconds()
fun DateTime.clearTime(): DateTime = this.withHourOfDay(0).clearMinutes()
/**
 * When importing events from GCal clean them.
 */
fun DateTime.toClosestQuarter(): DateTime {
    val min = if (minuteOfHour < 8) 0
    else if (minuteOfHour < 23) 15
    else if (minuteOfHour < 38) 30
    else if (minuteOfHour < 53) 45
    else 60

    if (min == 60) {
        return this.withMinuteOfHour(0).plusHours(1)
    }
    return this.withMinuteOfHour(min)

}

fun Date.toDateTime() = DateTime(this)

fun DateTime.differenceDaysWithinYear(target: DateTime): Int {
    val targetCleaned = target.withYear(this.year)
    return targetCleaned.dayOfYear - this.dayOfYear
}

fun DateTime.differenceDaysTo(to: DateTime): Int {
    return Days.daysBetween(this.toLocalDate(), to.toLocalDate()).days
}

// --------------------------------------------------------------------------- clock

interface Clock {
    fun now(): DateTime

    /**
     * Without seconds and milliseconds.
     */
    fun nowWithoutSeconds(): DateTime
}

class RealClock : Clock {
    override fun now() = DateTime.now()!!
    override fun nowWithoutSeconds() = now().clearSeconds()
}

// --------------------------------------------------------------------------- duration

// internally, duration is stored as an Int
fun minutes(minutes: Int) = Duration.standardMinutes(minutes.toLong())!!

fun Duration.toMinutes(): Int = this.standardMinutes.toInt()

/** E.g.: "45" or "1:30" */
fun Duration.formatHourMinutes(): String {
    val totalMinutes = this.toStandardMinutes().minutes
    val (hours, minutes) = splitHoursMinutes(totalMinutes)
    val minutesString = if (minutes < 10) "0$minutes" else minutes.toString()

    if (hours == 0) {
        return minutesString
    }
    return "$hours:$minutesString"
}

fun Duration.formatHourMinutesLong(): String {
    val totalMinutes = this.toStandardMinutes().minutes
    val (hours, minutes) = splitHoursMinutes(totalMinutes)

    return "$hours Stunde${if (hours == 1) "" else "n"} $minutes Minute${if (minutes == 1) "" else "n"}"
}

private fun splitHoursMinutes(totalMinutes: Int): Pair<Int, Int> {
    val hours = (totalMinutes / 60.0).toInt()
    val minutes = totalMinutes % 60
    return Pair(hours, minutes)
}
