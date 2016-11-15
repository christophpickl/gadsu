package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.IntegrationServiceLookuper
import at.cpickl.gadsu.testinfra.SequencedTestableIdGenerator
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureJdbcRepository
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureMeasurement
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisJdbcRepository
import at.cpickl.gadsu.treatment.dyn.treats.MeridianAndPosition
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosisJdbcRepository
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

    //<editor-fold desc="calculateNextNumber">

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

    //</editor-fold>

    //<editor-fold desc="dynTreatment">

    fun `dynTreatment insert treatment with hara should return hara`() {
        val hara = HaraDiagnosis(
                kyos = listOf(MeridianAndPosition.LungLeft, MeridianAndPosition.Spleen),
                jitsus = listOf(MeridianAndPosition.LargeIntestineLeft, MeridianAndPosition.SmallIntestineLeft),
                bestConnection = Pair(MeridianAndPosition.LungLeft, MeridianAndPosition.LargeIntestineLeft),
                note = "testNote")

        val savedTreatment = createAndSaveTreatment(hara)

        assertThat(HaraDiagnosisJdbcRepository(jdbcx).find(savedTreatment.id!!), equalTo(hara))
    }

    fun `dynTreatment insert treatment with tongue should return tongue`() {
        val tongue = TongueDiagnosis(note = "foobar")

        val savedTreatment = createAndSaveTreatment(tongue)

        assertThat(TongueDiagnosisJdbcRepository(jdbcx).find(savedTreatment.id!!), equalTo(tongue))
    }

    fun `dynTreatment insert treatment with blood should return blood`() {
        val blood = BloodPressure(
                BloodPressureMeasurement(80, 120, 60),
                BloodPressureMeasurement(70, 110, 50)
        )

        val savedTreatment = createAndSaveTreatment(blood)

        assertThat(BloodPressureJdbcRepository(jdbcx).find(savedTreatment.id!!), equalTo(blood))
    }

    private fun createAndSaveTreatment(withDynTreatment: DynTreatment): Treatment {
        val treatment = Treatment.unsavedValidInstance(savedClient).copy(dynTreatments = mutableListOf(withDynTreatment))
        val testee = testee()

        return testee.insert(treatment)
    }

    // fun `dynTreatment update`() {
//    @Test(dependsOnMethods = arrayOf("dynTreatment insert"))
//    fun `dynTreatment delete`() {
//
//    }

    //</editor-fold>

    private fun testee() = IntegrationServiceLookuper.lookupTreatmentService(jdbcx)

}
