package at.cpickl.gadsu.appointment.gcal

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.EventDateTime


fun org.joda.time.DateTime.toGDateTime(): DateTime {
    return DateTime(this.millis)
}

fun org.joda.time.DateTime.toGEventDateTime(): EventDateTime {
    return EventDateTime().setDateTime(DateTime(this.millis)) // no timezone
}
