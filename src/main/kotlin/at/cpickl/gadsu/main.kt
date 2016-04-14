package at.cpickl.gadsu

import at.cpickl.gadsu.client.ClientView
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

    fun start(args: Array<String>) {
        log.info("start(args={})", args)

        val guice = Guice.createInjector(GadsuModule(parseArgs(args)))
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

        database.initDatabase()
        registerMacStuff()
        SwingUtilities.invokeLater {
            window.changeContent(clientView)
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

class Development {
    companion object {
        val ENABLED: Boolean = System.getProperty("gadsu.development", "").equals("true")
        init {
            if (ENABLED) {
                println("Development mode is enabled via '-Dgadsu.development=true'")
            }
        }
    }
}

open class GadsuException(message: String, cause: Exception? = null) : RuntimeException(message, cause)
