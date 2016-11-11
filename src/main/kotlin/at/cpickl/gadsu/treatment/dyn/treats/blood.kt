package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.WEIGHT_BLOOD
import javax.swing.JComponent
import javax.swing.JLabel


// MODEL
// =====================================================================================================================

private val BLOOD_TITLE = "Blutdruck"

data class BloodPressureMeasurement(
        val systolic: Int,
        val diastolic: Int,
        val frequency: Int
)

data class BloodPressure(
        val before: BloodPressureMeasurement?,
        val after: BloodPressureMeasurement?
) : DynTreatment {
    companion object {
        fun insertPrototype() = BloodPressure(null, null)
    }

    override val title: String get() = BLOOD_TITLE
    override val tabLocationWeight: Int get() = WEIGHT_BLOOD

    override fun <T> call(back: DynTreatmentCallback<T>): T {
        return back.onBloodPressure(this)
    }
}

object BloodPressureManager : DynTreatmentManager {
    override val title: String get() = BLOOD_TITLE

    override fun dynTreatmentType() = BloodPressure::class.java

    override fun create() = BloodPressure.Companion.insertPrototype()
}


// PERSISTENCE
// =====================================================================================================================


// VIEW
// =====================================================================================================================

class BloodPressureRenderer(private val bloodPressure: BloodPressure) : DynTreatmentRenderer {
    override val dynTreatment: DynTreatment get() = bloodPressure
    override val view: JComponent get() = JLabel("bloooood")
    override fun readDynTreatment() = BloodPressure(null, null)
}
