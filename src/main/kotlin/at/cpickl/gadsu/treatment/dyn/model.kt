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

interface DynTreatmentsCallback<T> {
    fun onHaraDiagnosis(): T
    fun onBloodPressure(): T
    fun onTongueDiagnosis(): T
}

private var dynTreatmentsOrderCounter: Int = 0
enum class DynTreatments {
    // watch out: order here is of relevance!
    HARA { override fun <T> call(back: DynTreatmentsCallback<T>): T = back.onHaraDiagnosis() },
    TONGUE { override fun <T> call(back: DynTreatmentsCallback<T>): T = back.onTongueDiagnosis() },
    BLOOD { override fun <T> call(back: DynTreatmentsCallback<T>): T = back.onBloodPressure() };

    val order: Int

    init {
        order = dynTreatmentsOrderCounter++
    }

    abstract fun <T> call(back: DynTreatmentsCallback<T>): T
}


object DynTreatmentsFactory {

    private val all: Map<Class<out DynTreatment>, DynTreatments>

    init {
        val tmp = mutableMapOf<Class<out DynTreatment>, DynTreatments>()
        DynTreatments.values().forEach {
            it.call(object : DynTreatmentsCallback<Unit> {
                override fun onHaraDiagnosis() { tmp.put(HaraDiagnosis::class.java, DynTreatments.HARA) }
                override fun onBloodPressure() { tmp.put(BloodPressure::class.java, DynTreatments.BLOOD) }
                override fun onTongueDiagnosis() { tmp.put(TongueDiagnosis::class.java, DynTreatments.TONGUE) }
            })
        }
        all = tmp
    }

    fun dynTreatmentsFor(dynTreatment: DynTreatment): DynTreatments {
        return all[dynTreatment.javaClass] ?: throw IllegalArgumentException("Unhandled: $dynTreatment")
    }
}


interface DynTreatment {
    /** same as of DynTreatmentManager.title */
    val title: String

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

    val all: List<DynTreatmentManager>

    init {
        val tmp = mutableListOf<DynTreatmentManager>()
        DynTreatments.values().forEach {
            it.call(object : DynTreatmentsCallback<Unit> {
                override fun onHaraDiagnosis() { tmp.add(HaraDiagnosisManager) }
                override fun onBloodPressure() { tmp.add(BloodPressureManager) }
                override fun onTongueDiagnosis() { tmp.add(TongueDiagnosisManager) }
            })
        }
        all = tmp
    }

    fun managersForAllExcept(except: List<DynTreatment>): List<DynTreatmentManager> {
        return all.filter { !it.matches(except) }
    }
}
