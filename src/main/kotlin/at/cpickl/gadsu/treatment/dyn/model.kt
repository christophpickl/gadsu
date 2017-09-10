package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureManager
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisManager
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosisManager
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosisManager

interface DynTreatmentCallback<T> {
    fun onHaraDiagnosis(haraDiagnosis: HaraDiagnosis): T
    fun onTongueDiagnosis(tongueDiagnosis: TongueDiagnosis): T
    fun onPulseDiagnosis(pulseDiagnosis: PulseDiagnosis): T
    fun onBloodPressure(bloodPressure: BloodPressure): T
}

interface DynTreatmentsCallback<T> {
    fun onHaraDiagnosis(): T
    fun onTongueDiagnosis(): T
    fun onPulseDiagnosis(): T
    fun onBloodPressure(): T
}

private var dynTreatmentsOrderCounter: Int = 0
enum class DynTreatments {
    // watch out: order here is of relevance!
    HARA {
        override val dynTreatmentType: Class<out DynTreatment> get() = HaraDiagnosis::class.java
        override fun <T> call(back: DynTreatmentsCallback<T>): T = back.onHaraDiagnosis()
    },
    TONGUE {
        override val dynTreatmentType: Class<out DynTreatment> get() = TongueDiagnosis::class.java
        override fun <T> call(back: DynTreatmentsCallback<T>): T = back.onTongueDiagnosis()
    },
    PULSE {
        override val dynTreatmentType: Class<out DynTreatment> get() = PulseDiagnosis::class.java
        override fun <T> call(back: DynTreatmentsCallback<T>): T = back.onPulseDiagnosis()
    },
    BLOOD {
        override val dynTreatmentType: Class<out DynTreatment> get() = BloodPressure::class.java
        override fun <T> call(back: DynTreatmentsCallback<T>): T = back.onBloodPressure()
    }
    ;

    val order: Int

    abstract val dynTreatmentType: Class<out DynTreatment>

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
                override fun onTongueDiagnosis() { tmp.put(TongueDiagnosis::class.java, DynTreatments.TONGUE) }
                override fun onPulseDiagnosis() { tmp.put(PulseDiagnosis::class.java, DynTreatments.PULSE) }
                override fun onBloodPressure() { tmp.put(BloodPressure::class.java, DynTreatments.BLOOD) }
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

    fun matches(dynTreatments: List<Class<out DynTreatment>>): Boolean {
        return dynTreatments.firstOrNull { it == dynTreatmentType() } != null
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
                override fun onTongueDiagnosis() { tmp.add(TongueDiagnosisManager) }
                override fun onPulseDiagnosis() { tmp.add(PulseDiagnosisManager) }
                override fun onBloodPressure() { tmp.add(BloodPressureManager) }
            })
        }
        all = tmp
    }
    val size = all.size

    fun managersForAllExcept(except: List<Class<out DynTreatment>>): List<DynTreatmentManager> {
        return all.filter { !it.matches(except) }
    }

}
