package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRepository
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.scrolled
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject
import javax.swing.JComponent

// MODEL
// =====================================================================================================================

private val TITLE_TONGUE = "Zungendiagnose"

data class TongueDiagnosis(
        val note: String // um zu speichern wo zb die schwellung ist, etc.
) : DynTreatment {
    companion object {
        fun insertPrototype() = TongueDiagnosis("")
    }

    override val title: String get() = TITLE_TONGUE

    override fun <T> call(back: DynTreatmentCallback<T>): T {
        return back.onTongueDiagnosis(this)
    }
}

object TongueDiagnosisManager : DynTreatmentManager {
    override val title: String get() = TITLE_TONGUE

    override fun dynTreatmentType() = TongueDiagnosis::class.java

    override fun create() = TongueDiagnosis.Companion.insertPrototype()
}

// PERSISTENCE
// =====================================================================================================================

interface TongueDiagnosisRepository : DynTreatmentRepository<TongueDiagnosis> {}

class TongueDiagnosisJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx
) : TongueDiagnosisRepository {

    companion object {
        val TABLE = "tongue_diagnosis"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun find(treatmentId: String): TongueDiagnosis? {
        return jdbcx.queryMaybeSingle(TongueDiagnosis.ROW_MAPPER, "SELECT * FROM $TABLE WHERE id_treatment = ?", arrayOf(treatmentId))
    }

    override fun insert(treatmentId: String, dynTreatment: TongueDiagnosis) {
        log.debug("insert(treatmentId={}, dynTreatment={})", treatmentId, dynTreatment)

        jdbcx.update("INSERT INTO $TABLE (id_treatment, note) VALUES (?, ?)",
                treatmentId, dynTreatment.note)
    }

    override fun delete(treatmentId: String) {
        log.debug("delete(treatmentId={})", treatmentId)
        jdbcx.update("DELETE FROM $TABLE WHERE id_treatment = ?", treatmentId)
    }

}

@Suppress("UNUSED")
val TongueDiagnosis.Companion.ROW_MAPPER: RowMapper<TongueDiagnosis>
    get() = RowMapper { rs, rowNum ->
        TongueDiagnosis(rs.getString("note"))
    }

// VIEW
// =====================================================================================================================

class TongueDiagnosisRenderer(private val tongueDiagnosis: TongueDiagnosis) : DynTreatmentRenderer {

    private val inpNote = MyTextArea("TongueDiagnosisRenderer.inpNote", 2)

    override val dynTreatment: DynTreatment get() = tongueDiagnosis

    override val view: JComponent by lazy {
        val panel = GridPanel()

        panel.add(inpNote.scrolled())

        inpNote.text = tongueDiagnosis.note

        panel
    }

    override fun readDynTreatment() = TongueDiagnosis(inpNote.text)

    override fun isModified(): Boolean {
        return false
    }

}

