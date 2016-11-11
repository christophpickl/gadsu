package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.BloodPressureRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentTabbedPane
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisRenderer
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.TongueDiagnosisRenderer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.HashMap

@Test
class DynTreatmentTabbedPaneTest {

    private val dynTreat1Hara = HaraDiagnosis.insertPrototype()
    private val dynTreat1HaraRenderer = HaraDiagnosisRenderer(dynTreat1Hara)
    private val dynTreat2Blood = BloodPressure.insertPrototype()
    private val dynTreat2BloodRenderer = BloodPressureRenderer(dynTreat2Blood)
    private val dynTreat3Tongue = TongueDiagnosis.insertPrototype()
    private val dynTreat3TongueRenderer = TongueDiagnosisRenderer(dynTreat3Tongue)
    private var tabs = DynTreatmentTabbedPane()

    @BeforeMethod fun init() {
        tabs = DynTreatmentTabbedPane()
    }

    fun `calcTabIndex given empty index should return 1`() {
        assertThat(tabs.calcTabIndex(dynTreat1Hara), equalTo(1))
    }

    fun `calcTabIndex given index with bigger treat should return 1`() {
        tabs.index.put(1, dynTreat3TongueRenderer)
        assertThat(tabs.calcTabIndex(dynTreat1Hara), equalTo(1))
    }

    fun `calcTabIndex given index with lower treat should return 2`() {
        tabs.index.put(1, dynTreat1HaraRenderer)
        assertThat(tabs.calcTabIndex(dynTreat2Blood), equalTo(2))
    }

    fun `calcTabIndex given index with lower and bigger treat should return 2`() {
        tabs.index.put(1, dynTreat1HaraRenderer)
        tabs.index.put(2, dynTreat3TongueRenderer)
        assertThat(tabs.calcTabIndex(dynTreat2Blood), equalTo(2))
    }

    fun `recalcDynTreatmentsIndicesForDelete`() {
        tabs.index.put(1, dynTreat1HaraRenderer)
        tabs.index.put(2, dynTreat2BloodRenderer)
        tabs.index.put(3, dynTreat3TongueRenderer)
        tabs.recalcDynTreatmentsIndicesForDelete(2)
        assertThat(tabs.index, allOf<HashMap<Int, DynTreatmentRenderer>>(
                hasEntry<Int, DynTreatmentRenderer>(1, dynTreat1HaraRenderer),
                hasEntry<Int, DynTreatmentRenderer>(2, dynTreat3TongueRenderer)
        ))
    }

    fun `recalcDynTreatmentsIndicesForAdd`() {
        tabs.index.put(1, dynTreat1HaraRenderer)
        tabs.index.put(2, dynTreat3TongueRenderer)
        tabs.recalcDynTreatmentsIndicesForAddAndAddIt(2, dynTreat2BloodRenderer)
        assertThat(tabs.index, allOf<HashMap<Int, DynTreatmentRenderer>>(
                hasEntry<Int, DynTreatmentRenderer>(1, dynTreat1HaraRenderer),
                hasEntry<Int, DynTreatmentRenderer>(2, dynTreat2BloodRenderer),
                hasEntry<Int, DynTreatmentRenderer>(3, dynTreat3TongueRenderer)
        ))
    }

}
