package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.tcm.model.Meridian
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
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosisJdbcRepository
import at.cpickl.gadsu.treatment.dyn.treats.PulseProperty
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosisJdbcRepository
import at.cpickl.gadsu.treatment.dyn.treats.TongueProperty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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
        val tongue = TongueDiagnosis(
                listOf(TongueProperty.Color.Pale),
                listOf(TongueProperty.Shape.Swollen),
                listOf(TongueProperty.Coat.Yellow),
                listOf(TongueProperty.Special.RedDots),
                "testNote"
        )

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

    fun `dynTreatment insert treatment with pulse diagnosis should return pulse`() {
        val pulse = PulseDiagnosis(listOf(PulseProperty.Deep), "testNote")

        val savedTreatment = createAndSaveTreatment(pulse)

        assertThat(PulseDiagnosisJdbcRepository(jdbcx).find(savedTreatment.id!!), equalTo(pulse))
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


    //<editor-fold desc="treatedMeridians">

    fun `treatedMeridians do an insert and findAllForRaw should return inserted meridians`() {
        val unsavedTreat = Treatment.unsavedValidInstance(savedClient.id!!).copy(treatedMeridians = listOf(Meridian.Lung, Meridian.Heart))

        val testee = testee()
        testee.insert(unsavedTreat)

        val actualTreats = testee.findAllFor(savedClient)
        assertThat(actualTreats, hasSize(1))
        assertThat(actualTreats[0].treatedMeridians, equalTo(unsavedTreat.treatedMeridians))
    }

    fun `treatedMeridians do an update and findAllForRaw should return updated meridians`() {
        val unsavedTreat = Treatment.unsavedValidInstance(savedClient.id!!).copy(treatedMeridians = listOf(Meridian.Lung, Meridian.Heart))

        val testee = testee()
        val savedTreat = testee.insert(unsavedTreat)
        val treat2 = savedTreat.copy(treatedMeridians = listOf(Meridian.GallBladder))

        testee.update(treat2)

        val result = testee.findAllFor(savedClient)
        assertThat(result, hasSize(1))
        assertThat(result[0].treatedMeridians, equalTo(treat2.treatedMeridians))
    }

    fun `treatedMeridians delete should remove all entries`() {
        val testee = testee()
        val treat = testee.insert(Treatment.unsavedValidInstance(savedClient.id!!).copy(treatedMeridians = listOf(Meridian.Lung)))

        testee.delete(treat)

        assertThat(TreatmentMeridiansJdbcRepository(jdbcx).find(treat.id!!), empty())
    }

    //</editor-fold>

    private fun testee() = IntegrationServiceLookuper.lookupTreatmentService(jdbcx)

}
