package at.cpickl.gadsu

import at.cpickl.gadsu.view.MacHandler
import at.cpickl.gadsu.view.MainWindow
import at.cpickl.gadsu.view.ShowAboutDialogEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Guice
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

class GadsuStarter {
    private val log = LoggerFactory.getLogger(javaClass)

    fun start(args: Array<String>) {
        log.debug("start(args)")

        val guice = Guice.createInjector(GadsuModule())
        val app = guice.getInstance(GadsuGuiceStarter::class.java)
        app.start()
    }

}

class GadsuGuiceStarter @Inject constructor(
        private val window: MainWindow,
        private val bus: EventBus,
        private val mac: MacHandler
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun start() {
        log.debug("start()")

        registerMacStuff()
        SwingUtilities.invokeLater {
            JFrame.setDefaultLookAndFeelDecorated(true)
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
                //                UIManager.setLookAndFeel(new SubstanceRavenLookAndFeel());
            } catch (e: Exception) {
                log.error("Could not set native look&feel!", e)
            }
            window.start()
        }
    }

    private fun registerMacStuff() {
        if (!mac.isEnabled()) {
            log.debug("registerMacStuff() not enabled")
            return
        }

        log.debug("registerMacStuff() ... enabling mac specific handlers")
        mac.registerAbout { bus.post(ShowAboutDialogEvent()) }
        mac.registerQuit { bus.post(QuitUserEvent()) }
        // MINOR in future we will need prefs as well: mac.registerPreferences { ... }
    }

}


class AllMightyEventCatcher {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onEvent(event: Any) {
        log.trace("Event has been dispatched on EventBus: {}", event)
    }

}