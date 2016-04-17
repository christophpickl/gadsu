package at.cpickl.gadsu.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.QuitUserEvent
import at.cpickl.gadsu.client.view.ChangeBehaviour
import at.cpickl.gadsu.client.view.ClientViewController
import at.cpickl.gadsu.service.Prefs
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject


class MainWindowController @Inject constructor(
        private val window: MainWindow,
        private val clientController: ClientViewController,
        private val prefs: Prefs
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onAppStartupEvent(@Suppress("UNUSED_PARAMETER") event: AppStartupEvent) {
        if (prefs.windowDescriptor != null) {
            window.descriptor = prefs.windowDescriptor!!
        }
    }

    @Subscribe fun onQuitUserEvent(@Suppress("UNUSED_PARAMETER") event: QuitUserEvent) {
        log.info("onQuitUserEvent(event)")

        if (clientController.checkChanges() === ChangeBehaviour.ABORT) {
            log.debug("Quit aborted by changes detected by the client controller.")
            return
        }

        prefs.windowDescriptor = window.descriptor
        window.close()
    }
}
