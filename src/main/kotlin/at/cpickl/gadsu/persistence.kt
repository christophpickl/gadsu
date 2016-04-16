package at.cpickl.gadsu

import at.cpickl.gadsu.service.DateFormats
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import org.flywaydb.core.Flyway
import org.hsqldb.jdbc.JDBCDataSource
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import java.io.File
import java.sql.Timestamp
import javax.inject.Inject
import javax.sql.DataSource


/**
 * Extend date format custom to HSQLDB timestamp.
 */
@Suppress("UNUSED") val DateFormats.Companion.SQL_TIMESTAMP: DateTimeFormatter
    get() = DateTimeFormat.forPattern("'TIMESTAMP' ''yyyy-MM-dd HH:mm:ss''")


/**
 * Custom exception type.
 */
open class PersistenceException(message: String, cause: Exception? = null) : GadsuException(message, cause)


class PersistenceModule(private val args: Args) : AbstractModule() {
    companion object {
        val DEFAULT_DB_URL: String
        init {
            // or: "jdbc:hsqldb:mem:mymemdb"
            DEFAULT_DB_URL = "jdbc:hsqldb:file:" + File(GADSU_DIRECTORY, "database").absolutePath
        }
    }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun configure() {
        val databaseUrl = args.databaseUrl ?: DEFAULT_DB_URL
        log.debug("configure() ... using database URL; '{}'", databaseUrl)

        val dataSource = JDBCDataSource()
        dataSource.url = databaseUrl
        dataSource.user = "SA"

        bind(DataSource::class.java).toInstance(dataSource)
        bind(JdbcTemplate::class.java).toInstance(JdbcTemplate(dataSource))
        bind(DatabaseManager::class.java).asEagerSingleton()
    }
}

/**
 * Shuts down the database connection so HSQLDB persists its data to the filesystem.
 */
class DatabaseManager @Inject constructor(
        private val dataSource: DataSource
) {
    init {
        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            log.info("Database shutdown hook is running.")
            closeConnection()
        }, "DatabaseShutdownHookThread"))
    }
    private val log = LoggerFactory.getLogger(javaClass)
    private var databaseConnected: Boolean = false

    fun migrateDatabase() {
        log.info("migrateDatabase()")
        // https://flywaydb.org/documentation/api/

        val flyway = Flyway()
        flyway.setLocations(*arrayOf("/gadsu/persistence"))
        flyway.dataSource = dataSource
        flyway.migrate()
        databaseConnected = true
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe fun onQuit(event: QuitUserEvent) {
        closeConnection()
    }

    private fun closeConnection() {
        if (!databaseConnected) {
            log.warn("Not going to close database connection as it was never successfully opened")
            return
        }
        log.info("Closing database connection.")
        try {
            dataSource.connection.close()
        } catch (e: Exception) {
            // when there is a lock, the shutdown hook will fail, avoid this!
            log.error("Could not close database connection.", e)
        }
    }

}

fun DateTime.toSqlTimestamp() = Timestamp(millis)
