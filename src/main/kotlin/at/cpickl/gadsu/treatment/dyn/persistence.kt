package at.cpickl.gadsu.treatment.dyn

interface DynTreatmentRepository<DynTreatment> {
    fun insert(treatmentId: String, hara: DynTreatment)
    fun find(treatmentId: String): DynTreatment?
    fun delete(treatmentId: String)
}
