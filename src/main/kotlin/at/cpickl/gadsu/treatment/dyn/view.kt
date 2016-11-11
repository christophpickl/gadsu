package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisRenderer
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import com.google.common.annotations.VisibleForTesting
import java.util.HashMap
import javax.swing.JComponent
import javax.swing.JTabbedPane


interface DynTreatmentRenderer {
    val dynTreatment: DynTreatment
    val view: JComponent

    fun readDynTreatment(): DynTreatment
}


@VisibleForTesting class DynTreatmentTabbedPane : JTabbedPane() {

    private val log = LOG(javaClass)

    @VisibleForTesting var index = HashMap<Int, DynTreatmentRenderer>()

    fun addDynTreatment(dynTreatment: DynTreatment) {
        val addIndex = calcTabIndex(dynTreatment)
        log.trace("addDynTreatment(dynTreatment) .. calced index: $addIndex")

        val renderer = dynTreatment.call(object : DynTreatmentCallback<DynTreatmentRenderer> {
            override fun onHaraDiagnosis(haraDiagnosis: HaraDiagnosis): DynTreatmentRenderer {
                return HaraDiagnosisRenderer(haraDiagnosis)
            }

            override fun onBloodPressure(bloodPressure: BloodPressure): DynTreatmentRenderer {
                // FIXME
                throw UnsupportedOperationException("not implemented")
            }

            override fun onTongueDiagnosis(tongueDiagnosis: TongueDiagnosis): DynTreatmentRenderer {
                throw UnsupportedOperationException("not implemented")
            }
        })

        insertTab(dynTreatment.title, null, renderer.view, null, addIndex)
        selectedIndex = addIndex
        recalcDynTreatmentsIndicesForAddAndAddIt(addIndex, renderer)
    }

    fun getDynTreatmentAt(tabIndex: Int): DynTreatment {
        log.trace("getDynTreatmentAt(tabIndex=$tabIndex)")
        return index[tabIndex]!!.dynTreatment
    }

    fun removeDynTreatmentAt(tabIndex: Int) {
        log.trace("removeDynTreatmentAt(tabIndex=$tabIndex)")
        removeTabAt(tabIndex)
        index.remove(tabIndex)
        recalcDynTreatmentsIndicesForDelete(tabIndex)
    }

    fun readDynTreatments(): MutableList<DynTreatment> {
        return index.values.map { it.readDynTreatment() }.toMutableList()
    }

    @VisibleForTesting fun calcTabIndex(toAdd: DynTreatment): Int {
        var currentIndex = 1
        for (renderer in index.values) {
            if (toAdd.tabLocationWeight < renderer.dynTreatment.tabLocationWeight) {
                break
            }
            currentIndex++
        }
        return currentIndex
    }

    @VisibleForTesting fun recalcDynTreatmentsIndicesForAddAndAddIt(addIndex: Int, renderer: DynTreatmentRenderer) {
        val newIndex = HashMap<Int, DynTreatmentRenderer>()
        index.entries.forEach { entry ->
            val key = if (entry.key >= addIndex) entry.key + 1 else entry.key
            newIndex.put(key, entry.value)
        }
        newIndex.put(addIndex, renderer)
        index = newIndex
    }

    @VisibleForTesting fun recalcDynTreatmentsIndicesForDelete(removedIndex: Int) {
        val newIndex = HashMap<Int, DynTreatmentRenderer>()
        index.entries.forEach { entry ->
            val key = if (entry.key > removedIndex) entry.key - 1 else entry.key
            newIndex.put(key, entry.value)
        }
        index = newIndex
    }
}
