package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.tcm.model.MeridianFactory
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRepository
import at.cpickl.gadsu.treatment.dyn.WEIGHT_HARA
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.inputs.TriCheckBox
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.scrolled
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JLabel

// MODEL
// =====================================================================================================================

private val HARA_TITLE = "Hara Diagnose"

data class HaraDiagnosis(
        val kyos: List<Meridian>,
        val jitsus: List<Meridian>,
        val bestConnection: Pair<Meridian, Meridian>?,
        val note: String
) : DynTreatment {

    companion object {
        fun insertPrototype() = HaraDiagnosis(emptyList(), emptyList(), null, "")
    }

    override val title: String get() = HARA_TITLE
    override val tabLocationWeight: Int get() = WEIGHT_HARA

    override fun <T> call(back: DynTreatmentCallback<T>): T {
        return back.onHaraDiagnosis(this)
    }

}

object HaraDiagnosisManager : DynTreatmentManager {
    override val title: String get() = HARA_TITLE
    override fun dynTreatmentType() = HaraDiagnosis::class.java
    override fun create() = HaraDiagnosis.Companion.insertPrototype()
}

// PERSISTENCE
// =====================================================================================================================

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

/** Database table representation; got no kyo/jitsu lists yet as those are stored in a different table. */
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

// VIEW
// =====================================================================================================================

class KyoJitsuCheckBox(val meridian: Meridian) : TriCheckBox<Meridian>(meridian) {
    val isKyo: Boolean get() = selectionState == STATE_HALF
    val isJitsu: Boolean get() = selectionState == STATE_FULL

    fun initSelectionState(kyos: List<Meridian>, jitsus: List<Meridian>) {
        selectionState =
                if (kyos.contains(meridian)) STATE_HALF
                else if (jitsus.contains(meridian)) STATE_FULL
                else STATE_NONE
    }
}

class HaraDiagnosisRenderer(private val haraDiagnosis: HaraDiagnosis) : DynTreatmentRenderer {

    private val checkLu = KyoJitsuCheckBox(Meridian.Lung)
    private val checkLe = KyoJitsuCheckBox(Meridian.Liver)
    private val allChecks = listOf(checkLu, checkLe)

    private val inpNote = MyTextArea("HaraDiagnosisRenderer.inpNote", 2)

    override val dynTreatment: DynTreatment get() = haraDiagnosis

    override val view: JComponent by lazy {
        val panel = GridPanel()
        // FIXME enable check for changes!
        panel.add(JLabel("Lu"))
        panel.c.gridx++
        panel.add(checkLu)

        panel.c.gridx = 0
        panel.c.gridy++
        panel.add(JLabel("Le"))
        panel.c.gridx++
        panel.add(checkLe)

        panel.c.gridx = 0
        panel.c.gridy++
        panel.c.gridwidth = 2
        panel.add(inpNote.scrolled())

        checkLu.initSelectionState(haraDiagnosis.kyos, haraDiagnosis.jitsus)
        checkLe.initSelectionState(haraDiagnosis.kyos, haraDiagnosis.jitsus)

        inpNote.text = haraDiagnosis.note

        panel
    }

    override fun readDynTreatment(): DynTreatment {
        return HaraDiagnosis(
                allChecks.filter { it.isKyo }.map { it.meridian },
                allChecks.filter { it.isJitsu }.map { it.meridian },
                null, inpNote.text)
    }

}
