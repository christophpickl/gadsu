package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import javax.inject.Inject


interface GCalController {

}

@Logged
open class GCalControllerImpl @Inject constructor(
        private val gcal: GCalService,
        private val dialogs: Dialogs,
        private val syncService: SyncService,
        private val clientService: ClientService,
        private val mainFrame: MainFrame,
        bus: EventBus
) : GCalController {

    private val log = LOG(javaClass)
    private val window: SyncReportWindow by lazy { SyncReportSwingWindow(mainFrame, bus) }

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
        val appointmentsToImport = window.readImportAppointments().filter { it.enabled }

        // FIXME get back result and show UI
        syncService.import(appointmentsToImport)

        window.closeWindow()
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        window.destroy()
    }

}

