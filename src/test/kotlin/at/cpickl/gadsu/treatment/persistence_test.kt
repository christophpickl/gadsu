package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.DUMMY_CREATED
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientJdbcRepository
import at.cpickl.gadsu.persistence.PersistenceErrorCode
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.report.multiprotocol.MultiProtocol
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolJdbcRepository
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolRepository
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.testinfra.*
import at.cpickl.gadsu.testinfra.Expects.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.springframework.dao.DataIntegrityViolationException
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test(groups = arrayOf("hsqldb"))
class TreatmentJdbcRepositoryTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    private val client = Client.savedValidInstance()
    private val clientId = client.id!!
    private val client2 = Client.savedValidInstance2()
    private val unsavedTreatment = Treatment.unsavedValidInstance(clientId)
    private val treatmentNumber1 = unsavedTreatment.copy(number = 1)
    private val treatmentNumber2 = unsavedTreatment.copy(number = 2)
    private val treatmentNumber3 = unsavedTreatment.copy(number = 3)
    private lateinit var protocolRepo: MultiProtocolRepository
    private lateinit var testee: TreatmentJdbcRepository

    @BeforeMethod
    fun setUp() {
        idGenerator = SequencedTestableIdGenerator()
        testee = TreatmentJdbcRepository(jdbcx, idGenerator)
        protocolRepo = MultiProtocolJdbcRepository(jdbcx, idGenerator)


        ClientJdbcRepository(jdbcx, object : IdGenerator {
            override fun generate() = client.id!!
        }).insertWithoutPicture(client.copy(id = null))
    }

    //<editor-fold desc="insert">

    fun insert_sunshine() {
        val expectedSaved = unsavedTreatment.copy(id = "1")
        assertThat(testee.insert(unsavedTreatment), equalTo(expectedSaved))
        assertThat(jdbcx.query("SELECT * FROM treatment", Treatment.ROW_MAPPER), contains(expectedSaved))
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

    //</editor-fold>

    //<editor-fold desc="find">

    @Test(dependsOnMethods = arrayOf("insert_sunshine"))
    fun findAll_sunshine() {
        val saved = insertTreatment()

        assertThat(testee.findAllFor(client), contains(saved))
    }

    @Test(dependsOnMethods = arrayOf("insert_sunshine"))
    fun `findAllFor, returns reversed order`() {
        insertTreatment(unsavedTreatment.copy(number = 1))
        insertTreatment(unsavedTreatment.copy(number = 2))

        assertThat(testee.findAllFor(client).map { it.number }.toList(), equalTo(listOf(2, 1)))
    }

    fun findAll_notYetPersistedClient_fail() {
        Expects.expectPersistenceException(PersistenceErrorCode.EXPECTED_YET_PERSISTED, {
            testee.findAllFor(unsavedClient)
        })
    }

    fun `findFirstFor should return earliest treatment`() {
        insertTreatment(unsavedTreatment.copy(number = 1, date = TEST_DATETIME_FOR_TREATMENT_DATE.plusDays(1)))
        insertTreatment(unsavedTreatment.copy(number = 2, date = TEST_DATETIME_FOR_TREATMENT_DATE.minusDays(1)))
        insertTreatment(unsavedTreatment.copy(number = 3, date = TEST_DATETIME_FOR_TREATMENT_DATE))

        val found = testee.findFirstFor(clientId)

        assertThat(found!!.number, equalTo(2))
    }

    fun `findLastFor should return latest treatment`() {
        insertTreatment(unsavedTreatment.copy(number = 1, date = TEST_DATETIME_FOR_TREATMENT_DATE.minusDays(1)))
        insertTreatment(unsavedTreatment.copy(number = 2, date = TEST_DATETIME_FOR_TREATMENT_DATE.plusDays(1)))
        insertTreatment(unsavedTreatment.copy(number = 3, date = TEST_DATETIME_FOR_TREATMENT_DATE))

        val found = testee.findLastFor(clientId)

        assertThat(found!!.number, equalTo(2))
    }

    //</editor-fold>

    //<editor-fold desc="update">


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

    //</editor-fold>

    //<editor-fold desc="delete">

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

    //</editor-fold>

    //<editor-fold desc="calc number">

    fun `calculateMaxNumberUsed, none existing, should return null`() {
        assertCalculateNumber(null)
    }

    fun `calculateMaxNumberUsed, number 1 existing, should return 1`() {
        insertTreatment(treatmentNumber1)
        assertCalculateNumber(1)
    }

    fun `calculateMaxNumberUsed, with one gap, should return last`() {
        insertTreatment(treatmentNumber1)
        insertTreatment(treatmentNumber3)
        assertCalculateNumber(3)
    }

    private fun assertCalculateNumber(expectedNumber: Int?, givenClient: Client = client) {
        assertThat(testee.calculateMaxNumberUsed(givenClient), equalTo(expectedNumber))
    }

    //</editor-fold>

    //<editor-fold desc="count">

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

    //</editor-fold>

    //<editor-fold desc="count protocolized">

    fun `count protocolized for empty`() {
        assertThat(testee.countAllNonProtocolized(), equalTo(0))
    }

    fun `count protocolized, 0 protocolized and 1 unprotocolized should return 1`() {
        testee.insert(unsavedTreatment)

        assertThat(testee.countAllNonProtocolized(), equalTo(1))
    }

    fun `count protocolized, 1 protocolized and 2 unprotocolized should return 2`() {
        val treat1 = testee.insert(unsavedTreatment)
        insertProtocolEntryFor(treat1)
        testee.insert(unsavedTreatment)
        testee.insert(unsavedTreatment)

        assertThat(testee.countAllNonProtocolized(), equalTo(2))
    }

    private fun insertProtocolEntryFor(treatment: Treatment) {
        protocolRepo.insert(MultiProtocol(null, DUMMY_CREATED, "testDescription", listOf(treatment.id!!)))
    }

    //</editor-fold>

    // --------------------------------------------------------------------------- private infra


    private fun insertTreatment(treatmentToInsert: Treatment = unsavedTreatment): Treatment {
        return testee.insert(treatmentToInsert)
    }

}

/**
 * Compound test.
 */
@Test(groups = arrayOf("hsqldb", "integration"))
class ClientAndTreatmentSpringJdbcRepositoryTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    private lateinit var treatmentRepo: TreatmentJdbcRepository

    @BeforeMethod
    fun setUp() {
        treatmentRepo = TreatmentJdbcRepository(jdbcx, idGenerator)
    }

    fun deleteClientWithSomeTreatments_repositoryWillFailAsMustBeDoneViaServiceInstead() {
        val savedClient = insertClientViaRepo(unsavedClient)

        val unsavedTreatment = Treatment.unsavedValidInstance(savedClient.id!!)
        treatmentRepo.insert(unsavedTreatment)

        expect(type = PersistenceException::class, causedByType = DataIntegrityViolationException::class, action = {
            deleteClientViaRepo(savedClient)
        })
    }

}

