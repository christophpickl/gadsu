package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.view.AsyncDialogSettings
import at.cpickl.gadsu.view.AsyncWorker
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Provider
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
        private val preferences: Provider<PreferencesData>,
        bus: EventBus
) : GCalController {

    companion object {
        private val DIALOG_TITLE = "GCal Sync"
    }

    private val log = LOG(javaClass)
    private val window: SyncReportWindow by lazy { SyncReportSwingWindow(mainFrame, bus) }

    @Subscribe open fun onRequestGCalSyncEvent(event: RequestGCalSyncEvent) {
        // MINOR add possibility to interrupt background process by clicking on window close button
        async.doInBackground<SyncReport?>(
                settings = AsyncDialogSettings("GCal Sync", "Verbindung zu Google Server wird aufgebaut ..."),
                backgroundTask = { doTheSync() },
                doneTask = { report: SyncReport? ->
                    if (report == null) {
                        // prematurely aborted, do nothing
                    } else if (report.isEmpty()) {
                        dialogs.show(
                                title = DIALOG_TITLE,
                                message = "Es wurden keinerlei beachtenswerte Termine gefunden."
                        )
                    } else {
                        window.initReport(report, clientService.findAll(ClientState.ACTIVE), preferences.get().isGmailAndGapiConfigured)
                        window.start()
                    }
                },
                exceptionTask = { e ->
                    log.error("GCal synchronisation failed!", e)
                    dialogs.show(DIALOG_TITLE, "Beim Synchronisieren mit Google Calender ist ein Fehler aufgetreten.", type = DialogType.ERROR)
                }
        )
    }

    @Subscribe open fun onRequestImportSyncEvent(event: RequestImportSyncEvent) {
        val appointmentsToImport = window.readImportAppointments().filter { it.enabled }
        val appointmentsToDelete = window.readDeleteAppointments()
        val appointmentsToUpdate = window.readUpdateAppointments()

        if (appointmentsToImport.isEmpty() && appointmentsToDelete.isEmpty() && appointmentsToUpdate.isEmpty()) {
            dialogs.show(DIALOG_TITLE, "Scherzkeks ;) Es gibt nix zum Importieren.")
            return
        }

        async.doInBackground<Unit>(
                settings = AsyncDialogSettings(DIALOG_TITLE, "Importiere Termine ..."),
                backgroundTask = {
                    doTheImport(appointmentsToImport, appointmentsToDelete, appointmentsToUpdate)
                },
                doneTask = {
                    window.closeWindow()
                    // MINOR gcal sync feedback: could show details on what exactly had happened (just a regular JTextArea and fill with some plain text, nothing fancy)
                    dialogs.show(DIALOG_TITLE, "Der Kalenderabgleich war erfolgreich.")
                },
                exceptionTask = { e ->
                    log.error("GCal import failed!", e)
                    dialogs.show(DIALOG_TITLE, "Beim Synchronisieren mit Google Calender ist ein Fehler aufgetreten.", type = DialogType.ERROR)
                    window.closeWindow()
                }
        )
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        window.destroy()
    }

    private fun doTheSync(): SyncReport? {
        if (!gcal.isOnline) { // this isOnline request got some heavy I/O
            dialogs.show(
                    title = DIALOG_TITLE,
                    message = "Du bist nicht mit Google Calender verbunden. Siehe Einstellungen. Neustarten!",
                    type = DialogType.WARN
            )
            return null
        }
        return syncService.syncAndSuggest()
    }

    private fun doTheImport(appointmentsToImport: List<ImportAppointment>,
                            appointmentsToDelete: List<Appointment>,
                            appointmentsToUpdate: List<Appointment>) {
        log.debug("IMPORT ================================ START")

        appointmentsToUpdate.forEach {
            appointmentService.insertOrUpdate(it)
        }

        appointmentsToDelete.forEach {
            appointmentService.delete(it)
        }

        syncService.import(appointmentsToImport)

        log.debug("IMPORT ================================ END")
    }

}

