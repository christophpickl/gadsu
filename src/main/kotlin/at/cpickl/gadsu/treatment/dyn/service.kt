package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.treatment.Treatment
import javax.inject.Inject

interface DynTreatmentService {
    fun deleteAllFor(treatment: Treatment)
    fun insert(treatment: Treatment)
    fun findAllFor(treatmentId: String): List<DynTreatment>
}

class DynTreatmentServiceImpl @Inject constructor(
        private val haraDiagnosisRepository: HaraDiagnosisRepository
) : DynTreatmentService {

    override fun findAllFor(treatmentId: String): List<DynTreatment> {
        val dynTreatments = mutableListOf<DynTreatment>()

        val hara = haraDiagnosisRepository.find(treatmentId)
        if (hara != null) dynTreatments.add(hara)
        // FIXME do it compile time safe!
        return dynTreatments
    }

    override fun insert(treatment: Treatment) {
        treatment.dynTreatments.forEach {
            it.call(object : DynTreatmentCallback<Unit> {
                override fun onHaraDiagnosis(haraDiagnosis: HaraDiagnosis) {
                    haraDiagnosisRepository.insert(treatment.id!!, haraDiagnosis)
                }

                override fun onBloodPressure(bloodPressure: BloodPressure) {
                }

                override fun onTongueDiagnosis(tongueDiagnosis: TongueDiagnosis) {
                }
            })
        }
    }

    override fun deleteAllFor(treatment: Treatment) {
        treatment.dynTreatments.forEach {
            it.call(object : DynTreatmentCallback<Unit> {
                override fun onHaraDiagnosis(haraDiagnosis: HaraDiagnosis) {
                    haraDiagnosisRepository.delete(treatment.id!!)
                }

                override fun onBloodPressure(bloodPressure: BloodPressure) {
                }

                override fun onTongueDiagnosis(tongueDiagnosis: TongueDiagnosis) {
                }
            })
        }
    }
}
