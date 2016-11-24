package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.clearTime
import org.joda.time.DateTime
import javax.inject.Inject

interface SyncService {

    fun syncAndSuggest(): SyncReport

    // TODO use a different data model, as ImportAppointment got weird clients stuff in there for UI rendering only!
    fun import(appointmentsToImport: List<ImportAppointment>)

}

data class SyncReport(val eventsAndClients: Map<GCalEvent, List<Client>>) {
    companion object {} // for extensions only
}

class GCalSyncService @Inject constructor(
        private val syncer: GCalSyncer,
        private val clientService: ClientService,
        private val matcher: MatchClients,
        private val appointmentService: AppointmentService,
        private val clock: Clock
        ) : SyncService {

    private val log = LOG(javaClass)

    override fun syncAndSuggest(): SyncReport {
        log.debug("syncAndSuggest()")
        val gCalEvents = syncer.loadGCalEventsForImport()

        val eventsAndMaybeClients = suggestClients(gCalEvents)

        return SyncReport(eventsAndMaybeClients)
    }

    override fun import(appointmentsToImport: List<ImportAppointment>) {
        log.debug("import(appointmentsToImport.size={})", appointmentsToImport.size)

        val now = clock.now()
        appointmentsToImport.forEach {
            // no check for duplicates, you have to delete them manually ;)
            appointmentService.insertOrUpdate(it.toAppointment(now))
        }
    }

    private fun suggestClients(gCalEvents: List<GCalEvent>): Map<GCalEvent, List<Client>> {
        val allClients = clientService.findAll(ClientState.ACTIVE)

        return gCalEvents.associate {
            val mightBeName = it.summary
            val foundClients = matcher.findMatchingClients(mightBeName, allClients)
            log.trace("for name '$mightBeName' found ${foundClients.size} clients: ${foundClients.map { it.fullName }.joinToString(", ")}")

            Pair(it, foundClients)
        }
    }
}

interface GCalSyncer {
    fun loadGCalEventsForImport(): List<GCalEvent>
}

class GCalSyncerImpl @Inject constructor(
        private val gcal: GCalService,
        private val clock: Clock

) : GCalSyncer {

    companion object {
        private val log = LOG(javaClass)
        private val DAYS_BEFORE_AND_AFTER_TO_SCAN = 14
    }

    override fun loadGCalEventsForImport(): List<GCalEvent> {
        if (!gcal.isOnline) {
            throw IllegalStateException("can not sync: gcal is not online!")
        }

        val (start, end) = dateRangeForSyncer()
        val gcalEvents = gcal.listEvents(start, end)

        log.trace("gcal listed ${gcalEvents.size} events in total.")
        return gcalEvents.filter { it.gadsuId == null }
    }

    private fun dateRangeForSyncer(): Pair<DateTime, DateTime> {
        val now = clock.now().clearTime()
        return Pair(
                now.minusDays(DAYS_BEFORE_AND_AFTER_TO_SCAN),
                now.plusDays(DAYS_BEFORE_AND_AFTER_TO_SCAN)
        )
    }

}
