package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.PersistenceException
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSpringJdbcRepository
import at.cpickl.gadsu.client.savedValidInstance
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.testinfra.Expects.expect
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.TEST_UUID
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.mockito.Mockito.mock
import org.springframework.dao.DataIntegrityViolationException
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test(groups = arrayOf("hsqldb"))
class TreatmentSpringJdbcRepositoryTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    private val client = Client.savedValidInstance()
    private val unsavedTreatment = Treatment.unsavedValidInstance(client.id!!)

    private var testee = TreatmentSpringJdbcRepository(nullJdbcx(), idGenerator)

    @BeforeMethod
    fun setUp() {
        idGenerator = mock(IdGenerator::class.java)
        testee = TreatmentSpringJdbcRepository(jdbcx(), idGenerator)


        ClientSpringJdbcRepository(jdbcx(), object : IdGenerator {
            override fun generate() = client.id!!
        }).insert(client.copy(id = null))
    }

    // --------------------------------------------------------------------------- insert

    fun insert_sunshine() {
        whenGenerateIdReturnTestUuid()

        val expectedSaved = unsavedTreatment.copy(id = TEST_UUID)
        assertThat(testee.insert(unsavedTreatment, client), equalTo(expectedSaved))
        assertThat(jdbcx().query("SELECT * FROM treatment", Treatment.ROW_MAPPER), contains(expectedSaved))
    }

    fun insert_notYetPersistedClient_fail() {
        expect(type = PersistenceException::class, messageContains = "Client was not yet persisted", action = {
            testee.insert(unsavedTreatment, unsavedClient)
        })
    }

    fun insert_notExistingClient_failBecauseOfForeignKeyReferenceViolation() {
        whenGenerateIdReturnTestUuid()
        expect(type = PersistenceException::class,
                causedByType = DataIntegrityViolationException::class,
                action = {
                    testee.insert(unsavedTreatment, client.copy(id = "not_existing"))
                })

    }

    fun insert_yetPersistedTreatment_fail() {
        expect(type = PersistenceException::class, messageContains =  "Treatment was yet persisted", action = {
            testee.insert(unsavedTreatment.copy(id = "already_got_an_id"), client)
        })
    }

    // --------------------------------------------------------------------------- find

    @Test(dependsOnMethods = arrayOf("insert_sunshine"))
    fun findAll_sunshine() {
        whenGenerateIdReturnTestUuid()

        val saved = testee.insert(unsavedTreatment, client)

        assertThat(testee.findAllFor(client), contains(saved))
    }

    fun findAll_notYetPersistedClient_fail() {
        expect(type = PersistenceException::class, messageContains = "Client was not yet persisted", action = {
            testee.findAllFor(unsavedClient)
        })
    }

    // --------------------------------------------------------------------------- update

    @Test(dependsOnMethods = arrayOf("insert_sunshine", "findAll_sunshine"))
    fun `update sunshine`() {
        whenGenerateIdReturnTestUuid()
        val saved = testee.insert(unsavedTreatment, client)
        val updated = saved.copy(note = "new note")

        testee.update(updated)

        assertThat(testee.findAllFor(client), contains(updated))
    }

    fun `update unsaved treatment fails`() {
        expect(type = PersistenceException::class, action = {
            testee.update(unsavedTreatment)
        })
    }

    @Test(dependsOnMethods = arrayOf("insert_sunshine", "findAll_sunshine"))
    fun `update does not change client ID or created`() {
        whenGenerateIdReturnTestUuid()

        val saved = testee.insert(unsavedTreatment, client)
        val updated = saved.copy(clientId = "something else ID", created = saved.created.plusDays(1))

        testee.update(updated)

        val all = testee.findAllFor(client)
        assertThat(all, hasSize(1))
        val actual = all[0]
        assertThat(actual, equalTo(saved))
        assertThat(actual, not(equalTo(updated)))
    }

    // --------------------------------------------------------------------------- delete

    @Test(dependsOnMethods = arrayOf("insert_sunshine", "findAll_sunshine"))
    fun delete_sunshine() {
        whenGenerateIdReturnTestUuid()
        val saved = testee.insert(unsavedTreatment, client)

        testee.delete(saved)
        assertThat(testee.findAllFor(client), empty())
    }

    fun delete_notYetPersisted_fail() {
        expect(type = PersistenceException::class, messageContains = "Treatment was not yet persisted", action = {
            testee.delete(unsavedTreatment)
        })
    }

    fun delete_notExisting_fail() {
        expect(type = PersistenceException::class,
                causedByType = PersistenceException::class,
                causedByMessageContains = "Expected exactly one row to be deleted, but was: 0", action = {
            testee.delete(unsavedTreatment.copy(id = "not_existing"))
        })
    }

}