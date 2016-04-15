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

    fun start(cliArgs: Array<String>) {
        log.info("start(args={})", cliArgs)

        val args = parseArgsOrHelp(cliArgs) ?: return

        val guice = Guice.createInjector(GadsuModule(args))
        val app = guice.getInstance(GadsuGuiceStarter::class.java)
        app.start()
    }

    private fun parseArgsOrHelp(cliArgs: Array<String>): Args? {
        val args: Args
        try {
            args = parseArgs(cliArgs)
        } catch (e: ArgsException) {
            e.help()
            return null
        }

        if (args.help != null) {
            (args.help)()
            return null
        }
        return args
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

        database.initDatabase()
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
