package at.cpickl.gadsu

import at.cpickl.gadsu.client.view.ClientView
import at.cpickl.gadsu.view.MacHandler
import at.cpickl.gadsu.view.MainWindow
import at.cpickl.gadsu.view.ShowAboutDialogEvent
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Guice
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject
import javax.swing.SwingUtilities

val GADSU_DIRECTORY = File(System.getProperty("user.home"), ".gadsu")

object GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)
    fun register() {
        log.debug("Registering global exception handler.")

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            log.error("Uncaught exception in thread (${thread.name})", throwable)
            Dialogs(null).show(
                    title = "Fehler",
                    message = "Ein unerwarteter Fehler ist aufgetreten! Siehe Programmlogs f\u00fcr mehr Details.",
                    buttonLabels = arrayOf("Programm schlie\u00dfen"),
                    type = DialogType.ERROR
            )
            System.exit(1)
        }
    }
}

class GadsuStarter {
    private val log = LoggerFactory.getLogger(javaClass)

    fun start(args: Args) {
        log.info("start(args={})", args)

        GlobalExceptionHandler.register()
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
