package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.tcm.model.Meridian

interface DynTreatmentCallback<T> {
    fun onHaraDiagnosis(haraDiagnosis: HaraDiagnosis): T
    fun onBloodPressure(bloodPressure: BloodPressure): T
    fun onTongueDiagnosis(tongueDiagnosis: TongueDiagnosis): T
}

interface DynTreatment {
    /** same as of DynTreatmentManager.title */
    val title: String
    /** in order to calculate location (index) in tab bar, so its always the same*/
    val tabLocationWeight: Int

    fun <T> call(back: DynTreatmentCallback<T>): T
}

interface DynTreatmentManager {
    /** same as of DynTreatment.title */
    val title: String

    fun matches(dynTreatments: List<DynTreatment>): Boolean {
        return dynTreatments.firstOrNull { it.javaClass == dynTreatmentType() } != null
    }

    fun create(): DynTreatment

    /*pseudo internal*/fun dynTreatmentType(): Class<out DynTreatment>

}

object DynTreatmentFactory {
    val all: List<DynTreatmentManager> = listOf(
            HaraDiagnosisManager,
            BloodPressureManager,
            TongueDiagnosisManager
            // ...
            // register new dyn treatments here!
            // ...
    )

    fun managersForAllExcept(except: List<DynTreatment>): List<DynTreatmentManager> {
        return all.filter { !it.matches(except) }
    }
}

private val WEIGHT_HARA = 100
private val WEIGHT_BLOOD = 200
private val WEIGHT_TONGUE = 300

// HARA
// =====================================================================================================================

private val HARA_TITLE = "Hara Diagnose"

data class HaraDiagnosis(
        val kyos: List<Meridian>,
        val jitsus: List<Meridian>,
        val bestConnection: Pair<Meridian, Meridian>?,
        val note: String
) : DynTreatment {
    companion object {

        fun insertPrototype() = HaraDiagnosis(emptyList(), emptyList(), null, "")
    }
    override val title: String get() = HARA_TITLE
    override val tabLocationWeight: Int get() = WEIGHT_HARA

    override fun <T> call(back: DynTreatmentCallback<T>): T {
        return back.onHaraDiagnosis(this)
    }

}

object HaraDiagnosisManager : DynTreatmentManager {
    override val title: String get() = HARA_TITLE

    override fun dynTreatmentType() = HaraDiagnosis::class.java

    override fun create() = HaraDiagnosis.insertPrototype()
}

// BLOOD
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

    override fun create() = BloodPressure.insertPrototype()
}


// TONGUE
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

    override fun create() = TongueDiagnosis.insertPrototype()
}
