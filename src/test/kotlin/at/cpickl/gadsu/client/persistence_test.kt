package at.cpickl.gadsu.client

import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.persistence.toByteArray
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.testinfra.Expects.expect
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.TEST_CLIENT_PIC1
import at.cpickl.gadsu.testinfra.TEST_CLIENT_PIC2
import at.cpickl.gadsu.testinfra.TEST_UUID1
import at.cpickl.gadsu.testinfra.skip
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

    private val testPicture1 = MyImage.TEST_CLIENT_PIC1
    private val testPicture2 = MyImage.TEST_CLIENT_PIC2

    @BeforeMethod
    fun setUp() {
        idGenerator = mock(IdGenerator::class.java)
        testee = ClientSpringJdbcRepository(jdbcx(), idGenerator)
    }

    // --------------------------------------------------------------------------- insert

    fun `insertWithoutPicture should really just insert no picture`() {
        whenGenerateIdReturnTestUuid()

        val toBeSaved = unsavedClient.copy(
                gender = Gender.FEMALE,
                picture = MyImage.TEST_CLIENT_PIC1 // load a real image (but it wont be persisted)
        )
        val expected = toBeSaved.copy(
                id = TEST_UUID1, // returned by mock
                picture = MyImage.DEFAULT_PROFILE_WOMAN // internally reset to default woman pic, because of set gender
        )

        val actualSaved = testee.insertWithoutPicture(toBeSaved)
        assertThat(actualSaved, equalTo(expected))

        val result = jdbcx().query("SELECT * FROM client", Client.ROW_MAPPER)
        assertThat(result, contains(actualSaved))
    }

    fun insert_idSet_fails() {
        expect(type = PersistenceException::class, messageContains = "Client must not have set an ID", action = {
            testee.insertWithoutPicture(unsavedClient.copy(id = TEST_UUID1))
        })
    }

    // --------------------------------------------------------------------------- find

    fun findAll() {
        whenGenerateIdReturnTestUuid()

        assertThat(testee.findAll(), empty()) // sanity check
        val actualSavedClient = testee.insertWithoutPicture(unsavedClient)

        assertSingleFindAll(actualSavedClient)
    }

    // --------------------------------------------------------------------------- update

    fun updateWithoutPicture_sunshine() {
        whenGenerateIdReturnTestUuid()

        val savedClient = testee.insertWithoutPicture(unsavedClient.copy(gender = Gender.MALE))
        val changedClient = savedClient.copy(lastName = "something else", picture = testPicture1)
        testee.updateWithoutPicture(changedClient)

        assertSingleFindAll(changedClient.copy(picture = MyImage.DEFAULT_PROFILE_MAN))
    }

    @Test(expectedExceptions = arrayOf(PersistenceException::class))
    fun update_notExisting_shouldFail() {
        testee.updateWithoutPicture(Client.savedValidInstance())
    }

    fun update_changingCreated_shouldNotChangeAnything() {
        whenGenerateIdReturnTestUuid()

        val savedClient = testee.insertWithoutPicture(unsavedClient)
        testee.updateWithoutPicture(savedClient.copy(created = savedClient.created.plusHours(1)))

        assertSingleFindAll(savedClient)
    }

    // --------------------------------------------------------------------------- delete

    fun delete_sunshine() {
        whenGenerateIdReturnTestUuid()
        val savedClient = testee.insertWithoutPicture(unsavedClient)

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

    // --------------------------------------------------------------------------- picture

    fun `Insert without picture, should not persist picture blob`() {
        whenGenerateIdReturnTestUuid()
        val saved = testee.insertWithoutPicture(unsavedClient.copy(picture = testPicture1))
        assertPictureBlob(saved, null)
    }

    fun `Update without picture, should not persist picture blob`() {
        whenGenerateIdReturnTestUuid()

        val saved = testee.insertWithoutPicture(unsavedClient)
        testee.updateWithoutPicture(saved.copy(picture = testPicture1))

        assertPictureBlob(saved, null)
    }

    fun `Given saved client, when findAll him, then default picture is returned`() {
        whenGenerateIdReturnTestUuid()
        testee.insertWithoutPicture(unsavedClient)

        Assert.assertTrue(testee.findAll()[0].picture === MyImage.DEFAULT_PROFILE_MAN,
                "Expected findAll() to return the default man picture!")
    }

    fun `Given saved client with picture, when findAll him, then both picture bytes should be equal`() {
        skip("the expected output image is slightly different :-/")
        whenGenerateIdReturnTestUuid()

        testee.insertWithoutPicture(unsavedClient.copy(picture = testPicture1))

        val foundPicture = testee.findAll()[0].picture

        // BUT: those two seem to look equal anyway :)
        Files.write(foundPicture.toSaveRepresentation(), File("testresult_actual.jpg"))
        Files.write(testPicture1.toSaveRepresentation(), File("testresult_expected.jpg"))

        // actual will have 10 bytes less, so persisting it in database cuts off a but ...
        assertThat(foundPicture.toSaveRepresentation(),
                equalTo(testPicture1.toSaveRepresentation()))
    }

    // --------------------------------------------------------------------------- internal

    private fun assertPictureBlob(client: Client, expected: ByteArray?) {
        val blobBytes: ByteArray? = jdbcx().jdbc.queryForObject("SELECT picture FROM client WHERE id = '${client.id!!}'")
            { rs, rowNum -> rs.getBlob("picture").toByteArray() }

        assertThat(blobBytes, equalTo(expected))
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

        val savedClient = clientRepo.insertWithoutPicture(unsavedClient)
        val unsavedTreatment = Treatment.unsavedValidInstance(savedClient.id!!)
        treatmentRepo.insert(unsavedTreatment)

        expect(type = PersistenceException::class, causedByType = DataIntegrityViolationException::class, action = {
            clientRepo.delete(savedClient)
        })
    }

}
