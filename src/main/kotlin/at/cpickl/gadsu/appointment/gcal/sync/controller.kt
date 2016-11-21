package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.appointment.gcal.sync.MatchClients
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import javax.inject.Inject

class RequestGCalSyncEvent() : UserEvent()

interface GCalController {

}

@Logged
open class GCalControllerImpl @Inject constructor(
        private val gcal: GCalService,
        private val syncer: GCalSyncer,
        private val dialogs: Dialogs,
        private val clientService: ClientService,
        private val matcher: MatchClients,
        private val syncService: SyncService
) : GCalController {

    private val log = LOG(javaClass)
    private val window: SyncReportWindow by lazy { SyncReportSwingWindow() }

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

        window.initReport(report)
        window.start()

        // TODO check with yet existing appointments (do not re-import)
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        window.destroy()
    }


}