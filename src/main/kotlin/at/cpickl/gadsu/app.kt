package at.cpickl.gadsu

import at.cpickl.gadsu.development.Development
import at.cpickl.gadsu.development.ShowDevWindowEvent
import at.cpickl.gadsu.global.AppStartupEvent
import at.cpickl.gadsu.global.GADSU_DIRECTORY
import at.cpickl.gadsu.global.GadsuSystemProperty
import at.cpickl.gadsu.global.GlobalExceptionHandler
import at.cpickl.gadsu.global.QuitAskEvent
import at.cpickl.gadsu.global.QuitEvent
import at.cpickl.gadsu.persistence.GADSU_DATABASE_DIRECTORY
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.preferences.ShowPreferencesEvent
import at.cpickl.gadsu.service.GADSU_LOG_FILE
import at.cpickl.gadsu.service.LogConfigurator
import at.cpickl.gadsu.service.MetaInf
import at.cpickl.gadsu.start.Args
import at.cpickl.gadsu.start.ArgsActionException
import at.cpickl.gadsu.start.ArgsActionExecutor
import at.cpickl.gadsu.start.GadsuModule
import at.cpickl.gadsu.start.parseArgsOrHelp
import at.cpickl.gadsu.view.MacHandler
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.ShowAboutDialogEvent
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.google.common.eventbus.EventBus
import com.google.common.net.HostAndPort
import com.google.inject.Guice
import java.util.Arrays
import javax.inject.Inject
import javax.swing.SwingUtilities

/**
 * This name will also show up in the native mac app, so dont rename that class.
 */
object Gadsu {

    private val log = LOG {}

    @JvmStatic
    fun main(cliArgs: Array<String>) {
        val args = parseArgsOrHelp(cliArgs, false) ?: return

        initLogging(args.debug)
        initSwingLookAndFeel()

        try {
            start(args)
        } catch (e: ArgsActionException) {
            log.error("Invalid CLI arguments! " + Arrays.toString(cliArgs), e)
            System.err.println("You entered an invalid CLI argument: '" + Arrays.toString(cliArgs) + "'! Exception message: " + e.message)
        }
    }

    private fun initLogging(debug: Boolean) {
        if (GadsuSystemProperty.disableLog.isEnabledOrFalse()) {
            println("Gadsu log configuration disabled. (most likely because tests come with own log config)")
        } else {
            LogConfigurator(debug).configureLog()
        }
    }

    private fun start(args: Args) {
        log.info("************************************************************************************************")
        log.info("************************************************************************************************")
        log.info("************************************************************************************************")
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
        private val mainFrame: MainFrame,
        private val prefs: Prefs,
        private val metaInf: MetaInf
) {
    private val log = LOG {}

    fun start() {
        logInfo()
        enableProxy()

        registerMacHandler()

        SwingUtilities.invokeLater {
            bus.post(AppStartupEvent())
            frame.start()

            if (Development.ENABLED) {
                if (Development.SHOW_DEV_WINDOW_AT_STARTUP) {
                    bus.post(ShowDevWindowEvent())
                    mainFrame.requestFocus()
                }
            }
        }
    }

    private fun enableProxy() {
        if (prefs.preferencesData.proxy != null) {
            val proxy = prefs.preferencesData.proxy!!
            val hostAndPort = HostAndPort.fromString(proxy).withDefaultPort(8080)

            log.info("Enabling proxy: '{}'", hostAndPort)
            System.setProperty("http.proxyHost", hostAndPort.host)
            System.setProperty("http.proxyPort", hostAndPort.port.toString())
            System.setProperty("https.proxyHost", hostAndPort.host)
            System.setProperty("https.proxyPort", hostAndPort.port.toString())
        }
    }

    private fun logInfo() {
        log.info("")
        log.info("")
        log.info { """
    _____             _
   / ____|           | |
  | |  __   __ _   __| | ___  _   _
  | | |_ | / _` | / _` |/ __|| | | |
  | |__| || (_| || (_| |\__ \| |_| |
   \_____| \__,_| \__,_||___/ \__,_| v${metaInf.applicationVersion.toLabel()}
   -==================================================================-
    Gadsu directory: ${GADSU_DIRECTORY.absolutePath}
    Database directory: ${GADSU_DATABASE_DIRECTORY.absolutePath}
    Log file: ${GADSU_LOG_FILE.absolutePath}
   -==================================================================-

""" }
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

