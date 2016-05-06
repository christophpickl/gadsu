package at.cpickl.gadsu.version

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.IS_OS_MAC
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.OpenWebpageEvent
import at.cpickl.gadsu.view.AsyncDialogSettings
import at.cpickl.gadsu.view.AsyncWorker
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import at.cpickl.gadsu.view.currentActiveJFrame
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import java.net.URL
import javax.inject.Inject

interface VersionUpdater {
    // nothing, just via events :)
}

open class VersionUpdaterImpl @Inject constructor(
        private val checker: VersionChecker,
        private val dialogs: Dialogs,
        private val asyncWorker: AsyncWorker,
        private val bus: EventBus,
        private val prefs: Prefs
) : VersionUpdater {
    private val log = LOG(javaClass)

    private val suffix = if (IS_OS_MAC) "dmg" else "jar"

    private fun checkForUpdates(settings: AsyncDialogSettings?) {
        log.debug("validateVersion(settings)")
        asyncWorker.doInBackground(settings, { checker.check() }, { onResult(it) })
    }

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        if (prefs.preferencesData.checkUpdates) {
            log.debug("Preferences stated we should check updates on startup")
            checkForUpdates(null) // dont display progress dialog when checking at startup
        }
    }
    private val dialogTitle = "Auto Update"

    @Subscribe open fun onCheckForUpdatesEvent(event: CheckForUpdatesEvent) {
        checkForUpdates(AsyncDialogSettings(dialogTitle, "Prüfe die aktuellste Version ..."))
    }

    private fun onResult(result: VersionCheckResult) {
        when (result) {
            is VersionCheckResult.UpToDate -> dialogs.show(dialogTitle, "Juchu, du hast die aktuellste Version installiert!",
                    arrayOf("Ok"), null, DialogType.INFO, currentActiveJFrame())
            is VersionCheckResult.OutDated -> {
                val selected = dialogs.show(dialogTitle, "Es gibt eine neuere Version von Gadsu.\n" +
                        "Du benutzt zur Zeit ${result.current.toLabel()} aber es ist bereits Version ${result.latest.toLabel()} verfügbar.\n" +
                        "Bitte lade die neueste Version herunter.",
                        arrayOf("Download starten"), null, DialogType.WARN, currentActiveJFrame())
                if (selected == null) {
                    log.debug("User closed window by hitting the close button, seems as he doesnt care about using the latest version :-/")
                    return
                }
                val version = result.latest.toLabel()
                val downloadUrl = "https://github.com/christophpickl/gadsu/releases/download/v$version/Gadsu-$version.$suffix"
                log.info("Going to download latest gadsu version from: {}", downloadUrl)
                bus.post(OpenWebpageEvent(URL(downloadUrl)))
            }
        }
    }

}
