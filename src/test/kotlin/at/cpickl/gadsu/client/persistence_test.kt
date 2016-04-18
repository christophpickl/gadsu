package at.cpickl.gadsu.client

import at.cpickl.gadsu.PersistenceException
import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.testinfra.Expects.expect
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.TEST_UUID
import at.cpickl.gadsu.testinfra.testProfilePicture1
import at.cpickl.gadsu.testinfra.testProfilePicture2
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentSpringJdbcRepository
import at.cpickl.gadsu.treatment.unsavedValidInstance
import com.google.common.io.Files
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.dao.DataIntegrityViolationException
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.File

@Test(groups = arrayOf("hsqldb"))
class ClientSpringJdbcRepositoryTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    private var testee = ClientSpringJdbcRepository(nullJdbcx(), idGenerator)


    @BeforeMethod
    fun setUp() {
        idGenerator = mock(IdGenerator::class.java)
        testee = ClientSpringJdbcRepository(jdbcx(), idGenerator)
    }

    // --------------------------------------------------------------------------- insert

    fun insert() {
        whenGenerateIdReturnTestUuid()

        val actualSaved = testee.insert(unsavedClient)
        assertThat(actualSaved, equalTo(unsavedClient.copy(id = TEST_UUID)))

        val result = jdbcx().query("SELECT * FROM client", Client.ROW_MAPPER)
        assertThat(result, contains(actualSaved))
    }

    fun insert_idSet_fails() {
        expect(type = PersistenceException::class, messageContains = "Client must not have set an ID", action = {
            testee.insert(unsavedClient.copy(id = TEST_UUID))
        })
    }

    // --------------------------------------------------------------------------- find

    @Test(dependsOnMethods = arrayOf("insert"))
    fun findAll() {
        whenGenerateIdReturnTestUuid()

        assertThat(testee.findAll(), empty()) // sanity check
        val actualSavedClient = testee.insert(unsavedClient)

        assertSingleFindAll(actualSavedClient)
    }

    // --------------------------------------------------------------------------- update

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

    // --------------------------------------------------------------------------- delete

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

    // --------------------------------------------------------------------------- image

    fun `Insert image sunshine`() {
        whenGenerateIdReturnTestUuid()
        val picture = Images.testProfilePicture1()

        val saved = testee.insert(unsavedClient.copy(picture = picture))

        assertPictureBlob(saved, picture)
    }

    fun `Update image sunshine`() {
        whenGenerateIdReturnTestUuid()

        val picture1 = Images.testProfilePicture1()
        val picture2 = Images.testProfilePicture2()

        val saved = testee.insert(unsavedClient.copy(picture = picture1))
        testee.update(saved.copy(picture = picture2))

        assertPictureBlob(saved, picture2)
    }

    fun `Given saved client, when findAll him, then default picture is returned`() {
        whenGenerateIdReturnTestUuid()
        testee.insert(unsavedClient)

        Assert.assertTrue(testee.findAll()[0].picture === Images.DEFAULT_PROFILE_MAN,
                "Expected findAll() to return the default man picture!")
    }

    fun `Given saved client with picture, when findAll him, then both picture bytes should be equal`() {
        whenGenerateIdReturnTestUuid()
        val picture1 = Images.testProfilePicture1()

        testee.insert(unsavedClient.copy(picture = picture1))

        val foundPicture = testee.findAll()[0].picture

        // BUT: those two seem to look equal anyway :)
        Files.write(foundPicture.toSaveRepresentation(), File("testresult_actual.jpg"))
        Files.write(picture1.toSaveRepresentation(), File("testresult_expected.jpg"))

        // actual will have 10 bytes less, so persisting it in database cuts off a but ...
        assertThat(foundPicture.toSaveRepresentation(),
                equalTo(picture1.toSaveRepresentation()))
    }

    // --------------------------------------------------------------------------- internal

    private fun assertPictureBlob(client: Client, expected: MyImage) {
        val blobBytes = jdbcx().jdbc.queryForObject("SELECT picture FROM client WHERE id = '${client.id!!}'")
            { rs, rowNum -> rs.getBlob("picture").toByteArray() }

        assertThat(blobBytes, equalTo(expected.toSaveRepresentation()!!))
    }

    private fun assertSingleFindAll(expected: Client) {
        val found = testee.findAll()
        assertThat(found, contains(expected))
    }

}


/**
 * Compound test.
 */
@Test(groups = arrayOf("hsqldb", "integration"))
class ClientAndTreatmentSpringJdbcRepositoryTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    private var clientRepo = ClientSpringJdbcRepository(nullJdbcx(), idGenerator)
    private var treatmentRepo = TreatmentSpringJdbcRepository(nullJdbcx(), idGenerator)

    @BeforeMethod
    fun setUp() {
        idGenerator = mock(IdGenerator::class.java)
        clientRepo = ClientSpringJdbcRepository(jdbcx(), idGenerator)
        treatmentRepo = TreatmentSpringJdbcRepository(jdbcx(), idGenerator)
    }

    fun deleteClientWithSomeTreatments_repositoryWillFailAsMustBeDoneViaServiceInstead() {
        `when`(idGenerator.generate()).thenReturn("1").thenReturn("2")

        val savedClient = clientRepo.insert(unsavedClient)
        val unsavedTreatment = Treatment.unsavedValidInstance(savedClient.id!!)
        treatmentRepo.insert(unsavedTreatment, savedClient)

        expect(type = PersistenceException::class, causedByType = DataIntegrityViolationException::class, action = {
            clientRepo.delete(savedClient)
        })
    }

}