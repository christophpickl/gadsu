package at.cpickl.gadsu.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.client.view.ChangeBehaviour
import at.cpickl.gadsu.client.view.ClientViewController
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.Logged
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject


@Logged
open class MainFrameController @Inject constructor(
        private val frame: MainFrame,
        private val clientController: ClientViewController,
        private val prefs: Prefs
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe open fun onAppStartupEvent(@Suppress("UNUSED_PARAMETER") event: AppStartupEvent) {
        if (prefs.windowDescriptor != null) {
            frame.descriptor = prefs.windowDescriptor!!
        }
    }

    @Subscribe open fun onQuitEvent(@Suppress("UNUSED_PARAMETER") event: QuitEvent) {
        if (clientController.checkChanges() === ChangeBehaviour.ABORT) {
            log.debug("Quit aborted by changes detected by the client controller.")
            return
        }

        prefs.windowDescriptor = frame.descriptor
        frame.close()
    }
}
