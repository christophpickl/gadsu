package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer.Companion.GAP
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRepository
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.addChangeListener
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.ListSelectionModel


// MODEL
// =====================================================================================================================

private val TITLE_PULSE = "Puls"


// https://www.tcm24.de/pulsdiagnostik/
enum class PulseProperty(
        val label: String,
        val sqlCode: String,
        // used to decide whether to render in list 1 or list 2
        val isPrimary: Boolean
) {

    Superficial("oberflächlich", "SUPERFICIAL", true),
    Deep("tief", "DEEP", true),

    // FREQUENCE
    // beschleunigt
    Fast("schnell", "FAST", true),
    Slow("langsam", "SLOW", true),

    // SHAPE
    Full("voll", "FULL", true),
    Empty("leer", "EMPTY", true),
    Powerful("kräftig", "POWERFUL", true),
    Weak("schwach", "WEAK", true),
    Thin("dünn", "THIN", true),
    Thready("fadenförmig", "THREADY", true), // duenn-fadenfoermig
    Sharp("spitz", "SHARP", true),
    Round("rund", "ROUND", true),
    Slipery("rutschig", "SLIPERY", true),
    Hesitate("zögernd", "HESITATE", true),

    Soft("weich", "SOFT", false),
    Raugh("rauh", "RAUGH", false),
    Wiry("drahtig", "WIRY", false),
    Rhythmical("rhythmisch", "RHYTHMICAL", false),
    Arhythmical("arhythmisch", "ARHYTHMICAL", false), // unregelmaessig
    Stringlike("saitenförmig", "STRINGLIKE", false),

    Ascending("ansteigend", "ASCENDING", false),
    Tight("gespannt", "TIGHT", false),
    Abrupt("abrupt", "ABRUPT", false),
    Big("groß", "BIG", false),
    Adhesive("haftend", "ADHESIVE", false),
    Hanging("hängend", "HANGING", false),
    Hunty("jagend", "HUNTY", false),
    Slippery("schlüpfrig", "SLIPPERY", false),
    Floody("überflutend", "FLOODY", false),
    Slowly("verlangsamt", "SLOWLY", false),
    Hidden("versteckt", "HIDDEN", false),
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

private class PulsePropertyCellView(value: PulseProperty) : DefaultCellView<PulseProperty>(value) {

    private val label = JLabel(value.label)
    override val applicableForegrounds: Array<JComponent> = arrayOf(label)

    init {
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(label)
    }
}

class PulseDiagnosisRenderer(
        pulseDiagnosis: PulseDiagnosis,
        bus: EventBus
) : DynTreatmentRenderer {

    override var originalDynTreatment: DynTreatment = pulseDiagnosis

    companion object {
        private val ALL_PRIMARY: List<PulseProperty>
        private val ALL_SECONDARY: List<PulseProperty>

        init {
            val pair = PulseProperty.values().toList().partition { it.isPrimary }
            ALL_PRIMARY = pair.first
            ALL_SECONDARY = pair.second
        }
    }

    private val inpPulseProps1 = MyList("PulseDiagnosisRenderer.Properties1",
            MyListModel(ALL_PRIMARY), bus, object : MyListCellRenderer<PulseProperty>() {
        override fun newCell(value: PulseProperty) = PulsePropertyCellView(value)
    }).apply {
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        enableToggleSelectionMode()
    }
    private val inpPulseProps2 = MyList("PulseDiagnosisRenderer.Properties2",
            MyListModel(ALL_SECONDARY), bus, object : MyListCellRenderer<PulseProperty>() {
        override fun newCell(value: PulseProperty) = PulsePropertyCellView(value)
    }).apply {
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        enableToggleSelectionMode()
    }

    private val inpNote = MyTextArea("PulseDiagnosisRenderer.inpNote", 1)

    override val view: JComponent by lazy {
        GridPanel().apply {
            c.weightx = 0.5

            c.fill = GridBagConstraints.BOTH
            c.weighty = 0.7
            c.insets = Insets(GAP, GAP, 0, GAP)
            add(inpPulseProps1.scrolled())

            c.gridx++
            c.insets = Insets(GAP, 0, 0, GAP)
            add(inpPulseProps2.scrolled())

            c.gridx = 0
            c.gridy++
            c.gridwidth = 2
            c.insets = Pad.all(GAP)
            c.weightx = 1.0
            c.weighty = 0.3
            add(inpNote.scrolled())

            initValues(pulseDiagnosis)
        }
    }

    private fun initValues(pulseDiagnosis: PulseDiagnosis) {
        inpPulseProps1.addSelectedValues(pulseDiagnosis.properties.filter { ALL_PRIMARY.contains(it) })
        inpPulseProps2.addSelectedValues(pulseDiagnosis.properties.filter { ALL_SECONDARY.contains(it) })
        inpNote.text = pulseDiagnosis.note
    }

    override fun readDynTreatment() = PulseDiagnosis(
            inpPulseProps1.selectedValuesList.union(inpPulseProps2.selectedValuesList).toList(),
            inpNote.text
    )

    override fun registerOnChange(changeListener: () -> Unit) {
        inpPulseProps1.addListSelectionListener { if (!it.valueIsAdjusting) changeListener() }
        inpPulseProps2.addListSelectionListener { if (!it.valueIsAdjusting) changeListener() }
        inpNote.addChangeListener { changeListener() }
    }

}
