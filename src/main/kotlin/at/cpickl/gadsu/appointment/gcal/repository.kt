package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.service.LOG
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event

data class GCalEvent(
        val summary: String,
        val description: String,
        val start: org.joda.time.DateTime,
        val end: org.joda.time.DateTime
)

data class GCalUpdateEvent(
        val id: String,
        val summary: String,
        val start: org.joda.time.DateTime,
        val end: org.joda.time.DateTime
) {
    fun updateFields(gevent: Event) {
        gevent
            .setSummary(summary)
            .setStart(start.toGEventDateTime())
            .setEnd(end.toGEventDateTime())
    }
}

interface GCalRepository {
//    fun listEvents(startDate: org.joda.time.DateTime,
//                   endDate: org.joda.time.DateTime,
//                   maxResults: Int = 100
//    ): List<Event>

    fun createEvent(gCalEvent: GCalEvent): GCalEventMeta?
    fun updateEvent(gCalEvent: GCalUpdateEvent)
    fun deleteEvent(eventId: String)
}

object OfflineGCalRepository : GCalRepository {
    private val log = LOG(javaClass)

    override fun createEvent(gCalEvent: GCalEvent): GCalEventMeta? {
        log.debug("createEvent() ... no-op as offline")
        return null
    }

    override fun updateEvent(gCalEvent: GCalUpdateEvent) {
        log.debug("updateEvent() ... no-op as offline")
    }

    override fun deleteEvent(eventId: String) {
        log.debug("deleteEvent() ... no-op as offline")
    }
}

class RealGCalRepository constructor(
        private val calendar: Calendar,
        private val calendarId: String
) : GCalRepository {
    private val log = LOG(javaClass)

    // https://developers.google.com/google-apps/calendar/create-events
    override fun createEvent(gCalEvent: GCalEvent): GCalEventMeta? {
        log.info("createEvent(gCalEvent={})", gCalEvent)
        val newEvent = Event()
//                .setId(UUID.randomUUID().toString()) // possible to set custom ID (which will be the same as the AppointmentID)
                .setSummary(gCalEvent.summary)
//                .setAttendees()
//                .setReminders()
                .setDescription(gCalEvent.description)
                .setStart(gCalEvent.start.toGEventDateTime())
                .setEnd(gCalEvent.end.toGEventDateTime())
//                .setExtendedProperties(Event.ExtendedProperties().set("MYappointmentId", "fuchur"))
                .set("MYappointmentId", "fuchur")
        val savedEvent = calendar.events().insert(calendarId, newEvent).execute()
        log.info("Saved event (ID=${savedEvent.id}): ${savedEvent.htmlLink}")
        return GCalEventMeta(savedEvent.id, savedEvent.htmlLink)
    }

    // https://developers.google.com/google-apps/calendar/v3/reference/events/update#examples
    override fun updateEvent(gCalEvent: GCalUpdateEvent) {
        log.info("updateEvent(gCalEvent={})", gCalEvent)
        val eventId = gCalEvent.id
        val updateEvent = calendar.events().get(calendarId, eventId).execute()
        gCalEvent.updateFields(updateEvent)
        calendar.events().update(calendarId, eventId, updateEvent).execute()
    }

    // https://developers.google.com/google-apps/calendar/v3/reference/events/delete#examples
    override fun deleteEvent(eventId: String) {
        calendar.events().delete(calendarId, eventId).execute()
    }

}
