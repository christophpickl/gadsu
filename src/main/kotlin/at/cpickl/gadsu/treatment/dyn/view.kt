package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureRenderer
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisRenderer
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosisRenderer
import at.cpickl.gadsu.view.logic.ChangeAware
import com.google.common.annotations.VisibleForTesting
import java.util.HashMap
import javax.swing.JComponent
import javax.swing.JTabbedPane


interface DynTreatmentRenderer {

    /** will be reset after save */
    var originalDynTreatment: DynTreatment
    val view: JComponent

    fun readDynTreatment(): DynTreatment

    fun registerOnChange(changeListener: () -> Unit)
    fun isModified(): Boolean {
        return originalDynTreatment != readDynTreatment()
    }

}

@VisibleForTesting class DynTreatmentTabbedPane(private var originalTreatment: Treatment) : JTabbedPane(), ChangeAware {

    private val log = LOG(javaClass)
    @VisibleForTesting var index = HashMap<Int, DynTreatmentRenderer>()

    private lateinit var lateChangeListener: () -> Unit
    override fun onChange(changeListener: () -> Unit) {
        this.lateChangeListener = changeListener
    }


    fun addDynTreatment(dynTreatment: DynTreatment) {
        val addIndex = calcTabIndex(dynTreatment)
        log.trace("addDynTreatment(dynTreatment) .. calced index: $addIndex")

        val renderer = dynTreatment.call(object : DynTreatmentCallback<DynTreatmentRenderer> {
            override fun onHaraDiagnosis(haraDiagnosis: HaraDiagnosis) = HaraDiagnosisRenderer(haraDiagnosis)
            override fun onBloodPressure(bloodPressure: BloodPressure) = BloodPressureRenderer(bloodPressure)
            override fun onTongueDiagnosis(tongueDiagnosis: TongueDiagnosis) = TongueDiagnosisRenderer(tongueDiagnosis)
        })

        insertTab(dynTreatment.title, null, renderer.view, null, addIndex)
        selectedIndex = addIndex
        recalcDynTreatmentsIndicesForAddAndAddIt(addIndex, renderer)

        renderer.registerOnChange(lateChangeListener)
        lateChangeListener()
    }

    fun getDynTreatmentAt(tabIndex: Int): DynTreatment {
        log.trace("getDynTreatmentAt(tabIndex=$tabIndex)")
        return index[tabIndex]!!.originalDynTreatment
    }

    fun removeDynTreatmentAt(tabIndex: Int) {
        log.trace("removeDynTreatmentAt(tabIndex=$tabIndex)")
        removeTabAt(tabIndex)
        index.remove(tabIndex)
        recalcDynTreatmentsIndicesForDelete(tabIndex)
        lateChangeListener()
    }

    fun readDynTreatments(): MutableList<DynTreatment> {
        return index.values.map { it.readDynTreatment() }.toMutableList()
    }

    @VisibleForTesting fun calcTabIndex(toAdd: DynTreatment): Int {
        var currentIndex = 1
        for (renderer in index.values) {
            if (DynTreatmentsFactory.dynTreatmentsFor(toAdd).order <
                    DynTreatmentsFactory.dynTreatmentsFor(renderer.originalDynTreatment).order) {
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

    fun isModified(): Boolean {
        return originalTreatment.areDynTreatmentsModified(index.values) ||
                index.values.any { it.isModified() }
    }

    fun wasSaved(newTreatment: Treatment) {
        originalTreatment = newTreatment
        index.values.forEach {
            it.originalDynTreatment = newTreatment.dynTreatmentByType(it.originalDynTreatment.javaClass)
        }
    }

    private fun Treatment.dynTreatmentByType(dynTreatment: Class<DynTreatment>): DynTreatment {
        return this.dynTreatments.first { it.javaClass == dynTreatment }
    }

}

@VisibleForTesting fun Treatment.areDynTreatmentsModified(renderers: Collection<DynTreatmentRenderer>): Boolean {
    val xx = dynTreatments.map { it.javaClass }.sortedBy { it.name }
    val yy = renderers.map { it.originalDynTreatment.javaClass }.sortedBy { it.name }
    val zz = xx != yy
    return zz
//    return dynTreatments.map { it.javaClass }.sortedBy { it.name } !=
//            renderers.map { it.originalDynTreatment.javaClass }.sortedBy { it.name }
}
