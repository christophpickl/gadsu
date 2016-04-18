package at.cpickl.gadsu.service

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class DateFormats {
    companion object {
        val TIME_MINUTES: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
        val DATE: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
        val DATE_TIME: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
    }
}

fun DateTime.formatTimeWithoutSeconds() = DateFormats.TIME_MINUTES.print(this)
fun DateTime.formatDate() = DateFormats.DATE.print(this)
fun DateTime.formatDateTime() = DateFormats.DATE_TIME.print(this)

fun DateTime.clearSeconds() = this.withSecondOfMinute(0).withMillisOfSecond(0)

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
