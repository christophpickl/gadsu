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
import at.cpickl.gadsu.view.components.MultiProperties
import at.cpickl.gadsu.view.components.MyListCellRenderer
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
import javax.swing.event.ListSelectionListener

// MODEL
// =====================================================================================================================

private val TITLE_TONGUE = "Zunge"


interface TonguePropertable {
    val label: String
    val sqlCode: String
}

private fun <T : TonguePropertable> mapApplicableFor(allPopertables: Array<T>, raws: List<RawTonguePropertable>): List<T> {
    val rawSqlCodes = raws.map { it.sqlCode }
    return allPopertables.filter { rawSqlCodes.contains(it.sqlCode) }
}

enum class TongueProperty {;

    companion object {
        fun all() = listOf(Color::class.java, Shape::class.java, Coat::class.java, Special::class.java)
    }

    enum class Color(override val label: String, override val sqlCode: String) : TonguePropertable {
        Pale("blass", "PALE"),
        Pink("normal rosa", "PINK"),
        Red("rot", "RED"),
        RedSides("roter Rand", "RED SIDES"),
        RedTip("rote Spitze", "RED TIP"),
        BrightRed("scharlach rot", "BRIGHT RED"),
        DarkRed("dunkel rot", "DARK RED"),
        Yellow("gelb", "COLOR YELLOW"),
        Violett("violett (rötlich)", "VIOLET"),
        Purple("lila (bläulich)", "PURPLE"),
        Blue("blau", "BLUE"),
        ;

        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Color.values(), raws)
        }
    }

    enum class Shape(override val label: String, override val sqlCode: String) : TonguePropertable {
        Thin("dünn", "THIN SHAPE"),
        Thick("dick", "SHAPE THICK"), // verdickter, vergroessert
        Swollen("geschwollen", "SWOLLEN"),
        Flaccid("schlaff", "FLACCID"),
        Stiff("steif", "STIFF"),
        Short("kurz", "SHORT"),
        Long("lang", "LONG"),
        Sharp("spitz", "SHARP"),
        Cracked("rissig", "CRACKED"),
        // DELETED! Shaky("zitternd", "SHAKY"),
        OneSideAligned("einseitig", "ONESIDE ALIGNED")
        ;

        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Shape.values(), raws)
        }
    }

    enum class Coat(override val label: String, override val sqlCode: String) : TonguePropertable {

        // thickness
        Missing("kein", "MISSING"), // belaglos, kein, fehlt
        Less("wenig", "LESS"),
        Much("vermehrt", "MUCH"),
        Thin("dünn", "THIN COAT"),
        Thick("dick", "COAT THICK"),
        Spotted("fleckig", "SPOTTED"),

        // color
        White("weiß", "WHITE"), // kaelte = yin
        Yellow("gelb", "COAT YELLOW"), // hitze = yang
        Brown("braun", "BROWN"),
        Grey("grau", "GREY"),
        Black("schwarz", "BLACK"),

        // sonstiges
        // DELETED! PartlyMissing("teilweise fehlend", "PARTLY MISSING"),
        Unrooted("Wurzel ohne", "UNROOTED"),
        Rooted("Wurzel mit", "ROOTED"),
        // DELETED! Wet("feucht", "COAT WET"),
        // DELETED! Dry("trocken", "COAT DRY"),
        Slobbery("schmierig", "SLOBBERY"),
        ;

        companion object {
            fun mapApplicable(raws: List<RawTonguePropertable>) = mapApplicableFor(Coat.values(), raws)
        }
    }

    enum class Special(override val label: String, override val sqlCode: String) : TonguePropertable {
        ShowsHesitate("zeigt zögerlich", "SHOWS HESITATE"),
        ShowsQuick("zeigt schnell", "SHOWS QUICK"),
        Dry("trocken", "SPECIAL DRY"),
        Moist("feucht", "MOST"),
        TeethMarks("Zahnabdrücke", "TEETH MARKS"), // -eindruecke
        MiddleCrack("Mittelriss", "MID CRACK"),
        HorizontalCrack("Querriss(e)", "HORIZONTAL CRACK"),

        RedDots("rote Papillen", "RED DOTS"),
        RedPatch("rote Flecken", "RED PATCH"),
        // DELETED! ViolettDots("violette Papillen", "VIOLETT DOTS"),
        // DELETED! ViolettPatch("violette Flecken", "VIOLETT PATCH"),
        ShowsShaky("zittert", "SHOWS SHAKY"), // yin, means wind
        TipDown("Spitze hängt runter", "TIP DOWN"),
        Wet("nass", "SPECIAL WET"),
        Sticky("klebrig", "STICKY"), // = schlüpfrig (?)
        Thorns("Dornen", "THORNS"),

        BelowTongueBright("Untervenen Hell", "BELOW BRIGHT"),
        BelowTongueBlue("Untervenen Blau", "BELOW BLUE"),
        BelowTonguePurple("Untervenen Lila", "BELOW PURPLE"),
        BelowTongueSwell("Untervenen gestaut", "BELOW SWELL"),
        BelowTongueSwollen("Untervenen geschwollen", "BELOW SWOLLEN"),
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

    override val title = TITLE_TONGUE

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


class TongueDiagnosisRenderer(
        tongueDiagnosis: TongueDiagnosis,
        bus: EventBus
) : DynTreatmentRenderer {

    private val inpListColor: MultiProperties<Color> = MultiProperties(
            Color.values().toList(), bus, TonguePropertableRenderer(), "TongueColor", { it.map { it.label } }, false)

    private val inpListShape: MultiProperties<Shape> = MultiProperties(
            Shape.values().toList(), bus, TonguePropertableRenderer(), "TongueShape", { it.map { it.label } }, false)

    private val inpListCoat: MultiProperties<Coat> = MultiProperties(
            Coat.values().toList(), bus, TonguePropertableRenderer(), "TongueCoat", { it.map { it.label } }, false)

    private val inpListSpecial: MultiProperties<Special> = MultiProperties(
            Special.values().toList(), bus, TonguePropertableRenderer(), "TongueSpecial", { it.map { it.label } }, false)

    private val inpLists = arrayOf(inpListColor, inpListShape, inpListCoat, inpListSpecial)
    private val inpNote = MyTextArea("TongueDiagnosisRenderer.inpNote", 2) // does not work, see gridpanel...
    override var originalDynTreatment: DynTreatment = tongueDiagnosis

    override val view: JComponent by lazy {
        GridPanel().apply {
            c.weightx = 0.5

            c.fill = GridBagConstraints.HORIZONTAL
            c.weighty = 0.0
            c.insets = Insets(0, GAP, 0, GAP)
            add(JLabel("Farbe").bold())
            c.gridx++
            c.insets = Insets(0, 0, 0, GAP)
            add(JLabel("Form").bold())

            c.gridy++
            c.gridx = 0
            c.fill = GridBagConstraints.BOTH
            c.weighty = 1.0
            c.insets = Insets(0, GAP, GAP, GAP)
            add(inpListColor.toComponent())
            c.gridx++
            c.insets = Insets(0, 0, GAP, GAP)
            add(inpListShape.toComponent())

            c.gridy++
            c.gridx = 0
            c.fill = GridBagConstraints.HORIZONTAL
            c.weighty = 0.0
            c.insets = Insets(0, GAP, 0, GAP)
            add(JLabel("Belag").bold())
            c.gridx++
            c.insets = Insets(0, 0, 0, GAP)
            add(JLabel("Besonderheiten").bold())

            c.gridy++
            c.gridx = 0
            c.fill = GridBagConstraints.BOTH
            c.weighty = 1.0
            c.insets = Insets(0, GAP, 0, GAP)
            add(inpListCoat.toComponent())
            c.gridx++
            c.insets = Insets(0, 0, 0, GAP)
            add(inpListSpecial.toComponent())

            c.gridy++
            c.gridx = 0
            c.fill = GridBagConstraints.BOTH
            c.gridwidth = 2
            c.weightx = 1.0
            c.weighty = 0.1
            c.insets = Pad.all(GAP)
            add(inpNote.scrolled())

            initValues(tongueDiagnosis)
        }
    }

    private fun initValues(tongueDiagnosis: TongueDiagnosis) {
        inpListColor.updateValue(tongueDiagnosis.color, "")
        inpListShape.updateValue(tongueDiagnosis.shape, "")
        inpListCoat.updateValue(tongueDiagnosis.coat, "")
        inpListSpecial.updateValue(tongueDiagnosis.special, "")
        inpNote.text = tongueDiagnosis.note
    }

    override fun readDynTreatment() = TongueDiagnosis(
            inpListColor.selectedValues,
            inpListShape.selectedValues,
            inpListCoat.selectedValues,
            inpListSpecial.selectedValues,
            inpNote.text
    )

    override fun registerOnChange(changeListener: () -> Unit) {
        inpLists.forEach {
            it.addListSelectionListener(ListSelectionListener { if (!it.valueIsAdjusting) changeListener() })
        }
        inpNote.addChangeListener { changeListener() }
    }

}
