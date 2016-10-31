package at.cpickl.gadsu.treatment

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.annotations.Test

@Test
class DynTreatmentFactoryTest {

    fun `managersForAllExcept given empty except should return all dyn treatments managers`() {
        assertThat(DynTreatmentFactory.managersForAllExcept(emptyList()),
                equalTo(listOf(HaraDiagnosisManager, BloodPressureManager)))
    }

    fun `managersForAllExcept given hara as except should return all except hara manager`() {
        val except = listOf<DynTreatment>(HaraDiagnosis.insertPrototype())
        assertThat(DynTreatmentFactory.managersForAllExcept(except),
                allOf(
                        not(contains<DynTreatmentManager>(HaraDiagnosisManager)),
                        hasSize(DynTreatmentFactory.all.size - except.size)
                )
        )
    }

}
