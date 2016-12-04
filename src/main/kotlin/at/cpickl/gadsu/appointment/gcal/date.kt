package at.cpickl.gadsu.appointment.gcal

import org.joda.time.DateTime

fun org.joda.time.DateTime.toGDateTime(): com.google.api.client.util.DateTime {
    return com.google.api.client.util.DateTime(this.millis)
}

fun org.joda.time.DateTime.toGEventDateTime(): com.google.api.services.calendar.model.EventDateTime {
    return com.google.api.services.calendar.model.EventDateTime()
            .setDateTime(com.google.api.client.util.DateTime(this.millis))
            .setTimeZone("Europe/Vienna")
}

fun com.google.api.services.calendar.model.EventDateTime.toDateTime(): org.joda.time.DateTime {
    return DateTime(this.dateTime.value)
}
