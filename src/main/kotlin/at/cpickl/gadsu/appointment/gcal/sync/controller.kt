package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.joda.time.DateTime
import javax.inject.Inject


interface GCalController {

}

@Logged
open class GCalControllerImpl @Inject constructor(
        private val gcal: GCalService,
        private val dialogs: Dialogs,
        private val syncService: SyncService,
        private val clientService: ClientService,
        private val appointmentService: AppointmentService,
        bus: EventBus
) : GCalController {

    private val log = LOG(javaClass)
    private val window: SyncReportWindow by lazy { SyncReportSwingWindow(bus) }

    @Subscribe open fun onRequestGCalSyncEvent(event: RequestGCalSyncEvent) {
        if (!gcal.isOnline) {
            dialogs.show(
                    title = "GCal Sync fehlgeschlagen",
                    message = "Du bist nicht mit Google Calender verbunden. Siehe Einstellungen. Neustarten!",
                    type = DialogType.WARN
            )
            return
        }

        // TODO async infra
        val report = syncService.syncAndSuggest()

        if (report.eventsAndClients.isEmpty()) {
            dialogs.show(
                    title = "GCal Sync",
                    message = "Es wurden keinerlei beachtenswerte Termine gefunden."
            )
            return
        }

        window.initReport(report, clientService.findAll(ClientState.ACTIVE))
        window.start()

        // TODO check with yet existing appointments (do not re-import)
    }

    @Subscribe open fun onRequestImportSyncEvent(event: RequestImportSyncEvent) {
        val appointmentsToImport = window.readSelectedEvents()

        appointmentsToImport.forEach {
            appointmentService.insertOrUpdate(it.toAppointment())
        }

        window.closeWindow()
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        window.destroy()
    }

    private fun ImportAppointment.toAppointment(): Appointment {
        return Appointment(
                id = null,
                clientId = this.selectedClient.id!!,
                created = DateTime.now(),
                start = this.event.start,
                end = this.event.end,
                note = this.event.description,
                gcalId = this.event.id,
                gcalUrl = this.event.url
                )
    }

}

