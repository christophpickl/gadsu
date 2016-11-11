package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.WEIGHT_TONGUE
import javax.swing.JComponent
import javax.swing.JLabel

// MODEL
// =====================================================================================================================

private val TITLE_TONGUE = "Zungendiagnose"

data class TongueDiagnosis(
        // TODO enum opts
        val note: String
) : DynTreatment {
    companion object {
        fun insertPrototype() = TongueDiagnosis("")
    }

    override val title: String get() = TITLE_TONGUE
    override val tabLocationWeight: Int get() = WEIGHT_TONGUE

    override fun <T> call(back: DynTreatmentCallback<T>): T {
        return back.onTongueDiagnosis(this)
    }
}

object TongueDiagnosisManager : DynTreatmentManager {
    override val title: String get() = TITLE_TONGUE

    override fun dynTreatmentType() = TongueDiagnosis::class.java

    override fun create() = TongueDiagnosis.Companion.insertPrototype()
}

// PERSISTENCE
// =====================================================================================================================


// VIEW
// =====================================================================================================================

class TongueDiagnosisRenderer(private val tongueDiagnosis: TongueDiagnosis) : DynTreatmentRenderer {
    override val dynTreatment: DynTreatment get() = tongueDiagnosis
    override val view: JComponent get() = JLabel("tonguey tong")
    override fun readDynTreatment() = TongueDiagnosis("tongue noteeee")
}

