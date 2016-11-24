package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.service.LOG
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import org.joda.time.DateTime

private val XPROP_GADSU_ID = "GADSU_ID"

data class GCalEvent(
        val id: String?,
        val gadsuId: String?,
        val summary: String,
        val description: String,
        val start: org.joda.time.DateTime,
        val end: org.joda.time.DateTime,
        val url: String?
)

data class GCalUpdateEvent(
        val id: String,
        val gadsuId: String,
        val summary: String,
        val start: org.joda.time.DateTime,
        val end: org.joda.time.DateTime
) {
    fun updateFields(gevent: Event) {
        gevent
                .setSummary(summary)
                .setStart(start.toGEventDateTime())
                .setEnd(end.toGEventDateTime())
                .setExtendedProperties(Event.ExtendedProperties().apply { put(XPROP_GADSU_ID, gadsuId) })
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

    // https://developers.google.com/google-apps/calendar/v3/reference/events/list
    override fun listEvents(start: DateTime, end: DateTime): List<GCalEvent> {
        val events = calendar.events().list(calendarId).apply {
//            fields = "items(iCalUID,start,end,description,htmlLink,extendedProperties),summary"
            timeMin = start.toGDateTime()
            timeMax = end.toGDateTime()
        }.execute()

        log.debug("listEvents() returning ${events.items.size} events")
        return events.items.map { it.toGCalEvent() }
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
        log.info("deleteEvent(eventId={})", eventId)
        calendar.events().delete(calendarId, eventId).execute()
    }

    private fun Event.toGCalEvent(): GCalEvent {
        val maybeGadsuId = getPrivateExtendedProperty(XPROP_GADSU_ID)

        return GCalEvent(
                id = this.iCalUID, // TODO or just id?
                gadsuId = maybeGadsuId,
                summary = this.summary ?: "",
                description = this.description ?: "",
                start = this.start.toDateTime(),
                end = this.end.toDateTime(),
                url = this.htmlLink
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
                .apply {

                    val extendedProps = mutableMapOf<String, String>()
                    if (gadsuId != null) {
                        extendedProps.put(XPROP_GADSU_ID, gadsuId)
                    }
                    setPrivateExtendedProperties(extendedProps)
                }
    }

    private fun Event.setPrivateExtendedProperties(properties: Map<String, String>) {
        extendedProperties = Event.ExtendedProperties().apply {
            private = properties
        }
    }

    private fun Event.getPrivateExtendedProperty(key: String): String? {
        return if (extendedProperties == null || extendedProperties.private == null) {
            null
        } else {
            val storedValue = extendedProperties.private[key]
            if (storedValue == null) {
                null
            } else {
                storedValue
            }
        }
    }


}

