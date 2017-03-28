package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.appointment.gcal.sync.ImportAppointment
import at.cpickl.gadsu.appointment.gcal.sync.SyncReport
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.TEST_DATETIME1
import org.joda.time.DateTime

fun SyncReport.Companion.dummyInstance() = SyncReport(
        importEvents = mapOf(GCalEvent(
                id = null,
                gadsuId = null,
                clientId = null,
                summary = "caro",
                description = "",
                start = TEST_DATETIME1,
                end = TEST_DATETIME1.plusHours(1),
                url = null
                ) to listOf()),
        deleteAppointments = emptyList(),
        updateAppointments = emptyMap()
)

fun GCalEvent.Companion.testInstance(clientId: String, start: DateTime) = GCalEvent(
        id = "gcalTestId",
        gadsuId = null,
        clientId = clientId,
        summary = "test summary",
        description = "test description",
        start = start,
        end = start.plusHours(1),
        url = null
)

fun ImportAppointment.Companion.testInstance(client: Client, start: DateTime, sendConfirmation: Boolean = true) = ImportAppointment(
        event = GCalEvent.testInstance(client.id!!, start),
        enabled = true,
        sendConfirmation = sendConfirmation,
        selectedClient = client,
        allClients = listOf(client),
        isGmailGloballyConfigured = true
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
