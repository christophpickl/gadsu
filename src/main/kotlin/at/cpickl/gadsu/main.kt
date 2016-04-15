package at.cpickl.gadsu

import at.cpickl.gadsu.client.view.ClientView
import at.cpickl.gadsu.view.MacHandler
import at.cpickl.gadsu.view.MainWindow
import at.cpickl.gadsu.view.ShowAboutDialogEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Guice
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject
import javax.swing.SwingUtilities

val GADSU_DIRECTORY = File(System.getProperty("user.home"), ".gadsu")

class GadsuStarter {
    private val log = LoggerFactory.getLogger(javaClass)

    fun start(args: Args) {
        log.info("start(args={})", args)

        val guice = Guice.createInjector(GadsuModule(args))
        val app = guice.getInstance(GadsuGuiceStarter::class.java)
        app.start()
    }

}

class GadsuGuiceStarter @Inject constructor(
        private val window: MainWindow,
        private val bus: EventBus,
        private val mac: MacHandler,
        private val clientView: ClientView,
        private val database: DatabaseManager
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun start() {
        log.debug("start()")

        database.migrateDatabase()
        registerMacHandler()

        SwingUtilities.invokeLater {
            bus.post(AppStartupEvent())

            window.changeContent(clientView.asComponent())
            window.start()
        }
    }

    private fun registerMacHandler() {
        if (!mac.isEnabled()) {
            log.debug("registerMacHandler() not enabled")
            return
        }

        log.debug("Enabling mac specific handlers.")
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

    // EITHER - OR

//    @Subscribe fun onDeadEvent(event: DeadEvent) {
//        // TODO and again a global exception handler is required
//        throw GadsuException("Event (${event.event}) was not handled by anyone! (source: ${event.source})")
//    }

}

open class GadsuException(message: String, cause: Exception? = null) : RuntimeException(message, cause)
