package at.cpickl.gadsu.persistence

import at.cpickl.gadsu.GADSU_DIRECTORY
import com.google.inject.AbstractModule
import org.hsqldb.jdbc.JDBCDataSource
import org.slf4j.LoggerFactory
import java.io.File
import javax.sql.DataSource

val GADSU_DATABASE_DIRECTORY = File(GADSU_DIRECTORY, "database")

class PersistenceModule(private val databaseUrl: String?) : AbstractModule() {
    companion object {
        val DEFAULT_DB_URL: String
        private val DB_USER = "SA"
        init {
//            DEFAULT_DB_URL = "jdbc:hsqldb:mem:mymemdb;hsqldb.tx=mvcc"
            DEFAULT_DB_URL = "jdbc:hsqldb:file:${File(GADSU_DATABASE_DIRECTORY, "database").absolutePath}"

        }
    }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun configure() {
        val databaseUrl = databaseUrl ?: DEFAULT_DB_URL
        log.debug("configure() ... using database URL; '{}'", databaseUrl)

        val dataSource = JDBCDataSource()
        dataSource.url = databaseUrl
        dataSource.user = DB_USER

        bind(DataSource::class.java).toInstance(dataSource)
        bind(Jdbcx::class.java).toInstance(SpringJdbcx(dataSource))
        val dbManager = FlywayDatabaseManager(dataSource)
        bind(DatabaseManager::class.java).toInstance(dbManager)
        dbManager.migrateDatabase() // do it here, in order it happen first before all other stuff happens
        // or get the prefs data lazily... val memoizedFoo by lazy { foo(bar) }

        install(BackupModule())
    }
}
