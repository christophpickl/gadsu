package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.appointment.gcal.sync.SyncReport
import at.cpickl.gadsu.testinfra.TEST_DATETIME1
import org.joda.time.DateTime

fun SyncReport.Companion.dummyInstance() = SyncReport(
        mapOf(GCalEvent(
                id = null,
                gadsuId = null,
                clientId = null,
                summary = "caro",
                description = "",
                start = TEST_DATETIME1,
                end = TEST_DATETIME1.plusHours(1),
                url = null
                ) to listOf()),
        emptyList()
)

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
