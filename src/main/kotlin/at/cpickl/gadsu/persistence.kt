package at.cpickl.gadsu

import at.cpickl.gadsu.service.DateFormats
import com.google.common.eventbus.Subscribe
import org.hsqldb.jdbc.JDBCDataSource
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ScriptUtils
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


/**
 * Shuts down the database connection so HSQLDB persists its data to the filesystem.
 */
class DatabaseManager @Inject constructor(
        private val dataSource: JDBCDataSource
) {
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

    @Subscribe fun onQuit(event: QuitUserEvent) {
        log.info("Closing database connection.")
        dataSource.connection.close()
    }

}
