package at.cpickl.gadsu

import at.cpickl.gadsu.development.Development
import at.cpickl.gadsu.development.ShowDevWindowEvent
import at.cpickl.gadsu.persistence.DatabaseManager
import at.cpickl.gadsu.persistence.GADSU_DATABASE_DIRECTORY
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.preferences.ShowPreferencesEvent
import at.cpickl.gadsu.service.GADSU_LOG_FILE
import at.cpickl.gadsu.view.MacHandler
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.ShowAboutDialogEvent
import com.google.common.eventbus.EventBus
import com.google.common.net.HostAndPort
import com.google.inject.Guice
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.swing.SwingUtilities


class GadsuStarter {
    private val log = LoggerFactory.getLogger(javaClass)

    fun start(args: Args) {
        log.info("start(args={})", args)

        GlobalExceptionHandler.register()
        log.debug("====> GUICE START")
        val guice = Guice.createInjector(GadsuModule(args))
        log.debug("====> GUICE END")

        if (args.action != null) {
            log.info("User requested to start custom action '{}'.", args.action)
            guice.getInstance(ArgsActionExecutor::class.java).execute(args.action)
            guice.getInstance(EventBus::class.java).post(QuitEvent())
            return
        }

        val app = guice.getInstance(GadsuGuiceStarter::class.java)
        GlobalExceptionHandler.mainFrame = guice.getInstance(MainFrame::class.java).asJFrame()

        app.start()
    }

}

class GadsuGuiceStarter @Inject constructor(
        private val frame: MainFrame,
        private val bus: EventBus,
        private val mac: MacHandler,
        private val database: DatabaseManager,
        private val mainFrame: MainFrame,
        private val prefs: Prefs
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun start() {
        logInfo()
        enableProxy()

        database.migrateDatabase()
        registerMacHandler()

        SwingUtilities.invokeLater {
            bus.post(AppStartupEvent())
            frame.start()

            if (Development.ENABLED) {
                bus.post(ShowDevWindowEvent())
                mainFrame.requestFocus()
            }
        }
    }

    private fun enableProxy() {
        if (prefs.preferencesData.proxy != null) {
            val proxy = prefs.preferencesData.proxy!!
            val hostAndPort = HostAndPort.fromString(proxy).withDefaultPort(8080)

            log.info("Enabling proxy: '{}'", hostAndPort)
            System.setProperty("http.proxyHost", hostAndPort.hostText)
            System.setProperty("http.proxyPort", hostAndPort.port.toString())
            System.setProperty("https.proxyHost", hostAndPort.hostText)
            System.setProperty("https.proxyPort", hostAndPort.port.toString())
        }
    }

    private fun logInfo() {
        log.info("")
        log.info("")
        log.info("""
  _____             _
 / ____|           | |
| |  __   __ _   __| | ___  _   _
| | |_ | / _` | / _` |/ __|| | | |
| |__| || (_| || (_| |\__ \| |_| |
 \_____| \__,_| \__,_||___/ \__,_|
""")
        log.info("")
        log.info("-==================================================================-")
        log.info("Gadsu directory: {}", GADSU_DIRECTORY.absolutePath)
        log.info("Database directory: {}", GADSU_DATABASE_DIRECTORY.absolutePath)
        log.info("Log file: {}", GADSU_LOG_FILE.absolutePath)
        log.info("-==================================================================-")
        log.info("")
        log.info("")
    }

    private fun registerMacHandler() {
        if (!mac.isEnabled()) {
            log.debug("registerMacHandler() not enabled")
            return
        }

        log.debug("Enabling mac specific handlers.")
        mac.registerAbout { bus.post(ShowAboutDialogEvent()) }
        mac.registerPreferences { bus.post(ShowPreferencesEvent()) }
        mac.registerQuit { bus.post(QuitAskEvent()) }
    }

}

