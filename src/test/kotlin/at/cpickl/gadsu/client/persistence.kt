package at.cpickl.gadsu.client

import at.cpickl.gadsu.PersistenceException
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.testinfra.DUMMY_UUID
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.LogTestListener
import at.cpickl.gadsu.testinfra.skip
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Listeners
import org.testng.annotations.Test


// MINOR improve solution: http://testng.org/doc/documentation-main.html#testng-listeners
@Test @Listeners(LogTestListener::class)
class ClientSpringJdbcRepositoryTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    private var idGenerator: IdGenerator = mock(IdGenerator::class.java)

    override fun sqlScripts() = arrayOf("create_client.sql")
    override fun resetTables() = arrayOf("client")

    @BeforeMethod
    fun initMocks() {
        idGenerator = mock(IdGenerator::class.java)
    }

    fun insert() {
        `when`(idGenerator.generate()).thenReturn(DUMMY_UUID)

        val actualSaved = testee().insert(unsavedClient)

//        assertThat(actualSaved, theSameAs(newClient.withId(generatedId)).excludePath("Client.Created"))
        assertThat(actualSaved, equalTo(unsavedClient.withId(DUMMY_UUID)))

        val result = jdbc().query("SELECT * FROM client", Client.ROW_MAPPER)
        assertThat(result, hasSize(1))
        assertThat(result[0], equalTo(actualSaved))
    }

    @Test(expectedExceptions = arrayOf(PersistenceException::class))
    fun insert_idSet_fails() {
        testee().insert(unsavedClient.withId(DUMMY_UUID))
    }

    @Test(dependsOnMethods = arrayOf("insert"))
    fun findAll() {
        skip("nope")
        `when`(idGenerator.generate()).thenReturn(DUMMY_UUID)

        val testee = testee()
        assertThat(testee.findAll(), hasSize(0)) // sanity check
        val actualSavedClient = testee.insert(unsavedClient)

        val found = testee.findAll()
        assertThat(found, hasSize(1))
        assertThat(found[0], equalTo(actualSavedClient))
    }

    private fun testee() = ClientSpringJdbcRepository(jdbc(), idGenerator)

}