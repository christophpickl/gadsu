package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.view.AsyncDialogSettings
import at.cpickl.gadsu.view.AsyncWorker
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
        private val async: AsyncWorker,
        private val appointmentService: AppointmentService,
        bus: EventBus
) : GCalController {

    private val log = LOG(javaClass)
    private val window: SyncReportWindow by lazy { SyncReportSwingWindow(mainFrame, bus) }

    private fun backgroundSync(): SyncReport? {
        if (!gcal.isOnline) { // this isOnline request got some heavy I/O
            dialogs.show(
                    title = "GCal Sync fehlgeschlagen",
                    message = "Du bist nicht mit Google Calender verbunden. Siehe Einstellungen. Neustarten!",
                    type = DialogType.WARN
            )
            return null
        }
        return syncService.syncAndSuggest()
    }

    @Subscribe open fun onRequestGCalSyncEvent(event: RequestGCalSyncEvent) {
        async.doInBackground(
                settings = AsyncDialogSettings("GCal Sync", "Baue Verbindung zu Google Server auf ..."),
                backgroundTask = { backgroundSync() },
                doneTask = { report ->
                    if (report == null) {
                        // prematurely aborted, do nothing
                    } else if (report.isEmpty()) {
                        dialogs.show(
                                title = "GCal Sync",
                                message = "Es wurden keinerlei beachtenswerte Termine gefunden."
                        )
                    } else {
                        window.initReport(report, clientService.findAll(ClientState.ACTIVE))
                        window.start()
                    }

                },
                exceptionTask = { e ->
                    log.error("GCal synchronisation failed!", e)
                    dialogs.show("GCal Sync Fehler", "Beim Synchronisieren mit Google Calender ist ein Fehler aufgetreten.", type = DialogType.ERROR)
                }
        )
    }

    @Subscribe open fun onRequestImportSyncEvent(event: RequestImportSyncEvent) {

        val appointmentsToImport = window.readImportAppointments().filter { it.enabled }

        val appointmentsToDelete = window.readDeleteAppointments()
        appointmentsToDelete.forEach {
            appointmentService.delete(it)
        }

        // FIXME get back result and show UI
        syncService.import(appointmentsToImport)

        window.closeWindow()
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        window.destroy()
    }

}

