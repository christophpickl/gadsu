package at.cpickl.gadsu

import at.cpickl.gadsu.view.MacHandler
import at.cpickl.gadsu.view.MainWindow
import at.cpickl.gadsu.view.ShowAboutDialogEvent
import at.cpickl.gadsu.view.ViewModule
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.TypeLiteral
import com.google.inject.matcher.Matchers
import com.google.inject.spi.InjectionListener
import com.google.inject.spi.TypeEncounter
import com.google.inject.spi.TypeListener
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
        private val bus: EventBus
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
        if (!MacHandler.isMacApp()) {
            log.debug("registerMacStuff() not enabled")
            return
        }
        log.debug("registerMacStuff() ... enabling mac specific handlers")
        val mac = MacHandler()
        mac.registerAbout { bus.post(ShowAboutDialogEvent()) }
        mac.registerQuit { bus.post(QuitUserEvent()) }
        // MINOR in future we will need prefs as well: mac.registerPreferences { ... }
    }

}


class GadsuModule : AbstractModule() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun configure() {
        log.debug("configure()")

        val eventBus = EventBus({ exception, context ->
            log.error("Uncaught exception in event bus! context=$context", exception)
        })
        bind(EventBus::class.java).toInstance(eventBus)

        // remove necessity to call "bus.register(this)" all the time
        // https://spin.atomicobject.com/2012/01/13/the-guava-eventbus-on-guice/
        bindListener(Matchers.any(), object : TypeListener {
            override fun <I> hear(literal: TypeLiteral<I>, encounter: TypeEncounter<I>) {
                encounter.register(InjectionListener { i -> eventBus.register(i) })
            }
        })

        bind(AllMightyEventCatcher::class.java).asEagerSingleton()

        install(ViewModule())
    }
}

class AllMightyEventCatcher {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onEvent(event: Any) {
        log.trace("Event has been dispatched on EventBus: {}", event)
    }

}