package at.cpickl.gadsu

import at.cpickl.gadsu.service.DateFormats
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import org.hsqldb.jdbc.JDBCDataSource
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.ScriptUtils
import java.io.File
import javax.inject.Inject


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
        private val DEFAULT_DB_URL: String
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

        bind(JDBCDataSource::class.java).toInstance(dataSource)
        bind(JdbcTemplate::class.java).toInstance(JdbcTemplate(dataSource))
        bind(DatabaseManager::class.java).asEagerSingleton()
    }
}

/**
 * Shuts down the database connection so HSQLDB persists its data to the filesystem.
 */
class DatabaseManager @Inject constructor(
        private val dataSource: JDBCDataSource
) {
    init {
        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            log.info("Database shutdown hook is running.")
            closeConnection()
        }, "DatabaseShutdownHookThread"))
    }
    private val log = LoggerFactory.getLogger(javaClass)

    fun initDatabase() {
        log.debug("Executing init SQL scripts.")
        // FIXME catch exception (e.g. file locked!) => maybe app already running? or just not closed properly => need to remove lock file manually (give hint where it resides)
        arrayOf("create_client.sql").forEach {
            val scriptResource = ClassPathResource("/gadsu/persistence/" + it)
            log.trace("Executing script: {}", scriptResource)
            ScriptUtils.executeSqlScript(dataSource.connection, scriptResource)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe fun onQuit(event: QuitUserEvent) {
        closeConnection()
    }

    private fun closeConnection() {
        log.info("Closing database connection.")
        dataSource.connection.close()
    }

}
