package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.appointment.gcal.sync.MatchClients
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.components.Dialogs
import javax.inject.Inject

interface SyncService {

    fun syncAndSuggest(): SyncReport

}

data class SyncReport(val eventsAndClients: Map<GCalEvent, List<Client>>)

class GCalSyncService @Inject constructor(
        private val gcal: GCalService,
        private val syncer: GCalSyncer,
        private val dialogs: Dialogs,
        private val clientService: ClientService,
        private val matcher: MatchClients
) : SyncService {
    private val log = LOG(javaClass)

    override fun syncAndSuggest(): SyncReport {
        val gCalEvents = syncer.loadGCalEvents()

        val eventsAndMaybeClients = suggestClients(gCalEvents)

        return SyncReport(eventsAndMaybeClients)
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
