package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSpringJdbcRepository
import at.cpickl.gadsu.client.savedValidInstance
import at.cpickl.gadsu.client.savedValidInstance2
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.persistence.PersistenceErrorCode
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.testinfra.Expects
import at.cpickl.gadsu.testinfra.Expects.expect
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.SequencedTestableIdGenerator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.springframework.dao.DataIntegrityViolationException
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test(groups = arrayOf("hsqldb"))
class TreatmentSpringJdbcRepositoryTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    private val client = Client.savedValidInstance()
    private val client2 = Client.savedValidInstance2()
    private val unsavedTreatment = Treatment.unsavedValidInstance(client.id!!)
    private val treatmentNumber1 = unsavedTreatment.copy(number = 1)
    private val treatmentNumber2 = unsavedTreatment.copy(number = 2)
    private val treatmentNumber3 = unsavedTreatment.copy(number = 3)

    private var testee = TreatmentSpringJdbcRepository(nullJdbcx(), idGenerator)

    @BeforeMethod
    fun setUp() {
        idGenerator = SequencedTestableIdGenerator()
        testee = TreatmentSpringJdbcRepository(jdbcx(), idGenerator)


        ClientSpringJdbcRepository(jdbcx(), object : IdGenerator {
            override fun generate() = client.id!!
        }).insert(client.copy(id = null))
    }

    // --------------------------------------------------------------------------- insert

    fun insert_sunshine() {
        val expectedSaved = unsavedTreatment.copy(id = "1")
        assertThat(testee.insert(unsavedTreatment), equalTo(expectedSaved))
        assertThat(jdbcx().query("SELECT * FROM treatment", Treatment.ROW_MAPPER), contains(expectedSaved))
    }

    fun insert_notExistingClient_failBecauseOfForeignKeyReferenceViolation() {
        expect(type = PersistenceException::class,
                causedByType = DataIntegrityViolationException::class,
                action = {
                    testee.insert(unsavedTreatment.copy(clientId = "this client has not been persisted yet"))
                })

    }

    fun insert_yetPersistedTreatment_fail() {
        Expects.expectPersistenceException(PersistenceErrorCode.EXPECTED_NOT_YET_PERSISTED, {
            testee.insert(unsavedTreatment.copy(id = "already_got_an_id"))
        })
    }

    // --------------------------------------------------------------------------- find

    @Test(dependsOnMethods = arrayOf("insert_sunshine"))
    fun findAll_sunshine() {
        val saved = insertTreatment()

        assertThat(testee.findAllFor(client), contains(saved))
    }

    fun findAll_notYetPersistedClient_fail() {
        Expects.expectPersistenceException(PersistenceErrorCode.EXPECTED_YET_PERSISTED, {
            testee.findAllFor(unsavedClient)
        })
    }

    // --------------------------------------------------------------------------- update

    @Test(dependsOnMethods = arrayOf("insert_sunshine", "findAll_sunshine"))
    fun `update sunshine`() {
        val saved = insertTreatment()
        val updated = saved.copy(note = "new note")

        testee.update(updated)

        assertThat(testee.findAllFor(client), contains(updated))
    }

    fun `update unsaved treatment fails`() {
        Expects.expectPersistenceException(PersistenceErrorCode.EXPECTED_YET_PERSISTED, {
            testee.update(unsavedTreatment)
        })
    }

    @Test(dependsOnMethods = arrayOf("insert_sunshine", "findAll_sunshine"))
    fun `update does not change client ID or created`() {
        val saved = insertTreatment()
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
        val saved = insertTreatment()

        testee.delete(saved)
        assertThat(testee.findAllFor(client), empty())
    }

    fun delete_notYetPersisted_fail() {
        Expects.expectPersistenceException(PersistenceErrorCode.EXPECTED_YET_PERSISTED, {
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

    // --------------------------------------------------------------------------- recalc number

    fun `recalculateNumbers, none existing, does nothing`() {
        testee.recalculateNumbers(unsavedTreatment)
    }

    fun `recalculateNumbers, 1 and 3 exists, on recalc for 2, then 3 should be updated to 2`() {
        testee.insert(treatmentNumber1)
        testee.insert(treatmentNumber3)

        testee.recalculateNumbers(treatmentNumber2)

        val actual = testee.findAllFor(client)
        assertThat(actual.map { it.number }, containsInAnyOrder(1, 2))
    }

    // --------------------------------------------------------------------------- count

    fun `count, no treatment existing, then 0`() {
        assertThat(testee.countAllFor(client), equalTo(0))
    }

    fun `count, one treatment inserted, then 1`() {
        insertTreatment()
        assertThat(testee.countAllFor(client), equalTo(1))
    }

    fun `count, two treatments inserted, then 2`() {
        testee.insert(unsavedTreatment)
        testee.insert(unsavedTreatment)

        assertThat(testee.countAllFor(client), equalTo(2))
        assertThat(testee.countAllFor(client2), equalTo(0))
    }

    // --------------------------------------------------------------------------- private infra


    private fun insertTreatment(treatmentToInsert: Treatment = unsavedTreatment): Treatment {
        return testee.insert(treatmentToInsert)
    }

}
