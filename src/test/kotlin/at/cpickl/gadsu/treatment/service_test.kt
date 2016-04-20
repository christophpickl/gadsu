package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.IntegrationServiceLookuper
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

        assertThat(testee().calculateNextNumber(savedClient), equalTo(1))
    }

    // delete, 1 and 3 exists, both got 1 and 2

    private fun testee() = IntegrationServiceLookuper.lookupTreatmentService(jdbcx())

}
