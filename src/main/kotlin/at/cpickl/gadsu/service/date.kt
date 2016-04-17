package at.cpickl.gadsu.service

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class DateFormats {
    companion object {
        val DATE: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
        val DATE_TIME: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
    }
}
fun DateTime.formatDate() = DateFormats.DATE.print(this)
fun DateTime.formatDateTime() = DateFormats.DATE_TIME.print(this)

interface Clock {
    fun now(): DateTime
}

class RealClock : Clock {
    override fun now() = DateTime.now()
}
