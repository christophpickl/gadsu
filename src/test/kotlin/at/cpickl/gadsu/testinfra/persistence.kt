package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSpringJdbcRepository
import at.cpickl.gadsu.client.props.ClientPropsSpringJdbcRepository
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.persistence.DatabaseManager
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.persistence.SpringJdbcx
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentSpringJdbcRepository
import com.google.common.eventbus.EventBus
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hsqldb.jdbc.JDBCDataSource
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase
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
    private val allTables = arrayOf(TreatmentSpringJdbcRepository.TABLE, ClientPropsSpringJdbcRepository.TABLE, ClientSpringJdbcRepository.TABLE)

    private var dataSource: EmbeddedDatabase? = null
    private var jdbcx: SpringJdbcx? = null

    // MINOR @TEST - delete mock, and use testable implementations instead
    protected var bus = EventBus()
    protected var clock = SimpleTestableClock()
    protected var idGenerator: IdGenerator = mock(IdGenerator::class.java)
    protected var currentClient: CurrentClient = CurrentClient(EventBus())


    @BeforeClass
    fun initDb() {
        val dataSource = JDBCDataSource()
        dataSource.url = "jdbc:hsqldb:mem:testDb${javaClass.simpleName}"
        dataSource.user = "SA"
        log.info("Using data source URL: ${dataSource.url}")

        DatabaseManager(dataSource).migrateDatabase()
//        arrayOf("V1__create_tables.sql").forEach {
//            ScriptUtils.executeSqlScript(dataSource!!.connection, ClassPathResource("/gadsu/persistence/$it"))
//        }

        jdbcx = SpringJdbcx(dataSource)
    }

    @BeforeMethod
    fun resetState() {
        bus = EventBus()
        clock = SimpleTestableClock()
        currentClient = CurrentClient(bus)

        allTables.forEach { jdbcx!!.execute("DELETE FROM $it") }
    }

    @AfterClass
    fun shutdownDb() {
        dataSource?.shutdown()
    }

    protected fun jdbcx(): SpringJdbcx = jdbcx!!

    protected fun whenGenerateIdReturnTestUuid(vararg ids: String = arrayOf(TEST_UUID1)) {
        var mockStub = `when`(idGenerator.generate())
        ids.forEach { mockStub = mockStub.thenReturn(it) }
    }

    protected fun nullJdbcx() = mock(Jdbcx::class.java)

    protected fun assertEmptyTable(tableName: String) {
        assertThat("Expected table '$tableName' to be empty.", jdbcx().countTableEntries(tableName), equalTo(0))
    }



    protected fun insertClient(prototype: Client = Client.unsavedValidInstance(), id: String = TEST_UUID1): Client {
        return ClientSpringJdbcRepository(jdbcx(), SimpleTestableIdGenerator(id)).insertWithoutPicture(prototype)
    }

    protected fun insertTreatment(prototype: Treatment, id: String = TEST_UUID1): Treatment {
        return TreatmentSpringJdbcRepository(jdbcx(), SimpleTestableIdGenerator(id)).insert(prototype)
    }

}

fun SpringJdbcx.countTableEntries(tableName: String): Int {
    var count: Int? = null
    jdbc.query("SELECT COUNT(*) AS cnt FROM $tableName") { rs -> count = rs.getInt("cnt") }
    return count!!
}
