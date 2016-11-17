package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRepository
import at.cpickl.gadsu.treatment.dyn.treats.TongueProperty.*
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.addChangeListener
import at.cpickl.gadsu.view.swing.scrolled
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject
import javax.swing.JComponent

// MODEL
// =====================================================================================================================

private val TITLE_TONGUE = "Zungendiagnose"


interface TonguePropertable {
    val label: String
    val sqlCode: String
}



private fun <T : TonguePropertable> mapApplicableFor(allPopertables: Array<T>, raws: List<RawTonguePropertable>): List<T> {
    val rawSqlCodes = raws.map { it.sqlCode }
    return allPopertables.filter { rawSqlCodes.contains(it.sqlCode) }
}

enum class TongueProperty {;

    enum class Color(override val label: String, override val sqlCode: String) : TonguePropertable {
        Pale("blass", "PALE"),
        Pink("rosa", "PINK"), // normal
        Red("rot", "RED")
        ;
//    1. dunkel-rot
//    1. roetlich-violett
//    1. blaeulich-violett
//    1. blau
        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Color.values(), raws)
        }
    }


    enum class Shape(override val label: String, override val sqlCode: String) : TonguePropertable {
        Thin("dünn", "THIN"),
        Swollen("geschwollen", "SWOLLEN")
        ;
//    1. steif
//    1. schlaff (flaccid)
//    1. lang
//    1. kurz
//    1. rissig
//    1. zitternd
//    1. einseitig ausgerichtet

        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Shape.values(), raws)
        }
    }

    enum class Coat(override val label: String, override val sqlCode: String) : TonguePropertable {
        White("weiß", "WHITE"),
        Yellow("gelb", "YELLOW")
        ;
//    1. Farbe
//        1. weiss
//        1. gelb
//        1. grau
//        1. schwarz
//    1. Dicke
//        1. dick
//        1. duenn
//        1. komplett fehlend
//        1. teilweise fehlend
//        1. mit wurzel
//        1. ohne wurzel

        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Coat.values(), raws)
        }
    }

    enum class Special(override val label: String, override val sqlCode: String) : TonguePropertable {
        RedDots("Rote Punkte", "RED DOTS");
        // risse

        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Special.values(), raws)
        }
    }

}

data class TongueDiagnosis(
        val color: List<Color>,
        val shape: List<Shape>,
        val coat: List<Coat>,
        val special: List<Special>,
        val note: String
) : DynTreatment {

    companion object {
        fun insertPrototype() = TongueDiagnosis(emptyList(), emptyList(), emptyList(), emptyList(), "")
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
        val TABLE_PROPERTIES = "tongue_diagnosis_properties"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun find(treatmentId: String): TongueDiagnosis? {
        val rawDiagnosis = jdbcx.queryMaybeSingle(TongueDiagnosis.ROW_MAPPER,
                "SELECT * FROM $TABLE WHERE id_treatment = ?",
                arrayOf(treatmentId))
                ?: return null
        val rawPropertables = jdbcx.query(
                "SELECT * FROM $TABLE_PROPERTIES WHERE id_treatment = ?",
                arrayOf(treatmentId),
                RawTonguePropertable.ROW_MAPPER)

        return rawDiagnosis.copy(
                color = TongueProperty.Color.mapApplicable(rawPropertables),
                shape = TongueProperty.Shape.mapApplicable(rawPropertables),
                coat = TongueProperty.Coat.mapApplicable(rawPropertables),
                special= TongueProperty.Special.mapApplicable(rawPropertables)
        )
    }

    override fun insert(treatmentId: String, dynTreatment: TongueDiagnosis) {
        log.debug("insert(treatmentId={}, dynTreatment={})", treatmentId, dynTreatment)

        jdbcx.update("INSERT INTO $TABLE (id_treatment, note) VALUES (?, ?)", treatmentId, dynTreatment.note)
        val propertables = mutableListOf<TonguePropertable>()
        propertables.addAll(dynTreatment.color)
        propertables.addAll(dynTreatment.coat)
        propertables.addAll(dynTreatment.shape)
        propertables.addAll(dynTreatment.special)
        propertables.forEach {
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
val TongueDiagnosis.Companion.ROW_MAPPER: RowMapper<TongueDiagnosis>
    get() = RowMapper { rs, rowNum ->
        TongueDiagnosis(emptyList(), emptyList(), emptyList(), emptyList(), rs.getString("note"))
    }

data class RawTonguePropertable(val sqlCode: String) {
    companion object {}
}

@Suppress("UNUSED")
private val RawTonguePropertable.Companion.ROW_MAPPER: RowMapper<RawTonguePropertable>
    get() = RowMapper { rs, rowNum ->
        RawTonguePropertable(rs.getString("sql_code"))
    }

// VIEW
// =====================================================================================================================

class TongueDiagnosisRenderer(tongueDiagnosis: TongueDiagnosis) : DynTreatmentRenderer {

    // FIXME add tongue diagnosis enums
    private val inpNote = MyTextArea("TongueDiagnosisRenderer.inpNote", 2)

    override var originalDynTreatment: DynTreatment = tongueDiagnosis

    override val view: JComponent by lazy {
        val panel = GridPanel()

        panel.add(inpNote.scrolled())

        inpNote.text = tongueDiagnosis.note

        panel
    }

    // FIXME
    override fun readDynTreatment() = TongueDiagnosis(emptyList(), emptyList(), emptyList(), emptyList(), inpNote.text)

    override fun registerOnChange(changeListener: () -> Unit) {
        inpNote.addChangeListener { changeListener() }
    }

}

