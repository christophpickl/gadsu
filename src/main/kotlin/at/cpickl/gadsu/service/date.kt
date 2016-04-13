package at.cpickl.gadsu.service

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class DateFormats {
    companion object {
        val DATE: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
    }
}

interface Clock {
    // TODO myshiatsu got some nice extension methods for formatting joda date time ;)
    fun now(): DateTime
}

class RealClock : Clock {
    override fun now() = DateTime.now()
}
