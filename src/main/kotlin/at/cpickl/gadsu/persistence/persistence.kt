package at.cpickl.gadsu.persistence

import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.global.QuitEvent
import at.cpickl.gadsu.image.readBufferedImage
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.toDateTime
import com.google.common.annotations.VisibleForTesting
import com.google.common.eventbus.Subscribe
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.flywaydb.core.api.MigrationInfo
import org.flywaydb.core.api.callback.BaseFlywayCallback
import org.flywaydb.core.internal.util.jdbc.JdbcUtils
import org.hsqldb.HsqlException
import org.hsqldb.error.ErrorCode
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.awt.image.BufferedImage
import java.sql.Blob
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import javax.inject.Inject
import javax.sql.DataSource

interface Persistable {
    val yetPersisted: Boolean
}

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

    PROPS_INVALID_KEY

}


interface DatabaseManager {

    fun migrateDatabase()
    fun repairDatabase()

}

/**
 * Shuts down the database connection so HSQLDB persists its data to the filesystem.
 */
@Logged
open class FlywayDatabaseManager @Inject constructor(
        private val ds: DataSource
) : DatabaseManager {

    companion object {
        private val MIGRATION_LOCATION = "/gadsu/persistence"
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread(Runnable {

//            log.info("dumping env variables")
//            System.getProperties().forEach { key, value -> log.info("property: {} = {}", key, value) }

            log.debug("Database shutdown hook is running and dancing around.")
            closeConnection()
        }, "DatabaseShutdownHookThread"))
    }
    private val log = LOG(javaClass)
    private var databaseConnected: Boolean = false

    override fun migrateDatabase() {
        log.info("migrateDatabase()")

        val flyway = buildFlyway()
        try {
            flyway.migrate()
        } catch(e: FlywayException) {
            if (e.message?.contains("validate failed", true) ?: false) {
                log.warn("Migration failed due to validation error, going to repair the database first and try migrating then.", e)
                flyway.repair()
                log.info("DB repair done, going to migrate now again.")
                flyway.migrate()
            } else {
                val dbLockException = DatabaseLockedException.buildByCause(e)
                throw dbLockException ?: e
            }
        }
        databaseConnected = true

        log.debug("Good luck, DB migration was successfull.")
    }

    override fun repairDatabase() {
        log.info("repairDatabase()")
        buildFlyway().repair()
    }

    @VisibleForTesting fun buildFlyway() = Flyway().apply {
        setLocations(MIGRATION_LOCATION)
        dataSource = ds

        val myCallback = object : BaseFlywayCallback() {
            override fun beforeEachMigrate(connection: Connection, info: MigrationInfo) {
                log.debug("Execute migration step v{}: {}", info.version, info.description)
            }
        }
        setCallbacks(*callbacks.toMutableList().apply { add(myCallback) }.toTypedArray())
    }

    private fun changeTransactionControl(mode: String) {
        val connection = JdbcUtils.openConnection(ds)
        connection.createStatement().execute("SET DATABASE TRANSACTION CONTROL $mode")
        JdbcUtils.closeConnection(connection)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe open fun onQuit(event: QuitEvent) {
        closeConnection()
    }

    private fun closeConnection() {
        if (!databaseConnected) {
            log.warn("Not going to close database connection as it was never successfully opened")
            return
        }
        log.debug("Closing database connection.")
        try {
            ds.connection.close()
        } catch (e: Exception) {
            // when there is a lock, the shutdown hook will fail, avoid this!
            log.error("Could not close database connection.", e)
        }
    }

}

class DatabaseLockedException(message: String, cause: FlywayException) : GadsuException(message, cause) {
    companion object {
        fun buildByCause(e: FlywayException): DatabaseLockedException? {
            val hsqlCause = parseCause(e) ?: return null
            return if (hsqlCause.isCausedByLockFailure()) DatabaseLockedException("Database locked: ${hsqlCause.message}", e) else null
        }

        private fun parseCause(e: FlywayException): HsqlException? {
            val sqlCause = (e.cause ?: return null) as? SQLException ?: return null
            return (sqlCause.cause ?: return null) as? HsqlException ?: return null
        }

        private fun HsqlException.isCausedByLockFailure() =
                // strangely returns "-451" ?
                Math.abs(errorCode) == ErrorCode.LOCK_FILE_ACQUISITION_FAILURE
    }

}

// --------------------------------------------------------------------------- extension methods


fun Persistable.ensurePersisted() {
    if (!yetPersisted) {
        throw PersistenceException("Persistable must have set an ID! ($this)", PersistenceErrorCode.EXPECTED_YET_PERSISTED)
    }
}

fun Persistable.ensureNotPersisted() {
    if (yetPersisted) {
        throw PersistenceException("Persistable must not have set an ID! ($this)", PersistenceErrorCode.EXPECTED_NOT_YET_PERSISTED)
    }
}

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

fun ResultSet.getDateTime(column: String) = getTimestamp(column).toDateTime()
