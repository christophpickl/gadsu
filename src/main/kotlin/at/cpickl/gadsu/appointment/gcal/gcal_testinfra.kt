package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.service.clearTime
import org.joda.time.DateTime

fun GCalEvent.Companion.blankDummy(): GCalEvent {
    val now = DateTime.now().clearTime()
    return GCalEvent(
            id = null,
            gadsuId = null,
            clientId = null,
            summary = "testSummary",
            description = "testDescription",
            start = now,
            end = now.plusMinutes(60),
            url = null
    )
}

fun RealGCalRepository.getEvent(eventId: String) =
        calendar.events().get(calendarId, eventId).execute()!!

fun GCalEvent.Companion.withoutIdAndUrl(): GCalEvent {
    val now = DateTime.now().clearTime()
    return GCalEvent(
            id = null,
            gadsuId = "testGadsuId",
            clientId = "testClientId",
            summary = "testSummary",
            description = "testDescription",
            start = now,
            end = now.plusMinutes(60),
            url = null
    )
}
