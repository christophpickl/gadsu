package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRepository
import at.cpickl.gadsu.treatment.view.InvalidTreatmentInputException
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.inputs.TriCheckBox
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.addChangeListener
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.withFont
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.awt.Font
import java.awt.GridBagConstraints
import java.sql.ResultSet
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JLabel

// MODEL
// =====================================================================================================================

private val HARA_TITLE = "Hara"

data class HaraDiagnosis(
        val kyos: List<MeridianAndPosition>,
        val jitsus: List<MeridianAndPosition>,
        val bestConnection: Pair<MeridianAndPosition, MeridianAndPosition>?,
        val note: String
) : DynTreatment {

    companion object {
        fun insertPrototype() = HaraDiagnosis(emptyList(), emptyList(), null, "")
    }

    init {
        if (bestConnection != null) {
            if (!kyos.contains(bestConnection.first)) {
                throw IllegalArgumentException("Kyo best connection '${bestConnection.first}' not contained in: $kyos")
            }
            if (!jitsus.contains(bestConnection.second)) {
                throw IllegalArgumentException("Jitsu best connection '${bestConnection.second}' not contained in: $jitsus")
            }
        }
    }

    override val title = HARA_TITLE

    override fun <T> call(back: DynTreatmentCallback<T>): T {
        return back.onHaraDiagnosis(this)
    }

}

enum class HaraPosition(val sqlCode: String) {
    Left("LE"),
    Right("RI"),
    Single("SI"),
    Bottom("BO");

}

enum class MeridianAndPosition(val meridian: Meridian, val position: HaraPosition) {
    LungLeft(Meridian.Lung, HaraPosition.Left),
    LungRight(Meridian.Lung, HaraPosition.Right),
    LargeIntestineLeft(Meridian.LargeIntestine, HaraPosition.Left),
    LargeIntestineRight(Meridian.LargeIntestine, HaraPosition.Right),
    Stomach(Meridian.Stomach, HaraPosition.Single),
    Spleen(Meridian.Spleen, HaraPosition.Single),

    Heart(Meridian.Heart, HaraPosition.Single),
    SmallIntestineLeft(Meridian.SmallIntestine, HaraPosition.Left),
    SmallIntestineRight(Meridian.SmallIntestine, HaraPosition.Right),
    UrinaryBladderLeft(Meridian.UrinaryBladder, HaraPosition.Left),
    UrinaryBladderRight(Meridian.UrinaryBladder, HaraPosition.Right),
    UrinaryBladderBottom(Meridian.UrinaryBladder, HaraPosition.Bottom),
    KidneyLeft(Meridian.Kidney, HaraPosition.Left),
    KidneyRight(Meridian.Kidney, HaraPosition.Right),
    KidneyBottom(Meridian.Kidney, HaraPosition.Bottom),

    Pericardium(Meridian.Pericardium, HaraPosition.Single),
    TripleBurner(Meridian.TripleBurner, HaraPosition.Single),
    GallBladder(Meridian.GallBladder, HaraPosition.Single),
    Liver(Meridian.Liver, HaraPosition.Single)
}

object MeridianAndPositionFactory {

    // i know this can be simplified, but i am tooooo lazy right now ;)
    private val mapBySqlCodes = mapOf(
            Meridian.Lung.sqlCode to mapOf(
                    HaraPosition.Left.sqlCode to MeridianAndPosition.LungLeft,
                    HaraPosition.Right.sqlCode to MeridianAndPosition.LungRight
            ),
            Meridian.LargeIntestine.sqlCode to mapOf(
                    HaraPosition.Left.sqlCode to MeridianAndPosition.LargeIntestineLeft,
                    HaraPosition.Right.sqlCode to MeridianAndPosition.LargeIntestineRight
            ),
            Meridian.Stomach.sqlCode to mapOf(
                    HaraPosition.Single.sqlCode to MeridianAndPosition.Stomach
            ),
            Meridian.Spleen.sqlCode to mapOf(
                    HaraPosition.Single.sqlCode to MeridianAndPosition.Spleen
            ),
            Meridian.Heart.sqlCode to mapOf(
                    HaraPosition.Single.sqlCode to MeridianAndPosition.Heart
            ),
            Meridian.SmallIntestine.sqlCode to mapOf(
                    HaraPosition.Left.sqlCode to MeridianAndPosition.SmallIntestineLeft,
                    HaraPosition.Right.sqlCode to MeridianAndPosition.SmallIntestineRight
            ),
            Meridian.UrinaryBladder.sqlCode to mapOf(
                    HaraPosition.Left.sqlCode to MeridianAndPosition.UrinaryBladderLeft,
                    HaraPosition.Right.sqlCode to MeridianAndPosition.UrinaryBladderRight,
                    HaraPosition.Bottom.sqlCode to MeridianAndPosition.UrinaryBladderBottom
            ),
            Meridian.Kidney.sqlCode to mapOf(
                    HaraPosition.Left.sqlCode to MeridianAndPosition.KidneyLeft,
                    HaraPosition.Right.sqlCode to MeridianAndPosition.KidneyRight,
                    HaraPosition.Bottom.sqlCode to MeridianAndPosition.KidneyBottom
            ),
            Meridian.Pericardium.sqlCode to mapOf(
                    HaraPosition.Single.sqlCode to MeridianAndPosition.Pericardium
            ),
            Meridian.TripleBurner.sqlCode to mapOf(
                    HaraPosition.Single.sqlCode to MeridianAndPosition.TripleBurner
            ),
            Meridian.GallBladder.sqlCode to mapOf(
                    HaraPosition.Single.sqlCode to MeridianAndPosition.GallBladder
            ),
            Meridian.Liver.sqlCode to mapOf(
                    HaraPosition.Single.sqlCode to MeridianAndPosition.Liver
            )
    )

    fun bySqlCodes(meridianSqlCode: String, positionSqlCode: String) =
            mapBySqlCodes[meridianSqlCode]!![positionSqlCode]!!
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
        // those two database tables could be merged into one; actually put abstraction over "meridian list entity"
        val TABLE_KYO = "hara_diagnosis_kyo"
        val TABLE_JITSU = "hara_diagnosis_jitsu"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun find(treatmentId: String): HaraDiagnosis? {
        val rawHara = jdbcx.queryMaybeSingle(RawHaraMapper, "SELECT * FROM $TABLE WHERE id_treatment = ?", arrayOf(treatmentId)) ?: return null
        val kyos = jdbcx.query("SELECT * FROM $TABLE_KYO WHERE id_treatment = ?", arrayOf(treatmentId), MeridianAndPositionMapper)
        val jitsus = jdbcx.query("SELECT * FROM $TABLE_JITSU WHERE id_treatment = ?", arrayOf(treatmentId), MeridianAndPositionMapper)
        return HaraDiagnosis(kyos, jitsus, rawHara.connections, rawHara.note)
    }

    override fun insert(treatmentId: String, dynTreatment: HaraDiagnosis) {
        log.debug("insert(treatmentId={}, dynTreatment={})", treatmentId, dynTreatment)

        jdbcx.update("INSERT INTO $TABLE (id_treatment, connection1Meridian, connection1Position, " +
                "connection2Meridian, connection2Position, note) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
                treatmentId,
                dynTreatment.bestConnection?.first?.meridian?.sqlCode, dynTreatment.bestConnection?.first?.position?.sqlCode,
                dynTreatment.bestConnection?.second?.meridian?.sqlCode, dynTreatment.bestConnection?.second?.position?.sqlCode,
                dynTreatment.note)
        dynTreatment.kyos.forEach {
            jdbcx.update("INSERT INTO $TABLE_KYO (id_treatment, meridian, position) VALUES (?, ?, ?)",
                    treatmentId, it.meridian.sqlCode, it.position.sqlCode)
        }
        dynTreatment.jitsus.forEach {
            jdbcx.update("INSERT INTO $TABLE_JITSU (id_treatment, meridian, position) VALUES (?, ?, ?)",
                    treatmentId, it.meridian.sqlCode, it.position.sqlCode)
        }
    }

    override fun delete(treatmentId: String) {
        log.debug("delete(treatmentId={})", treatmentId)
        jdbcx.update("DELETE FROM $TABLE WHERE id_treatment = ?", treatmentId)
        jdbcx.update("DELETE FROM $TABLE_KYO WHERE id_treatment = ?", treatmentId)
        jdbcx.update("DELETE FROM $TABLE_JITSU WHERE id_treatment = ?", treatmentId)
    }

}

/** Database table representation; got no kyo/jitsu lists yet as those are stored in a different table. */
private data class RawHara(val connections: Pair<MeridianAndPosition, MeridianAndPosition>?, val note: String)

private object RawHaraMapper : RowMapper<RawHara> {
    override fun mapRow(rs: ResultSet, rowNum: Int): RawHara {
        val connection1 = meridianOrNull(rs, "1")
        val connection2 = meridianOrNull(rs, "2")
        return RawHara(
                if (connection1 == null || connection2 == null) null else Pair(connection1, connection2),
                rs.getString("note")
        )
    }

    private fun meridianOrNull(rs: ResultSet, connectionNumber: String): MeridianAndPosition? {
        val sqlCodeMeridian = rs.getString("connection${connectionNumber}Meridian") ?: return null
        val sqlCodePosition = rs.getString("connection${connectionNumber}Position") ?: return null
        return MeridianAndPositionFactory.bySqlCodes(sqlCodeMeridian, sqlCodePosition)
    }
}

private object MeridianAndPositionMapper : RowMapper<MeridianAndPosition> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = MeridianAndPositionFactory.bySqlCodes(
            rs.getString("meridian"),
            rs.getString("position")
    )
}

// VIEW
// =====================================================================================================================

class KyoJitsuCheckBox(val meridianAndPos: MeridianAndPosition) : TriCheckBox<MeridianAndPosition>(
        item = meridianAndPos,
        enableAltSelection = true // represents the connection thingy
) {
    val isKyo: Boolean get() = selectionState == STATE_HALF
    val isJitsu: Boolean get() = selectionState == STATE_FULL

    fun initSelectionState(kyos: List<MeridianAndPosition>, jitsus: List<MeridianAndPosition>) {
        selectionState =
                if (kyos.contains(meridianAndPos)) STATE_HALF
                else if (jitsus.contains(meridianAndPos)) STATE_FULL
                else STATE_NONE
    }

    fun renderWithLabel() = GridPanel().apply {
        c.anchor = GridBagConstraints.CENTER
        add(JLabel(meridianAndPos.meridian.labelShort).withFont(Font.PLAIN, 12))
        c.gridy++
        add(this@KyoJitsuCheckBox)
    }
}

class HaraDiagnosisRenderer(haraDiagnosis: HaraDiagnosis) : DynTreatmentRenderer {

    private val mapOfCheckboxes = MeridianAndPosition.values().associate { Pair(it, KyoJitsuCheckBox(it)) }

    private val inpNote = MyTextArea("HaraDiagnosisRenderer.inpNote", 2)

    override var originalDynTreatment: DynTreatment = haraDiagnosis

    override val view: JComponent by lazy {
        GridPanel().apply {
            c.fill = GridBagConstraints.HORIZONTAL
            c.weightx = 1.0
            c.weighty = 0.0
            add(kyoJitsuPanel())
            c.gridy++
            c.fill = GridBagConstraints.BOTH
            c.weighty = 1.0
            c.insets = Pad.all(DynTreatmentRenderer.GAP)
            add(inpNote.scrolled())

            initValues(haraDiagnosis)
        }
    }

    private fun initValues(haraDiagnosis: HaraDiagnosis) {
        mapOfCheckboxes.values.forEach { it.initSelectionState(haraDiagnosis.kyos, haraDiagnosis.jitsus) }

        val bestConnection = haraDiagnosis.bestConnection
        if (bestConnection != null) {
            mapOfCheckboxes[bestConnection.first]!!.altSelectionState = true
            mapOfCheckboxes[bestConnection.second]!!.altSelectionState = true
        }

        inpNote.text = haraDiagnosis.note
    }

    private fun kyoJitsuPanel(): JComponent {
        return GridPanel().apply {
            c.insets = Pad.all(2)

            c.gridx = 2
            add(meridian(MeridianAndPosition.GallBladder))
            c.gridx = 3
            add(meridian(MeridianAndPosition.Heart))
            c.gridx = 4
            add(meridian(MeridianAndPosition.Stomach))

            c.gridy++
            c.gridx = 0
            add(meridian(MeridianAndPosition.LungRight))
            c.gridx = 1
            add(meridian(MeridianAndPosition.Liver))
            c.gridx = 3
            add(meridian(MeridianAndPosition.Pericardium))
            c.gridx = 5
            add(meridian(MeridianAndPosition.TripleBurner))
            c.gridx = 6
            add(meridian(MeridianAndPosition.LungLeft))

            c.gridy++
            c.gridx = 1
            add(meridian(MeridianAndPosition.UrinaryBladderRight))
            c.gridx = 2
            add(meridian(MeridianAndPosition.KidneyRight))
            c.gridx = 3
            add(meridian(MeridianAndPosition.Spleen))
            c.gridx = 4
            add(meridian(MeridianAndPosition.KidneyLeft))
            c.gridx = 5
            add(meridian(MeridianAndPosition.UrinaryBladderLeft))

            c.gridy++
            c.gridx = 3
            add(meridian(MeridianAndPosition.KidneyBottom))

            c.gridy++
            c.gridx = 1
            add(meridian(MeridianAndPosition.LargeIntestineRight))
            c.gridx = 2
            add(meridian(MeridianAndPosition.SmallIntestineRight))
            c.gridx = 3
            add(meridian(MeridianAndPosition.UrinaryBladderBottom))
            c.gridx = 4
            add(meridian(MeridianAndPosition.SmallIntestineLeft))
            c.gridx = 5
            add(meridian(MeridianAndPosition.LargeIntestineLeft))
        }
    }

    override fun isModified(): Boolean {
        try {
            // first check the validation in here, if it fails, dont let the user potentially save the subtreatment
            readBestConnection()
        } catch(e: InvalidTreatmentInputException) {
            return false
        }
        return originalDynTreatment != readDynTreatment()
    }

    override fun readDynTreatment(): DynTreatment {
        return HaraDiagnosis(
                mapOfCheckboxes.values.filter { it.isKyo }.map { it.meridianAndPos },
                mapOfCheckboxes.values.filter { it.isJitsu }.map { it.meridianAndPos },
                readBestConnection(),
                inpNote.text)
    }

    override fun registerOnChange(changeListener: () -> Unit) {
        mapOfCheckboxes.values.forEach { it.addChangeListener { changeListener() } }
        inpNote.addChangeListener { changeListener() }
    }

    private fun meridian(meridian: MeridianAndPosition) = mapOfCheckboxes[meridian]!!.renderWithLabel()

    private fun readBestConnection(): Pair<MeridianAndPosition, MeridianAndPosition>? {
        val connectionsSelected = mapOfCheckboxes.filter { it.value.altSelectionState }

        if (connectionsSelected.isEmpty()) {
            return null
        }

        if (connectionsSelected.size == 2) {
            val connectionIt = connectionsSelected.iterator()
            val connection1 = connectionIt.next().value
            val connection2 = connectionIt.next().value

            if (!(connection1.isKyo && connection2.isJitsu ||
                    connection1.isJitsu && connection2.isKyo)) {
                throw InvalidTreatmentInputException("Both connections are jitsu!",
                        "Es muss immer ein Kyo und ein Jitsu Meridian ausgewählt werden!")
            }
            val kyoConnection = if (connection1.isKyo) connection1 else connection2
            val jitsuConnection = if (connection1.isJitsu) connection1 else connection2
            return Pair(kyoConnection.meridianAndPos, jitsuConnection.meridianAndPos)
        }

        throw InvalidTreatmentInputException("Invalid amount of connections selected: ${connectionsSelected.size} ($connectionsSelected)",
                "Es müssen genau 0 oder 2 Meridianverbindungen ausgewählt werden, es waren aber ${connectionsSelected.size} ausgewählt!")
    }

}
