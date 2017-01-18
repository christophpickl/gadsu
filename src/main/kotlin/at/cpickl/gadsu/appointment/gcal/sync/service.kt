package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.mail.Mail
import at.cpickl.gadsu.mail.MailSender
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.clearTime
import at.cpickl.gadsu.service.formatDateWithDayNoYear
import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import org.joda.time.DateTime
import javax.inject.Inject

interface SyncService {

    fun syncAndSuggest(): SyncReport
    fun import(appointmentsToImport: List<Appointment>)

}

data class SyncReport(
        val importEvents: Map<GCalEvent, List<Client>>, // with suggested clients
        val deleteAppointments: List<Appointment>,
        val updateAppointments: Map<GCalEvent, Appointment>
) {
    companion object {} // for extensions only

    fun isEmpty() =
            importEvents.isEmpty() && deleteAppointments.isEmpty() && updateAppointments.isEmpty()
}

class GCalSyncService @Inject constructor(
        private val syncer: GCalSyncer,
        private val clientService: ClientService,
        private val matcher: MatchClients,
        private val appointmentService: AppointmentService,
        private val clock: Clock,
        private val mailSender: MailSender
) : SyncService {

    companion object {
        private val DAYS_BEFORE_TO_SCAN = 14
        private val DAYS_AFTER_TO_SCAN = 40
    }

    private val log = LOG(javaClass)

    private fun Appointment.equalTo(event: GCalEvent) = event.equalTo(this)
    private fun GCalEvent.equalTo(appointment: Appointment): Boolean {
        return this.start == appointment.start &&
                this.end == appointment.end &&
                // do not sync summary (yet), as it is inferred by client id / full name
                this.description == appointment.note
    }

    override fun syncAndSuggest(): SyncReport {
        log.debug("syncAndSuggest()")

        val range = dateRangeForSyncer()

        val gCalEvents = syncer.loadEvents(range)

        val gadsuIdsByGCal = gCalEvents.filter { it.gadsuId != null }.map { it.gadsuId!! }
        val appointmentsInRange = appointmentService.findAllBetween(range)

        val deleteEvents = appointmentsInRange.filter { it.gcalId != null && !gadsuIdsByGCal.contains(it.id!!) }

        val existingEventsByGadsuId = gCalEvents.filter { it.gadsuId != null }.associateBy { it.gadsuId }
        val appointmentsExistingAtGcal = appointmentsInRange.filter { it.gcalId != null && gadsuIdsByGCal.contains(it.id!!) }
        val appointmentsToUpdate = appointmentsExistingAtGcal.filter {
            !existingEventsByGadsuId[it.id]!!.equalTo(it)
        }.associateBy { existingEventsByGadsuId[it.id]!! }

        // could store update field in local appointment ID and check with gcal update timestamp... but... nah ;)

        val toBeImportedEvents = withSuggestedClients(gCalEvents.filter { it.gadsuId == null })

        return SyncReport(
                toBeImportedEvents,
                deleteEvents,
                appointmentsToUpdate
        )
    }

    override fun import(appointmentsToImport: List<Appointment>) {
        log.debug("import(appointmentsToImport.size={})", appointmentsToImport.size)

        appointmentsToImport.forEach { appointment ->
            // no check for duplicates, you have to delete them manually ;)
            appointmentService.insertOrUpdate(appointment)

            sendConfirmationMail(clientService.findById(appointment.clientId), appointment)
        }
    }

    private fun sendConfirmationMail(client: Client, appointment: Appointment) {
        // verify ...
//        client.contact.mail.isNotEmpty()
//        client.firstName.isNotEmpty()

        val date = appointment.start // should we also display the end/length?
        // FIXME #87 configurable mail confirmation text (subject + body), using own simple template mechanism (reference name and date variables)
        mailSender.send(Mail(client.contact.mail, "Shiatsu Terminbestaetigung",
                """halli hallo ${client.firstName},

hiermit bestaetige ich dir den termin am ${date.formatDateWithDayNoYear()} um ${date.formatTimeWithoutSeconds()} uhr.

auf bald und alles liebe,
christoph
"""))
    }

    private fun dateRangeForSyncer(): Pair<DateTime, DateTime> {
        val now = clock.now().clearTime()
        return Pair(
                now.minusDays(DAYS_BEFORE_TO_SCAN),
                now.plusDays(DAYS_AFTER_TO_SCAN)
        )
    }

    private fun withSuggestedClients(gCalEvents: List<GCalEvent>): Map<GCalEvent, List<Client>> {
        if (gCalEvents.isEmpty()) {
            return emptyMap()
        }

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
    fun loadEvents(range: Pair<DateTime, DateTime>): List<GCalEvent>
}

class GCalSyncerImpl @Inject constructor(
        private val gcal: GCalService,
        private val clock: Clock

) : GCalSyncer {

    companion object {
        private val log = LOG(javaClass)
    }

    override fun loadEvents(range: Pair<DateTime, DateTime>): List<GCalEvent> {
        if (!gcal.isOnline) {
            throw IllegalStateException("can not sync: gcal is not online!")
        }

        val gcalEvents = gcal.listEvents(range.first, range.second)

        log.trace("gcal listed ${gcalEvents.size} events in total.")
        return gcalEvents
    }


}
