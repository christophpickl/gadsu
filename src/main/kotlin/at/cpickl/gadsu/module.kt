package at.cpickl.gadsu

import at.cpickl.gadsu.client.ClientModule
import at.cpickl.gadsu.service.ServiceModule
import at.cpickl.gadsu.view.ViewModule
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import com.google.inject.matcher.Matchers
import com.google.inject.spi.InjectionListener
import com.google.inject.spi.TypeEncounter
import com.google.inject.spi.TypeListener
import org.hsqldb.jdbc.JDBCDataSource
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import java.io.File


class GadsuModule(private val args: Args) : AbstractModule() {
    companion object {
        private val DEFAULT_DB_URL: String
        init {
            // or: "jdbc:hsqldb:mem:mymemdb"
            DEFAULT_DB_URL = "jdbc:hsqldb:file:" + File(GADSU_DIRECTORY, "database").absolutePath
        }
    }
    private val log = LoggerFactory.getLogger(javaClass)



    override fun configure() {
        log.debug("configure()")

        configureDataSource(args.databaseUrl ?: DEFAULT_DB_URL)

        bind(DatabaseManager::class.java).asEagerSingleton()
        bind(DevelopmentController::class.java).asEagerSingleton()

        configureEventBus()
        installSubModules()
    }

    private fun configureDataSource(databaseUrl: String) {
        log.info("configureDataSource(databaseUrl='{}')", databaseUrl)

        val dataSource = JDBCDataSource()
        dataSource.url = databaseUrl
        dataSource.user = "SA"

        bind(JDBCDataSource::class.java).toInstance(dataSource)
        bind(JdbcTemplate::class.java).toInstance(JdbcTemplate(dataSource))
    }

    private fun configureEventBus() {
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
    }

    private fun installSubModules() {
        install(ServiceModule())
        install(ViewModule())

        install(ClientModule())
    }
}
