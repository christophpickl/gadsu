package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientJdbcRepository
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.client.xprops.XPropsSqlJdbcRepository
import at.cpickl.gadsu.persistence.DatabaseManager
import at.cpickl.gadsu.persistence.SpringJdbcx
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentJdbcRepository
import com.google.common.eventbus.EventBus
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hsqldb.jdbc.JDBCDataSource
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod


abstract class HsqldbTest {
    companion object {
        init {
            TestLogger().configureLog()
        }
    }

    private val log = LoggerFactory.getLogger(javaClass)
    private val allTables = arrayOf(
            TreatmentJdbcRepository.TABLE,
            XPropsSqlJdbcRepository.TABLE,
            ClientJdbcRepository.TABLE
    )

    private var dataSource: JDBCDataSource? = null
    protected lateinit var jdbcx: SpringJdbcx

    // MINOR @TEST - delete mock, and use testable implementations instead
    protected lateinit var bus: EventBus
    protected lateinit var clock: Clock
    protected lateinit var idGenerator: IdGenerator
    protected lateinit var currentClient: CurrentClient


    @BeforeClass
    fun initDb() {
        dataSource = JDBCDataSource()
        dataSource!!.url = "jdbc:hsqldb:mem:testDb${javaClass.simpleName}"
        dataSource!!.user = "SA"
        jdbcx = SpringJdbcx(dataSource!!)
        log.info("Using data source URL: ${dataSource!!.url}")

        DatabaseManager(dataSource!!).migrateDatabase()
    }

    @BeforeMethod
    fun resetState() {
        bus = EventBus()
        clock = SimpleTestableClock()
        idGenerator = SequencedTestableIdGenerator()
        currentClient = CurrentClient(bus)

        allTables.forEach { jdbcx.execute("DELETE FROM $it") }
    }

    @AfterClass
    fun shutdownDb() {
        // could have happened, that @BeforeClass failed, in this case shutting down will fail as a consequence. avoid this!
        dataSource?.connection?.close()
    }

    protected fun assertEmptyTable(tableName: String) {
        assertThat("Expected table '$tableName' to be empty.", jdbcx.countTableEntries(tableName), equalTo(0))
    }



    protected fun insertClientViaRepo(prototype: Client = Client.unsavedValidInstance()): Client {
        return ClientJdbcRepository(jdbcx, idGenerator).insertWithoutPicture(prototype)
    }

    protected fun deleteClientViaRepo(client: Client) {
        ClientJdbcRepository(jdbcx, idGenerator).delete(client)
    }

    protected fun insertTreatment(prototype: Treatment, id: String = TEST_UUID1): Treatment {
        return TreatmentJdbcRepository(jdbcx, SimpleTestableIdGenerator(id)).insert(prototype)
    }


    protected fun <T> assertEmptyRows(table: String, mapper: RowMapper<T>) {
        assertRows(table, mapper)
    }

    protected fun <T> assertRows(table: String, mapper: RowMapper<T>, vararg expected: T) {
        val rawRows = jdbcx.query("SELECT * FROM $table", mapper)
        if (expected.isEmpty()) {
            assertThat(rawRows, Matchers.emptyIterable())
        } else {
            assertThat(rawRows, Matchers.hasSize(expected.size))
            assertThat(rawRows, Matchers.contains(*expected))
        }
    }

}

fun SpringJdbcx.countTableEntries(tableName: String): Int {
    var count: Int? = null
    jdbc.query("SELECT COUNT(*) AS cnt FROM $tableName") { rs -> count = rs.getInt("cnt") }
    return count!!
}
