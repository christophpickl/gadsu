package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisManager
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.annotations.Test

@Test
class DynTreatmentFactoryTest {

    fun `managersForAllExcept given empty except should return all dyn treatments managers`() {
        assertThat(
                DynTreatmentFactory.managersForAllExcept(emptyList()),
                equalTo(DynTreatmentFactory.all))
    }

    fun `managersForAllExcept given all except should return empty managers`() {
        assertThat(
                DynTreatmentFactory.managersForAllExcept(DynTreatmentFactory.all.map { it.create() }),
                empty())
    }

    fun `managersForAllExcept given hara as except should return all except hara manager`() {
        val except = listOf<DynTreatment>(HaraDiagnosis.insertPrototype())
        assertThat(
                DynTreatmentFactory.managersForAllExcept(except),
                allOf(
                        not(contains<DynTreatmentManager>(HaraDiagnosisManager)),
                        hasSize(DynTreatmentFactory.all.size - except.size)
                )
        )
    }

}
