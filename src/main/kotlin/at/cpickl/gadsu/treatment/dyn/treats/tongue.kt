package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer.Companion.GAP
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRepository
import at.cpickl.gadsu.treatment.dyn.treats.TongueProperty.*
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.addChangeListener
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.bold
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
        Pink("rosa", "PINK"),
        Red("rot", "RED"),
        DarkRed("dunkel rot", "DARK RED"),
        VioletRed("rötlich violet", "VIOLET RED"),
        VioletBlue("bläulich violet", "VIOLET BLUE"),
        Blue("blau", "BLUE"),
        ;

        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Color.values(), raws)
        }
    }

    enum class Shape(override val label: String, override val sqlCode: String) : TonguePropertable {
        Thin("dünn", "THIN SHAPE"),
        Swollen("geschwollen", "SWOLLEN"),
        Stiff("steif", "STIFF"),
        Flaccid("schlaff", "FLACCID"),
        Long("lang", "LONG"),
        Short("kurz", "SHORT"),
        Cracked("rissig", "CRACKED"),
        Shaky("zitternd", "SHAKY"),
        OneSideAligned("einseitig ausgerichtet", "ONESIDE ALIGNED")
        ;

        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Shape.values(), raws)
        }
    }

    enum class Coat(override val label: String, override val sqlCode: String) : TonguePropertable {
        // color
        White("weiß", "WHITE"),
        Yellow("gelb", "YELLOW"),
        Grey("grau", "GREY"),
        Black("schwarz", "BLACK"),
        // thickness
        Thick("dick", "THICK"),
        Thin("dünn", "THIN COAT"),
        Missing("komplett fehlend", "MISSING"),
        PartlyMissing("teilweise fehlend", "PARTLY MISSING"),
        Rooted("mit Wurzel", "ROOTED"),
        Unrooted("ohne Wurzel", "UNROOTED"),
        ;

        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Coat.values(), raws)
        }
    }

    enum class Special(override val label: String, override val sqlCode: String) : TonguePropertable {
        ShowsQuick("schnell zeigen", "SHOWS QUICK"),
        ShowsHesitate("zögerlich zeigen", "SHOWS HESITATE"),
        RedDots("Rote Papillen", "RED DOTS"),
        MiddleCrack("Mittelriss", "MID CRACK"),
        HorizontalCrack("Querriss", "HORIZONTAL CRACK"),
        BelowTongueBlue("Unterseite Blau", "BELOW BLUE"),
        BelowTonguePurple("Unterseite Lila", "BELOW PURPLE"),
        TeethMarks("Zahnabdrücke", "TEETH MARKS"),
        Dry("feucht", "DRY"),
        Wet("trocken", "WET"),
        Sticky("klebrig/schlüpfrig", "STICKY")
        ;

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
                special = TongueProperty.Special.mapApplicable(rawPropertables)
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

private class TonguePropertableRenderer<T : TonguePropertable> : MyListCellRenderer<T>() {
    override fun newCell(value: T) = TonguePropertableCellView(value)
}

private class TonguePropertableCellView<T : TonguePropertable>(value: T) : DefaultCellView<T>(value) {
    private val label = JLabel(value.label)

    override val applicableForegrounds: Array<JComponent> = arrayOf(label)

    init {
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(label)
    }
}

//private class TongueList<T : TonguePropertable>(
//        viewNameSuffix: String,
//        values: Array<T>,
//        bus: EventBus
//) : MyList<T>("TongueList.$viewNameSuffix", MyListModel<T>(values.toList()), bus, TonguePropertableRenderer()) {
//    init {
//        enableToggleSelectionMode()
//        visibleRowCount = 3
//    }
//}

class TongueDiagnosisRenderer(
        tongueDiagnosis: TongueDiagnosis,
        bus: EventBus
) : DynTreatmentRenderer {

    // TODO copy'n'paste as i dont get generics :(
    private val inpListColor = MyList("TongueDiagnosisRenderer.Color",
            MyListModel(Color.values().toList()), bus, TonguePropertableRenderer(), "Farbe")
    private val inpListShape = MyList("TongueDiagnosisRenderer.Shape",
            MyListModel(Shape.values().toList()), bus, TonguePropertableRenderer(), "Form")
    private val inpListCoat = MyList("TongueDiagnosisRenderer.Coat",
            MyListModel(Coat.values().toList()), bus, TonguePropertableRenderer(), "Belag")
    private val inpListSpecial = MyList("TongueDiagnosisRenderer.Special",
            MyListModel(Special.values().toList()), bus, TonguePropertableRenderer(), "Besonderheiten")

    private val inpLists = arrayOf(inpListColor, inpListShape, inpListCoat, inpListSpecial)
    private val inpNote = MyTextArea("TongueDiagnosisRenderer.inpNote", 2) // does not work, see gridpanel...
    override var originalDynTreatment: DynTreatment = tongueDiagnosis

    init {
        inpLists.forEach {
            with(it) {
                setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
                enableToggleSelectionMode()
            }
        }
    }

    override val view: JComponent by lazy {
        GridPanel().apply {
            c.weightx = 0.5

            c.fill = GridBagConstraints.HORIZONTAL
            c.weighty = 0.0
            c.insets = Insets(0, GAP, 0, GAP)
            add(JLabel(inpListColor.label).bold())
            c.gridx++
            c.insets = Insets(0, 0, 0, GAP)
            add(JLabel(inpListShape.label).bold())

            c.gridy++
            c.gridx = 0
            c.fill = GridBagConstraints.BOTH
            c.weighty = 1.0
            c.insets = Insets(0, GAP, GAP, GAP)
            add(inpListColor.scrolled())
            c.gridx++
            c.insets = Insets(0, 0, GAP, GAP)
            add(inpListShape.scrolled())

            c.gridy++
            c.gridx = 0
            c.fill = GridBagConstraints.HORIZONTAL
            c.weighty = 0.0
            c.insets = Insets(0, GAP, 0, GAP)
            add(JLabel(inpListCoat.label).bold())
            c.gridx++
            c.insets = Insets(0, 0, 0, GAP)
            add(JLabel(inpListSpecial.label).bold())

            c.gridy++
            c.gridx = 0
            c.fill = GridBagConstraints.BOTH
            c.weighty = 1.0
            c.insets = Insets(0, GAP, 0, GAP)
            add(inpListCoat.scrolled())
            c.gridx++
            c.insets = Insets(0, 0, 0, GAP)
            add(inpListSpecial.scrolled())

            c.gridy++
            c.gridx = 0
            c.fill = GridBagConstraints.BOTH
            c.gridwidth = 2
            c.weightx = 1.0
            c.weighty = 0.1
            c.insets = Pad.all(GAP)
            add(inpNote.scrolled())

            inpListColor.addSelectedValues(tongueDiagnosis.color)
            inpListShape.addSelectedValues(tongueDiagnosis.shape)
            inpListCoat.addSelectedValues(tongueDiagnosis.coat)
            inpListSpecial.addSelectedValues(tongueDiagnosis.special)
            inpNote.text = tongueDiagnosis.note
        }
    }

    override fun readDynTreatment() = TongueDiagnosis(
            inpListColor.selectedValuesList,
            inpListShape.selectedValuesList,
            inpListCoat.selectedValuesList,
            inpListSpecial.selectedValuesList,
            inpNote.text
    )

    override fun registerOnChange(changeListener: () -> Unit) {
        inpListColor.addListSelectionListener { if (!it.valueIsAdjusting) changeListener() }
        inpListShape.addListSelectionListener { if (!it.valueIsAdjusting) changeListener() }
        inpListCoat.addListSelectionListener { if (!it.valueIsAdjusting) changeListener() }
        inpListSpecial.addListSelectionListener { if (!it.valueIsAdjusting) changeListener() }
        inpNote.addChangeListener { changeListener() }
    }

}
