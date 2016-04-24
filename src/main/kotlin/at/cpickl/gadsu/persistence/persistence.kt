package at.cpickl.gadsu.persistence

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.QuitUserEvent
import at.cpickl.gadsu.image.readBufferedImage
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import com.google.common.eventbus.Subscribe
import org.flywaydb.core.Flyway
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.awt.image.BufferedImage
import java.sql.Blob
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
open class PersistenceException(message: String, val errorCode: PersistenceErrorCode, cause: Exception? = null) : GadsuException(message, cause)

enum class PersistenceErrorCode {

    EXPECTED_YET_PERSISTED,
    EXPECTED_NOT_YET_PERSISTED,

    EXPECT_DELETED_ONE,
    EXPECT_UPDATE_ONE,

    EXPECT_QUERY_SINGLE_ONE,

    UNKNOWN,

}

/**
 * Shuts down the database connection so HSQLDB persists its data to the filesystem.
 */
@Logged
open class DatabaseManager @Inject constructor(
        private val dataSource: DataSource
) {
    init {
        Runtime.getRuntime().addShutdownHook(Thread(Runnable {

            log.info("dumping env variables")
            System.getProperties().forEach { key, value -> log.info("property: {} = {}", key, value) }

            log.info("Database shutdown hook is running.")
            closeConnection()
        }, "DatabaseShutdownHookThread"))
    }
    private val log = LOG(javaClass)
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
    @Subscribe open fun onQuit(event: QuitUserEvent) {
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

// --------------------------------------------------------------------------- extension methods

fun DateTime.toSqlTimestamp() = Timestamp(millis)

fun Blob?.toByteArray(): ByteArray? {
    if (this == null) {
        return null
    }
    val blobLength = this.length().toInt()
    return this.getBytes(1, blobLength)
}

fun Blob.toBufferedImage(): BufferedImage? {
    return toByteArray()?.readBufferedImage()
}
