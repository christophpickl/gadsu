package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRepository
import at.cpickl.gadsu.view.components.panels.GridPanel
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JLabel


// MODEL
// =====================================================================================================================

private val TITLE_PULSE = "Puls"

enum class PulseProperty(val label: String, val sqlCode: String) {
    Superficial("oberflächlich", "SUPERFICIAL"),
    Deep("tief", "DEEP")
    // FIXME more enums
    ;

    companion object {
        fun mapSqlCodes(rawProperties: MutableList<RawPulseProperty>): List<PulseProperty> {
            val sqlCodes = rawProperties.map { it.sqlCode }
            return PulseProperty.values().filter { sqlCodes.contains(it.sqlCode) }
        }
    }
}

data class PulseDiagnosis(
        val properties: List<PulseProperty>,
        val note: String
) : DynTreatment {

    companion object {
        fun insertPrototype() = PulseDiagnosis(emptyList(), "")
    }

    override val title = TITLE_PULSE

    override fun <T> call(back: DynTreatmentCallback<T>) = back.onPulseDiagnosis(this)

}

object PulseDiagnosisManager : DynTreatmentManager {
    override val title: String get() = TITLE_PULSE

    override fun dynTreatmentType() = PulseDiagnosis::class.java

    override fun create() = PulseDiagnosis.Companion.insertPrototype()
}

// PERSISTENCE
// =====================================================================================================================


interface PulseDiagnosisRepository : DynTreatmentRepository<PulseDiagnosis> {}

class PulseDiagnosisJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx
) : PulseDiagnosisRepository {

    companion object {
        val TABLE = "pulse_diagnosis"
        val TABLE_PROPERTIES = "pulse_diagnosis_properties"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun find(treatmentId: String): PulseDiagnosis? {
        val rawDiagnosis = jdbcx.queryMaybeSingle(PulseDiagnosis.ROW_MAPPER,
                "SELECT * FROM $TABLE WHERE id_treatment = ?",
                arrayOf(treatmentId))
                ?: return null

        val rawPropertables = jdbcx.query(
                "SELECT * FROM $TABLE_PROPERTIES WHERE id_treatment = ?",
                arrayOf(treatmentId),
                RawPulseProperty.ROW_MAPPER)

        return rawDiagnosis.copy(properties = PulseProperty.mapSqlCodes(rawPropertables))
    }

    override fun insert(treatmentId: String, dynTreatment: PulseDiagnosis) {
        log.debug("insert(treatmentId={}, dynTreatment={})", treatmentId, dynTreatment)

        jdbcx.update("INSERT INTO $TABLE (id_treatment, note) VALUES (?, ?)", treatmentId, dynTreatment.note)
        dynTreatment.properties.forEach {
            jdbcx.update("INSERT INTO $TABLE_PROPERTIES (id_treatment, sql_code) VALUES (?, ?)", treatmentId, it.sqlCode)
        }
    }

    override fun delete(treatmentId: String) {
        log.debug("delete(treatmentId={})", treatmentId)
        jdbcx.update("DELETE FROM $TABLE WHERE id_treatment = ?", treatmentId)
        jdbcx.update("DELETE FROM $TABLE_PROPERTIES WHERE id_treatment = ?", treatmentId)
    }

}

@Suppress("UNUSED")
val PulseDiagnosis.Companion.ROW_MAPPER: RowMapper<PulseDiagnosis>
    get() = RowMapper { rs, rowNum ->
        PulseDiagnosis(emptyList(), rs.getString("note"))
    }

data class RawPulseProperty(val sqlCode: String) {
    companion object {}
}

@Suppress("UNUSED")
private val RawPulseProperty.Companion.ROW_MAPPER: RowMapper<RawPulseProperty>
    get() = RowMapper { rs, rowNum ->
        RawPulseProperty(rs.getString("sql_code"))
    }

// VIEW
// =====================================================================================================================

class PulseDiagnosisRenderer(
        pulseDiagnosis: PulseDiagnosis
) : DynTreatmentRenderer {

    override var originalDynTreatment: DynTreatment = pulseDiagnosis

    override val view: JComponent by lazy {
        GridPanel().apply {
            add(JLabel("tongue (enum, note)"))
        }
    }

    override fun readDynTreatment() = PulseDiagnosis(emptyList(), "")

    override fun registerOnChange(changeListener: () -> Unit) {
        // FIXME
    }

}
