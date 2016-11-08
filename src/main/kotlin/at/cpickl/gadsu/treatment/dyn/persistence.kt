package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.persistence.Jdbcx
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject

interface DynTreatmentRepository<DynTreatment> {
    fun insert(treatmentId: String, hara: DynTreatment)
    fun find(treatmentId: String): DynTreatment?
    fun delete(treatmentId: String)
}

interface HaraDiagnosisRepository : DynTreatmentRepository<HaraDiagnosis> {}

class HaraDiagnosisJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx
) : HaraDiagnosisRepository {

    companion object {
        val TABLE = "hara_diagnosis"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun find(treatmentId: String): HaraDiagnosis? {
        return jdbcx.queryMaybeSingle(HaraDiagnosis.ROW_MAPPER, "SELECT * FROM $TABLE WHERE id_treatment = ?", arrayOf(treatmentId))
    }

    override fun insert(treatmentId: String, hara: HaraDiagnosis) {
        log.debug("insert(treatmentId={}, hara={})", treatmentId, hara)
        jdbcx.update("INSERT INTO $TABLE (" +
                "id_treatment, note) VALUES (?, ?)",
                treatmentId, hara.note)
    }

    override fun delete(treatmentId: String) {
        log.debug("delete(treatmentId={})", treatmentId)
        jdbcx.update("DELETE FROM $TABLE WHERE id_treatment = ?", treatmentId)
    }

}

@Suppress("UNUSED")
val HaraDiagnosis.Companion.ROW_MAPPER: RowMapper<HaraDiagnosis>
    get() = RowMapper { rs, rowNum ->
        HaraDiagnosis(
                // FIXME #17 implement me
                emptyList(), emptyList(), null, rs.getString("note")
        )
    }
