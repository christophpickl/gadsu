package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.persistence.toByteArray
import at.cpickl.gadsu.testinfra.Expects
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.TEST_CLIENT_PIC1
import at.cpickl.gadsu.testinfra.TEST_CLIENT_PIC2
import at.cpickl.gadsu.testinfra.TEST_UUID1
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.File

@Test(groups = arrayOf("hsqldb"))
class ClientSpringJdbcRepositoryTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    private lateinit var testee: ClientJdbcRepository

    private val testPicture1 = MyImage.TEST_CLIENT_PIC1
    private val testPicture2 = MyImage.TEST_CLIENT_PIC2

    @BeforeMethod
    fun setUp() {
        testee = ClientJdbcRepository(jdbcx, idGenerator)
    }

    // --------------------------------------------------------------------------- insert

    fun `insertWithoutPicture should really just insert no picture`() {
        val toBeSaved = Client.REAL_DUMMY.copy(id = null, gender = Gender.MALE, cprops = CProps.empty, picture = MyImage.byFile(File("src/main/resources/gadsu/images/profile_pic-real_dummy.jpg")))
        val expected = toBeSaved.copy(id = TEST_UUID1, picture = MyImage.DEFAULT_PROFILE_MAN)// ID returned by mock

        assertThat(testee.insertWithoutPicture(toBeSaved), equalTo(expected))

        val result = jdbcx.query("SELECT * FROM client", Client.ROW_MAPPER)
        assertThat(result, hasSize(1))
        val actualClient = result[0]
        assertThat(actualClient.picture, equalTo(expected.picture))
        assertThat(actualClient, equalTo(expected))
    }

    fun insert_idSet_fails() {
        Expects.expect(type = PersistenceException::class, messageContains = "must not have set an ID", action = {
            testee.insertWithoutPicture(unsavedClient.copy(id = TEST_UUID1))
        })
    }

    // --------------------------------------------------------------------------- find

    fun findAll() {
        assertThat(testee.findAll(), empty()) // sanity check
        val actualSavedClient = testee.insertWithoutPicture(unsavedClient)

        assertSingleFindAll(actualSavedClient)
    }

    // --------------------------------------------------------------------------- update

    fun updateWithoutPicture_sunshine() {
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
        val savedClient = testee.insertWithoutPicture(unsavedClient)
        testee.updateWithoutPicture(savedClient.copy(created = savedClient.created.plusHours(1)))

        assertSingleFindAll(savedClient)
    }

    // --------------------------------------------------------------------------- delete

    fun delete_sunshine() {
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
        val saved = testee.insertWithoutPicture(unsavedClient.copy(picture = testPicture1))
        assertPictureBlob(saved, null)
    }

    fun `Update without picture, should not persist picture blob`() {
        val saved = testee.insertWithoutPicture(unsavedClient)
        testee.updateWithoutPicture(saved.copy(picture = testPicture1))

        assertPictureBlob(saved, null)
    }

    fun `Given saved client, when findAll him, then default picture is returned`() {
        testee.insertWithoutPicture(unsavedClient)

        Assert.assertTrue(testee.findAll()[0].picture === MyImage.DEFAULT_PROFILE_MAN,
                "Expected findAll() to return the default man picture!")
    }

    // --------------------------------------------------------------------------- internal

    private fun assertPictureBlob(client: Client, expected: ByteArray?) {
        val blobBytes: ByteArray? = jdbcx.jdbc.queryForObject("SELECT picture FROM client WHERE id = '${client.id!!}'")
            { rs, _ -> rs.getBlob("picture").toByteArray() }

        assertThat(blobBytes, equalTo(expected))
    }

    private fun assertSingleFindAll(expected: Client) {
        val found = testee.findAll()
//        val f = found[0]
//        val r = f.equals(expected)
        assertThat(found, contains(expected))
    }

}

