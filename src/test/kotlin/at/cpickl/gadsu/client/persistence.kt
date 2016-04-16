package at.cpickl.gadsu.client

import at.cpickl.gadsu.PersistenceException
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.testinfra.Expects.expect
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.TEST_UUID
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.jdbc.core.JdbcTemplate
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test(groups = arrayOf("hsqldb"))
//@Listeners(LogTestListener::class)
class ClientSpringJdbcRepositoryTest : HsqldbTest() {
//    init {
//        TestLogger().configureLog()
//    }

    private val unsavedClient = Client.unsavedValidInstance()
    private var idGenerator: IdGenerator = mock(IdGenerator::class.java)
    private var testee = ClientSpringJdbcRepository(JdbcTemplate(), idGenerator)

    override fun resetTables() = arrayOf("client")

    @BeforeMethod
    fun setUp() {
        idGenerator = mock(IdGenerator::class.java)
        testee = ClientSpringJdbcRepository(jdbc(), idGenerator)
    }

    fun insert() {
        whenGenerateIdReturnTestUuid()

        val actualSaved = testee.insert(unsavedClient)
        assertThat(actualSaved, equalTo(unsavedClient.copy(id = TEST_UUID)))

        val result = jdbc().query("SELECT * FROM client", Client.ROW_MAPPER)
        assertThat(result, contains(actualSaved))
    }

    fun insert_idSet_fails() {
        expect(PersistenceException::class, "Client must not have set the ID", {
            testee.insert(unsavedClient.copy(id = TEST_UUID))
        })
    }

    @Test(dependsOnMethods = arrayOf("insert"))
    fun findAll() {
        whenGenerateIdReturnTestUuid()

        assertThat(testee.findAll(), empty()) // sanity check
        val actualSavedClient = testee.insert(unsavedClient)

        assertSingleFindAll(actualSavedClient)
    }

    @Test(dependsOnMethods = arrayOf("insert"))
    fun update_sunshine() {
        whenGenerateIdReturnTestUuid()

        val savedClient = testee.insert(unsavedClient)
        val changedClient = savedClient.copy(lastName = "something else")
        testee.update(changedClient)

        assertSingleFindAll(changedClient)
    }

    @Test(expectedExceptions = arrayOf(PersistenceException::class))
    fun update_notExisting_shouldFail() {
        testee.update(Client.savedValidInstance())
    }

    @Test(dependsOnMethods = arrayOf("insert"))
    fun update_changingCreated_shouldNotChangeAnything() {
        whenGenerateIdReturnTestUuid()

        val savedClient = testee.insert(unsavedClient)
        testee.update(savedClient.copy(created = savedClient.created.plusHours(1)))

        assertSingleFindAll(savedClient)
    }

    @Test(dependsOnMethods = arrayOf("insert", "findAll"))
    fun delete_sunshine() {
        whenGenerateIdReturnTestUuid()
        val savedClient = testee.insert(unsavedClient)

        testee.delete(savedClient)

        assertThat(testee.findAll(), empty())
    }

    @Test(expectedExceptions = arrayOf(PersistenceException::class))
    fun delete_clientWithNoId_fails() {
        testee.delete(unsavedClient)
    }

    @Test(expectedExceptions = arrayOf(PersistenceException::class))
    fun delete_notPersistedClient_fails() {
        testee.delete(unsavedClient.copy(id = "not_exists"))
    }

    private fun whenGenerateIdReturnTestUuid() {
        `when`(idGenerator.generate()).thenReturn(TEST_UUID)
    }

    private fun assertSingleFindAll(expected: Client) {
        val found = testee.findAll()
        assertThat(found, contains(expected))
    }

}