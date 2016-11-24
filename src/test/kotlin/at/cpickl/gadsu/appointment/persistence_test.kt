package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.testinfra.Expects.expect
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.assertEmptyTable
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.springframework.dao.DataIntegrityViolationException
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test class AppointmentJdbcRepositoryTest : HsqldbTest() {

    private val TABLE = TABLE_APPOINTMENT
    private lateinit var testee: AppointmentJdbcRepository
    private lateinit var savedClient: Client
    private lateinit var clientId: String
    private lateinit var app1: Appointment

    @BeforeMethod
    fun setUp() {
        testee = AppointmentJdbcRepository(jdbcx, idGenerator)
        savedClient = insertClientViaRepo()
        clientId = savedClient.id!!
        app1 = Appointment.unsavedValidInstance(clientId)
    }

    fun `insert sunshine`() {
        testee.insert(app1)
        assertRows(TABLE, Appointment.ROW_MAPPER, app1.copy(id = "2")) // id 1 already consumed by inserted client
    }

    fun `insert client not existing fails`() {
        expect(PersistenceException::class, {
            testee.insert(app1.copy(clientId = "not_existing"))
        }, causedByType = DataIntegrityViolationException::class)
    }

    @Test(dependsOnMethods = arrayOf("insert sunshine"))
    fun `findAll sunshine`() {
        val savedApp = testee.insert(app1)
        assertThat(testee.findAllFor(savedClient), equalTo(listOf(savedApp)))
    }

    @Test(dependsOnMethods = arrayOf("insert sunshine", "findAll sunshine"))
    fun `update sunshine`() {
        val savedApp = testee.insert(app1)
        val updatedApp = savedApp.copy(
                start = savedApp.start.minusDays(12),
                end = savedApp.end.minusDays(12)
        )
        testee.update(updatedApp)
        assertThat(testee.findAllFor(savedClient), equalTo(listOf(updatedApp)))
    }

    @Test(dependsOnMethods = arrayOf("insert sunshine"))
    fun `delete sunshine`() {
        val savedApp = testee.insert(app1)
        testee.delete(savedApp)
        jdbcx.assertEmptyTable(TABLE)
    }

    fun `findAllBetween`() {
        val now = DateTime.now()
        val inserted = testee.insert(app1.copy(start = now, end = now.plusMinutes(30)))

        assertThat(testee.findAllBetween(now.minusDays(1) to now.plusDays(1)), contains(inserted))
        assertThat(testee.findAllBetween(now.minusDays(2) to now.minusDays(1)), Matchers.empty())
    }

}
