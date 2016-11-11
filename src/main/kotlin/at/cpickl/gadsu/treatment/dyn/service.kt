package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureRepository
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisRepository
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosisRepository
import javax.inject.Inject

interface DynTreatmentService {
    fun deleteAllFor(treatment: Treatment)
    fun insert(treatment: Treatment)
    fun findAllFor(treatmentId: String): List<DynTreatment>
}

interface RepositoryFacade {
    fun findFor(dynTreatmentType: DynTreatments, treatmentId: String): DynTreatment?
    fun insert(dynTreatment: DynTreatment, treatmentId: String)
    fun delete(dynTreatment: DynTreatment, treatmentId: String)
}

class RepositoryFacadeImpl @Inject constructor(
        private val haraDiagnosisRepository: HaraDiagnosisRepository,
        private val tongueDiagnosisRepository: TongueDiagnosisRepository,
        private val bloodPressureRepository: BloodPressureRepository
) : RepositoryFacade {
    val reposByType: Map<DynTreatments, DynTreatmentRepository<out DynTreatment>>

    init {
        val tmp = mutableMapOf<DynTreatments, DynTreatmentRepository<out DynTreatment>>()
        DynTreatments.values().forEach {
            it.call(object : DynTreatmentsCallback<Unit> {
                override fun onHaraDiagnosis() { tmp.put(it, haraDiagnosisRepository) }
                override fun onTongueDiagnosis() { tmp.put(it, tongueDiagnosisRepository) }
                override fun onBloodPressure() { tmp.put(it, bloodPressureRepository) }
            })
        }
        reposByType = tmp
    }

    override fun findFor(dynTreatmentType: DynTreatments, treatmentId: String) =
            reposByType[dynTreatmentType]!!.find(treatmentId)

    override fun insert(dynTreatment: DynTreatment, treatmentId: String) {
        // reposByType[dynTreatmentType]!!.insert(treatmentId, dynTreatment) ... NOPE, not possible as of generics
        dynTreatment.call(object : DynTreatmentCallback<Unit>{
            override fun onHaraDiagnosis(haraDiagnosis: HaraDiagnosis) {
                haraDiagnosisRepository.insert(treatmentId, dynTreatment as HaraDiagnosis)
            }
            override fun onBloodPressure(bloodPressure: BloodPressure) {
                bloodPressureRepository.insert(treatmentId, dynTreatment as BloodPressure)
            }
            override fun onTongueDiagnosis(tongueDiagnosis: TongueDiagnosis) {
                tongueDiagnosisRepository.insert(treatmentId, dynTreatment as TongueDiagnosis)
            }
        })
    }

    override fun delete(dynTreatment: DynTreatment, treatmentId: String) {
        val dynTreatmentType = DynTreatmentsFactory.dynTreatmentsFor(dynTreatment)
        reposByType[dynTreatmentType]!!.delete(treatmentId)
    }

}

class DynTreatmentServiceImpl @Inject constructor(
        private val repositoryFacade: RepositoryFacade
) : DynTreatmentService {

    override fun findAllFor(treatmentId: String): List<DynTreatment> {
        val dynTreatments = mutableListOf<DynTreatment>()

        DynTreatments.values().forEach {
            val dynTreat = repositoryFacade.findFor(it, treatmentId)
            if (dynTreat != null) dynTreatments.add(dynTreat)
        }

        return dynTreatments
    }

    override fun insert(treatment: Treatment) {
        treatment.dynTreatments.forEach {
            repositoryFacade.insert(it, treatment.id!!)
        }
    }

    override fun deleteAllFor(treatment: Treatment) {
        treatment.dynTreatments.forEach {
            repositoryFacade.delete(it, treatment.id!!)
        }
    }
}
