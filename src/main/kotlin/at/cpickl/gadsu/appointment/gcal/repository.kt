package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.service.LOG
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import org.joda.time.DateTime

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

    val isOnline: Boolean

    fun listEvents(start: DateTime, end: DateTime): List<GCalEvent>

    /**
     * @return the GCal event ID if was saved, null otherwise (offline or GCal not enabled)
     */
    fun createEvent(gCalEvent: GCalEvent): GCalEventMeta?

    fun updateEvent(gCalEvent: GCalUpdateEvent)

    fun deleteEvent(eventId: String)

}

object OfflineGCalRepository : GCalRepository {
    private val log = LOG(javaClass)

    override val isOnline = false

    override fun listEvents(start: DateTime, end: DateTime): List<GCalEvent> {
        log.debug("listEvents() ... no-op as offline")
        return emptyList()
    }

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

    override val isOnline = true

    // https://developers.google.com/google-apps/calendar/create-events
    override fun createEvent(gCalEvent: GCalEvent): GCalEventMeta? {
        log.info("createEvent(gCalEvent={})", gCalEvent)
        val newEvent = gCalEvent.toEvent()
        // MINOR is it possible to store custom metadata in here??
        // .set("MYappointmentId", "fuchur")

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

    override fun listEvents(start: DateTime, end: DateTime): List<GCalEvent> {
        val events = calendar.events().list(calendarId).apply {
            timeMin = start.toGDateTime()
            timeMax = end.toGDateTime()
        }.execute()

        log.trace("listEvents() returning ${events.items.size} events")
        return events.items.map { it.toGCalEvent() }
    }

    private fun Event.toGCalEvent(): GCalEvent {
        return GCalEvent(
                summary = this.summary ?: "",
                description = this.description ?: "",
                start = this.start.toDateTime(),
                end = this.end.toDateTime()
        )
    }

    private fun GCalEvent.toEvent(): Event {
        return Event()
//                .setId(UUID.randomUUID().toString()) // possible to set custom ID (which will be the same as the AppointmentID)
                .setSummary(this.summary)
                .setDescription(this.description)
                .setStart(this.start.toGEventDateTime())
                .setEnd(this.end.toGEventDateTime())
//                .setAttendees()
//                .setReminders()
//                .setExtendedProperties(Event.ExtendedProperties().set("MYappointmentId", "fuchur"))
    }

}

