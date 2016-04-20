package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.IntegrationServiceLookuper
import at.cpickl.gadsu.testinfra.SequencedTestableIdGenerator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test(groups = arrayOf("hsqldb", "integration"))
class TreatmentServiceIntegrationTest : HsqldbTest() {

    private var savedClient = Client.INSERT_PROTOTYPE // just something, to avoid nullness!

    @BeforeMethod
    fun init() {
        savedClient = insertClient()
    }

    fun `calculateNextNumber, 0 treatments existing, returns 1`() {
        assertThat(testee().calculateNextNumber(savedClient), equalTo(1))
    }

    fun `calculateNextNumber, 1 treatment existing, returns 2`() {
        insertTreatment(Treatment.unsavedValidInstance(savedClient))

        assertThat(testee().calculateNextNumber(savedClient), equalTo(2))
    }

    fun `calculateNextNumber, 3 treatments existing, delete first, returns 3`() {
        val testee = IntegrationServiceLookuper.lookupTreatmentService(jdbcx = jdbcx(), idGenerator = SequencedTestableIdGenerator())

        testee.insert(Treatment.unsavedValidInstance(savedClient.copy(note = "note1")))
        val saved2 = testee.insert(Treatment.unsavedValidInstance(savedClient.copy(note = "note2")))
        testee.insert(Treatment.unsavedValidInstance(savedClient.copy(note = "note3")))

        testee.delete(saved2)

        assertThat(testee.calculateNextNumber(savedClient), equalTo(3))
    }

    // delete, 1 and 3 exists, both got 1 and 2

    private fun testee() = IntegrationServiceLookuper.lookupTreatmentService(jdbcx())

}
