package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.treatment.BloodPressure
import at.cpickl.gadsu.treatment.DynTreatment
import at.cpickl.gadsu.treatment.HaraDiagnosis
import at.cpickl.gadsu.treatment.TongueDiagnosis
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.HashMap

@Test
class DynTreatmentTabbedPaneTest {

    private val dynTreat1Hara = HaraDiagnosis.insertPrototype()
    private val dynTreat2Blood = BloodPressure.insertPrototype()
    private val dynTreat3Tongue = TongueDiagnosis.insertPrototype()
    private var tabs = DynTreatmentTabbedPane()

    @BeforeMethod fun init() {
        tabs = DynTreatmentTabbedPane()
    }

    fun `calcTabIndex given empty index should return 1`() {
        assertThat(tabs.calcTabIndex(dynTreat1Hara), equalTo(1))
    }

    fun `calcTabIndex given index with bigger treat should return 1`() {
        tabs.index.put(1, dynTreat3Tongue)
        assertThat(tabs.calcTabIndex(dynTreat1Hara), equalTo(1))
    }

    fun `calcTabIndex given index with lower treat should return 2`() {
        tabs.index.put(1, dynTreat1Hara)
        assertThat(tabs.calcTabIndex(dynTreat2Blood), equalTo(2))
    }

    fun `calcTabIndex given index with lower and bigger treat should return 2`() {
        tabs.index.put(1, dynTreat1Hara)
        tabs.index.put(2, dynTreat3Tongue)
        assertThat(tabs.calcTabIndex(dynTreat2Blood), equalTo(2))
    }

    fun `recalcDynTreatmentsIndicesForDelete`() {
        tabs.index.put(1, dynTreat1Hara)
        tabs.index.put(2, dynTreat2Blood)
        tabs.index.put(3, dynTreat3Tongue)
        tabs.recalcDynTreatmentsIndicesForDelete(2)
        assertThat(tabs.index, allOf<HashMap<Int, DynTreatment>>(
                hasEntry<Int, DynTreatment>(1, dynTreat1Hara),
                hasEntry<Int, DynTreatment>(2, dynTreat3Tongue)
        ))
    }

    fun `recalcDynTreatmentsIndicesForAdd`() {
        tabs.index.put(1, dynTreat1Hara)
        tabs.index.put(2, dynTreat3Tongue)
        tabs.recalcDynTreatmentsIndicesForAdd(2, dynTreat2Blood)
        assertThat(tabs.index, allOf<HashMap<Int, DynTreatment>>(
                hasEntry<Int, DynTreatment>(1, dynTreat1Hara),
                hasEntry<Int, DynTreatment>(2, dynTreat2Blood),
                hasEntry<Int, DynTreatment>(3, dynTreat3Tongue)
        ))
    }

}
