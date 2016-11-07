package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.IntegrationServiceLookuper
import at.cpickl.gadsu.testinfra.SequencedTestableIdGenerator
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test(groups = arrayOf("hsqldb", "integration"))
class TreatmentServiceIntegrationTest : HsqldbTest() {

    private var savedClient = Client.INSERT_PROTOTYPE // just something, to avoid nullness!

    @BeforeMethod
    fun init() {
        savedClient = insertClientViaRepo()
    }

    fun `calculateNextNumber, 0 treatments existing, returns 1`() {
        assertThat(testee().calculateNextNumber(savedClient), equalTo(1))
    }

    fun `calculateNextNumber, 1 treatment existing, returns 2`() {
        insertTreatment(Treatment.unsavedValidInstance(savedClient))

        assertThat(testee().calculateNextNumber(savedClient), equalTo(2))
    }

    fun `calculateNextNumber, 3 treatments existing, delete first, returns 3`() {
        val testee = IntegrationServiceLookuper.lookupTreatmentService(jdbcx = jdbcx, idGenerator = SequencedTestableIdGenerator())

        testee.insert(Treatment.unsavedValidInstance(savedClient).copy(number = 1))
        val saved2 = testee.insert(Treatment.unsavedValidInstance(savedClient).copy(number = 2))
        testee.insert(Treatment.unsavedValidInstance(savedClient).copy(number = 3))

        testee.delete(saved2)

        assertThat(testee.calculateNextNumber(savedClient), equalTo(4))
    }

    // delete, 1 and 3 exists, both got 1 and 2

    fun `dynTreatment insert`() {
        val testNote = "testNote"
        val hara = HaraDiagnosis.insertPrototype().copy(note = testNote)
        val treatment = Treatment.unsavedValidInstance(savedClient).copy(dynTreatments = mutableListOf(hara))
        val testee = testee()
        val savedTreatment = testee.insert(treatment)

        val actual = HaraDiagnosisJdbcRepository(jdbcx).find(savedTreatment.id!!)
        assertThat(actual, equalTo(hara))
    }

    // fun `dynTreatment update`() {
    // fun `dynTreatment delete`() {

    private fun testee() = IntegrationServiceLookuper.lookupTreatmentService(jdbcx)

}
