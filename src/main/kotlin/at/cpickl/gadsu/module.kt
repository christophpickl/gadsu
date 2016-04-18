package at.cpickl.gadsu

import at.cpickl.gadsu.client.ClientModule
import at.cpickl.gadsu.image.ImageModule
import at.cpickl.gadsu.preferences.PreferencesModule
import at.cpickl.gadsu.report.ReportModule
import at.cpickl.gadsu.service.ServiceModule
import at.cpickl.gadsu.treatment.TreatmentModule
import at.cpickl.gadsu.view.ViewModule
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import com.google.inject.matcher.Matchers.any
import com.google.inject.spi.InjectionListener
import com.google.inject.spi.TypeEncounter
import com.google.inject.spi.TypeListener
import org.slf4j.LoggerFactory

class GadsuModule(private val args: Args) : AbstractModule() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun configure() {
        log.debug("configure()")
        bind(DevelopmentController::class.java).asEagerSingleton()

        configureEventBus()
        installSubModules(args)
    }


    private fun configureEventBus() {
        val bus = EventBus({ exception, context ->
            log.error("Uncaught exception in event bus! context=$context", exception)
            throw GadsuException("Some internal error occured!", exception) // TODO this does not get propagated to the global exception handler!
        })

        bind(EventBus::class.java).toInstance(bus)
        bindListener(any(), BusRegisteringTypeListener(bus))

        bind(AllMightyEventCatcher::class.java).asEagerSingleton()
    }

    private fun installSubModules(args: Args) {
        install(PersistenceModule(args.databaseUrl))
        install(ServiceModule())
        install(ViewModule())

        install(ClientModule())
        install(TreatmentModule())

        install(ImageModule())
        install(PreferencesModule(args.preferencesNode))
        install(ReportModule())
    }
}

// remove necessity to call "bus.register(this)" all the time
// https://spin.atomicobject.com/2012/01/13/the-guava-eventbus-on-guice/
class BusRegisteringTypeListener(private val bus: EventBus) : TypeListener {
    override fun <I> hear(literal: TypeLiteral<I>, encounter: TypeEncounter<I>) {
        encounter.register(InjectionListener { i -> bus.register(i) })
    }
}
