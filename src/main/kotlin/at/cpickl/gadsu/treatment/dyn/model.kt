package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureManager
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisManager
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosisManager

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

// TODO make a enum of all dynTreats and use that for ordering instead
val WEIGHT_HARA = 100
val WEIGHT_BLOOD = 200
val WEIGHT_TONGUE = 300
