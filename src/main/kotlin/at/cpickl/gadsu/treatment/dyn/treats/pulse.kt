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
enum class PulseProperty(val label: String, val sqlCode: String) {

    Superficial("oberflächlich", "SUPERFICIAL"),
    Deep("tief", "DEEP"),

    // frequence
    Fast("schnell", "FAST"),
    Slow("langsam", "SLOW"),

    // shape
    Full("voll", "FULL"),
    Empty("leer", "EMPTY"),
    Sharp("spitz", "SHARP"),
    Round("rund", "ROUND"),
    Slipery("rutschig", "SLIPERY"),
    Hesitate("zögernd", "HESITATE"),
    // ansteigend
    // fadenfoermig
    // saitenaehnlich
    // gespannt
    // abrupt
    Soft("weich", "SOFT"),
    Raugh("rauh", "RAUGH"),
    Wiry("drahtig", "WIRY"),

    Rhythmical("rhythmisch", "RHYTHMICAL"),
    Arhythmical("arhythmisch", "ARHYTHMICAL")
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

    private val inpPulseProps = MyList("PulseDiagnosisRenderer.Properties",
            MyListModel(PulseProperty.values().toList()), bus, object : MyListCellRenderer<PulseProperty>() {
        override fun newCell(value: PulseProperty) = PulsePropertyCellView(value)
    }).apply {
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        enableToggleSelectionMode()
    }

    private val inpNote = MyTextArea("PulseDiagnosisRenderer.inpNote", 1)

    override val view: JComponent by lazy {
        GridPanel().apply {
            c.weightx = 1.0

            c.fill = GridBagConstraints.BOTH
            c.weighty = 0.7
            c.insets = Insets(GAP, GAP, 0, GAP)
            add(inpPulseProps.scrolled())

            c.gridy++
            c.insets = Pad.all(GAP)
            c.weighty = 0.3
            add(inpNote.scrolled())

            initValues(pulseDiagnosis)
        }
    }

    private fun initValues(pulseDiagnosis: PulseDiagnosis) {
        inpPulseProps.addSelectedValues(pulseDiagnosis.properties)
        inpNote.text = pulseDiagnosis.note
    }

    override fun readDynTreatment() = PulseDiagnosis(
            inpPulseProps.selectedValuesList,
            inpNote.text
    )

    override fun registerOnChange(changeListener: () -> Unit) {
        inpPulseProps.addListSelectionListener { if (!it.valueIsAdjusting) changeListener() }
        inpNote.addChangeListener { changeListener() }
    }

}
