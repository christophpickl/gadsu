package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.view.components.inputs.LabeledDateTime
import at.cpickl.gadsu.view.language.Languages
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*


class DateFormats {
    companion object {
        val TIME_WITHOUT_SECONDS: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
        val DATE: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
        val DATE_TIME: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
        val DATE_TIME_TALKATIVE: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM. 'um' HH:mm 'Uhr'")
        // needs I18N (check Language and create am/pm style for EN
        val DATE_TIME_SEMILONG: DateTimeFormatter = DateTimeFormat.forPattern("EE, dd.MM., HH:mm").withLocale(Languages.locale)
        val DATE_TIME_LONG: DateTimeFormatter = DateTimeFormat.forPattern("EEEE, dd.MM.yy, HH:mm 'Uhr'").withLocale(Languages.locale)

        val DATE_TIME_FILE: DateTimeFormatter = DateTimeFormat.forPattern("yyyy_MM_dd_HH_mm_ss")
    }
}

// --------------------------------------------------------------------------- time

// TODO dont do this! stop!
fun Int.isQuarterMinute(): Boolean = this == 0 || this == 15 || this == 30 || this == 45

fun DateTime.ensureNoSeconds() {
    if (!this.equals(this.clearSeconds())) {
        throw GadsuException("Illegal date: must not have seconds or milliseconds set! Was: $this")
    }
}

fun DateTime.ensureQuarterMinute() {
    if (!minuteOfHour.isQuarterMinute()) {
        throw GadsuException("Illegal date: expected minute to be a quarter part but was: $this")
    }
}


//    println(timesList().map { it.formatTimeWithoutSeconds() }.joinToString("\n"))
fun timesList(): List<DateTime> {
    var current = "00:00".parseTimeWithoutSeconds()
    return 0.rangeTo(24 * 4 - 1).map {
        val result = current
        current = current.plusMinutes(15)
        result
    }
}

fun timesLabeledList(): List<LabeledDateTime> {
    return timesList().map { LabeledDateTime(it) }.toList()
}

fun DateTime.equalsHoursAndMinute(that: DateTime): Boolean {
    return this.hourOfDay == that.hourOfDay &&
           this.minuteOfHour == that.minuteOfHour

}

// --------------------------------------------------------------------------- extension methods

fun DateTime.formatTimeWithoutSeconds(): String = DateFormats.TIME_WITHOUT_SECONDS.print(this)
fun DateTime.formatDate(): String = DateFormats.DATE.print(this)
fun DateTime.formatDateTime(): String = DateFormats.DATE_TIME.print(this)
fun DateTime.formatDateTimeTalkative(): String = DateFormats.DATE_TIME_TALKATIVE.print(this)
fun DateTime.formatDateTimeSemiLong(): String = DateFormats.DATE_TIME_SEMILONG.print(this)
fun DateTime.formatDateTimeLong(): String = DateFormats.DATE_TIME_LONG.print(this)
fun DateTime.formatDateTimeFile(): String = DateFormats.DATE_TIME_FILE.print(this)

fun String.parseTimeWithoutSeconds(): DateTime = DateFormats.TIME_WITHOUT_SECONDS.parseDateTime(this)
fun String.parseDate(): DateTime = DateFormats.DATE.parseDateTime(this)
fun String.parseDateTime(): DateTime = DateFormats.DATE_TIME.parseDateTime(this)
fun String.parseDateTimeFile(): DateTime = DateFormats.DATE_TIME_FILE.parseDateTime(this)


fun DateTime.withAllButHourAndMinute(copyReference: DateTime): DateTime =
        copyReference.withHourOfDay(this.hourOfDay).withMinuteOfHour(this.minuteOfHour)



fun DateTime.clearSeconds(): DateTime = this.withSecondOfMinute(0).withMillisOfSecond(0)
fun DateTime.clearMinutes(): DateTime = this.withMinuteOfHour(0).clearSeconds()
fun DateTime.clearTime(): DateTime = this.withHourOfDay(0).clearMinutes()

fun Date.toDateTime() = DateTime(this)


// --------------------------------------------------------------------------- clock

interface Clock {
    fun now(): DateTime

    /**
     * Without seconds and milliseconds.
     */
    fun nowWithoutSeconds(): DateTime
}

class RealClock : Clock {

    override fun now() = DateTime.now()

    override fun nowWithoutSeconds() = now().clearSeconds()

}

// --------------------------------------------------------------------------- duration

// internally, duration is stored as an Int
fun minutes(minutes: Int) = Duration.standardMinutes(minutes.toLong())
fun Duration.toMinutes(): Int = this.standardMinutes.toInt()

/** E.g.: "45" or "1:30" */
fun Duration.formatHourMinutes(): String {
    val totalMinutes = this.toStandardMinutes().minutes

    val hours = (totalMinutes / 60.0).toInt()
    val minutes = totalMinutes % 60

    val minutesString = if (minutes < 10) "0$minutes" else minutes.toString()

    if (hours == 0) {
        return minutesString
    }
    return "$hours:$minutesString"
}
