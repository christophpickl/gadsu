package at.cpickl.gadsu.appointment.gcal

import org.joda.time.DateTime

object TestableGCalService : GCalService {

    override val isOnline = true

    override fun listEvents(start: DateTime, end: DateTime): List<GCalEvent> {
        return emptyList()
    }

    override fun updateEvent(gCalEvent: GCalUpdateEvent) {
    }
    override fun deleteEvent(eventId: String) {
    }
    override fun createEvent(gCalEvent: GCalEvent): GCalEventMeta? {
        return null
    }
}
