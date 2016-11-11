package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.treatment.view.TriStateCheckBox
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.annotations.VisibleForTesting
import java.util.HashMap
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTabbedPane

interface DynTreatmentRenderer {
    val dynTreatment: DynTreatment
    val view: JComponent

    fun readDynTreatment(): DynTreatment
}

class MeridianTriStateCheckBox(meridian: Meridian) : TriStateCheckBox<Meridian>(meridian)
class BloodPressureRenderer(private val bloodPressure: BloodPressure) : DynTreatmentRenderer {
    override val dynTreatment: DynTreatment get() = bloodPressure
    override val view: JComponent get() = JLabel("bloooood")
    override fun readDynTreatment() = BloodPressure(null, null)
}

class TongueDiagnosisRenderer(private val tongueDiagnosis: TongueDiagnosis) : DynTreatmentRenderer {
    override val dynTreatment: DynTreatment get() = tongueDiagnosis
    override val view: JComponent get() = JLabel("tonguey tong")
    override fun readDynTreatment() = TongueDiagnosis("tongue noteeee")
}

class HaraDiagnosisRenderer(private val haraDiagnosis: HaraDiagnosis) : DynTreatmentRenderer {

    private val checkLu = MeridianTriStateCheckBox(Meridian.Lung)
    private val checkLe = MeridianTriStateCheckBox(Meridian.Liver)
    private val allChecks = listOf(checkLu, checkLe)

    private val inpNote = MyTextArea("HaraDiagnosisRenderer.inpNote", 2)

    override val dynTreatment: DynTreatment get() = haraDiagnosis
    override val view: JComponent by lazy {
        val panel = GridPanel()
        // FIXME enable check for changes!
        panel.add(JLabel("Lu"))
        panel.c.gridx++
        panel.add(checkLu)

        panel.c.gridx = 0
        panel.c.gridy++
        panel.add(JLabel("Le"))
        panel.c.gridx++
        panel.add(checkLe)

        panel.c.gridx = 0
        panel.c.gridy++
        panel.c.gridwidth = 2
        panel.add(inpNote.scrolled())

        checkLu.isSelected = haraDiagnosis.kyos.contains(Meridian.Lung)
        checkLe.isSelected = haraDiagnosis.kyos.contains(Meridian.Liver)
        // jitsus
        inpNote.text = haraDiagnosis.note

        panel
    }

    override fun readDynTreatment(): DynTreatment {
        return HaraDiagnosis(allChecks.filter { it.isSelected }.map { it.item }, emptyList(), null, inpNote.text)
    }

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
