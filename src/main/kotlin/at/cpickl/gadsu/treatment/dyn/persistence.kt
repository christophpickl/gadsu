package at.cpickl.gadsu.treatment.dyn

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.tcm.model.MeridianFactory
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
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
        val TABLE_KYO = "hara_diagnosis_kyo"
        val TABLE_JITSU = "hara_diagnosis_jitsu"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun find(treatmentId: String): HaraDiagnosis? {
        val rawHara = jdbcx.queryMaybeSingle(RawHaraMapper, "SELECT * FROM $TABLE WHERE id_treatment = ?", arrayOf(treatmentId)) ?: return null
        val kyos = jdbcx.query("SELECT * FROM $TABLE_KYO WHERE id_treatment = ?", arrayOf(treatmentId), MeridianMapper)
        val jitsus = jdbcx.query("SELECT * FROM $TABLE_JITSU WHERE id_treatment = ?", arrayOf(treatmentId), MeridianMapper)
        return HaraDiagnosis(kyos, jitsus, rawHara.connections, rawHara.note)
    }

    override fun insert(treatmentId: String, hara: HaraDiagnosis) {
        log.debug("insert(treatmentId={}, hara={})", treatmentId, hara)

        jdbcx.update("INSERT INTO $TABLE (id_treatment, connection1, connection2, note) VALUES (?, ?, ?, ?)",
                treatmentId, hara.bestConnection?.first?.sqlCode, hara.bestConnection?.second?.sqlCode, hara.note)
        hara.kyos.forEach { jdbcx.update("INSERT INTO $TABLE_KYO (id_treatment, meridian) VALUES (?, ?)", treatmentId, it.sqlCode) }
        hara.jitsus.forEach { jdbcx.update("INSERT INTO $TABLE_JITSU (id_treatment, meridian) VALUES (?, ?)", treatmentId, it.sqlCode) }
    }

    override fun delete(treatmentId: String) {
        log.debug("delete(treatmentId={})", treatmentId)
        jdbcx.update("DELETE FROM $TABLE WHERE id_treatment = ?", treatmentId)
        jdbcx.update("DELETE FROM $TABLE_KYO WHERE id_treatment = ?", treatmentId)
        jdbcx.update("DELETE FROM $TABLE_JITSU WHERE id_treatment = ?", treatmentId)
    }

}

private data class RawHara(val connections: Pair<Meridian, Meridian>?, val note: String)
private object RawHaraMapper : RowMapper<RawHara> {
    override fun mapRow(rs: ResultSet, rowNum: Int): RawHara {
        val connection1 = meridianOrNull(rs.getString("connection1"))
        val connection2 = meridianOrNull(rs.getString("connection2"))
        return RawHara(
                if (connection1 == null || connection2 == null) null else Pair(connection1, connection2),
                rs.getString("note")
        )
    }

    private fun meridianOrNull(sqlCode: String?): Meridian? {
        if (sqlCode == null) {
            return null
        }
        return MeridianFactory.meridianBySqlCode(sqlCode)
    }
}

private object MeridianMapper : RowMapper<Meridian> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = MeridianFactory.meridianBySqlCode(rs.getString("meridian"))
}
