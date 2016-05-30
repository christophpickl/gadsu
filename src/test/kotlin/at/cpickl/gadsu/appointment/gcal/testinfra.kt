package at.cpickl.gadsu.appointment.gcal

object TestableGCalService : GCalService {
    override fun updateEvent(gCalEvent: GCalUpdateEvent) {
    }
    override fun deleteEvent(eventId: String) {
    }
    override fun createEvent(gCalEvent: GCalEvent): GCalEventMeta? {
        return null
    }
}