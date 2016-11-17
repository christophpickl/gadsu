package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentCallback
import at.cpickl.gadsu.treatment.dyn.DynTreatmentManager
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRenderer
import at.cpickl.gadsu.treatment.dyn.DynTreatmentRepository
import at.cpickl.gadsu.view.components.inputs.NumberField
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.addChangeListener
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.awt.GridBagConstraints
import java.sql.ResultSet
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JLabel


// MODEL
// =====================================================================================================================

private val BLOOD_TITLE = "Blutdruck"

data class BloodPressureMeasurement(
        val systolic: Int,
        val diastolic: Int,
        val frequency: Int
)

data class BloodPressure(
        val before: BloodPressureMeasurement?,
        val after: BloodPressureMeasurement?
) : DynTreatment {
    companion object {
        fun insertPrototype() = BloodPressure(null, null)
    }

    override val title: String get() = BLOOD_TITLE

    override fun <T> call(back: DynTreatmentCallback<T>): T {
        return back.onBloodPressure(this)
    }
}

object BloodPressureManager : DynTreatmentManager {
    override val title: String get() = BLOOD_TITLE

    override fun dynTreatmentType() = BloodPressure::class.java

    override fun create() = BloodPressure.Companion.insertPrototype()
}


// PERSISTENCE
// =====================================================================================================================

interface BloodPressureRepository : DynTreatmentRepository<BloodPressure> {}

class BloodPressureJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx
) : BloodPressureRepository {

    companion object {
        val TABLE = "blood_pressure"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun find(treatmentId: String): BloodPressure? {
        return jdbcx.queryMaybeSingle(BloodPressure.ROW_MAPPER, "SELECT * FROM $TABLE WHERE id_treatment = ?", arrayOf(treatmentId))
    }

    override fun insert(treatmentId: String, dynTreatment: BloodPressure) {
        log.debug("insert(treatmentId={}, dynTreatment={})", treatmentId, dynTreatment)

        jdbcx.update("INSERT INTO $TABLE (id_treatment, before_systolic, before_diastolic, before_frequency, after_systolic, after_diastolic, after_frequency) VALUES (?, ?, ?, ?, ?, ?, ?)",
                treatmentId,
                dynTreatment.before?.systolic, dynTreatment.before?.diastolic, dynTreatment.before?.frequency,
                dynTreatment.after?.systolic, dynTreatment.after?.diastolic, dynTreatment.after?.frequency)
    }

    override fun delete(treatmentId: String) {
        log.debug("delete(treatmentId={})", treatmentId)
        jdbcx.update("DELETE FROM $TABLE WHERE id_treatment = ?", treatmentId)
    }

}

@Suppress("UNUSED")
val BloodPressure.Companion.ROW_MAPPER: RowMapper<BloodPressure>
    get() = RowMapper { rs, rowNum ->
        BloodPressure(mapMeasurement(rs, "before"), mapMeasurement(rs, "after"))
    }

private fun mapMeasurement(rs: ResultSet, columnPrefix: String): BloodPressureMeasurement? {
    val systolic = rs.getInt("${columnPrefix}_systolic")
    if (rs.wasNull()) return null
    val diastolic = rs.getInt("${columnPrefix}_diastolic")
    val frequency = rs.getInt("${columnPrefix}_frequency")
    return BloodPressureMeasurement(systolic, diastolic, frequency)
}

// VIEW
// =====================================================================================================================

class BloodPressureRenderer(bloodPressure: BloodPressure) : DynTreatmentRenderer {

    private val beforeMeasure = BloodMeasurementPanel(bloodPressure.before)
    private val afterMeasure = BloodMeasurementPanel(bloodPressure.after)

    override var originalDynTreatment: DynTreatment = bloodPressure

    override val view: JComponent by lazy {
        val panel = GridPanel()

        panel.c.weightx = 1.0
        panel.c.fill = GridBagConstraints.HORIZONTAL
        panel.add(beforeMeasure)
        panel.c.gridy++
        panel.add(afterMeasure)

        panel
    }

    override fun readDynTreatment() = BloodPressure(beforeMeasure.readMeasurement(), afterMeasure.readMeasurement())

    override fun registerOnChange(changeListener: () -> Unit) {
        beforeMeasure.registerOnChange(changeListener)
        afterMeasure.registerOnChange(changeListener)
    }
}

private class BloodMeasurementPanel(initMeasure: BloodPressureMeasurement?) : GridPanel() {

    private val inpSystolic = NumberField(4, 0)
    private val inpDiastolic = NumberField(4, 0)
    private val inpFrequency = NumberField(4, 0)
    private val inputs = arrayOf(inpSystolic, inpDiastolic, inpFrequency)

    init {
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        add(JLabel("Systolisch: "))

        c.gridx++
        c.weightx = 0.3
        c.fill = GridBagConstraints.HORIZONTAL
        add(inpSystolic)

        c.gridx++
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        add(JLabel("Diastolisch: "))

        c.gridx++
        c.weightx = 0.3
        c.fill = GridBagConstraints.HORIZONTAL
        add(inpDiastolic)

        c.gridx++
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        add(JLabel("Frequenz: "))

        c.gridx++
        c.weightx = 0.3
        c.fill = GridBagConstraints.HORIZONTAL
        add(inpFrequency)

        if (initMeasure != null) {
            inpSystolic.numberValue = initMeasure.systolic
            inpDiastolic.numberValue = initMeasure.diastolic
            inpFrequency.numberValue = initMeasure.frequency
        }
    }

    fun readMeasurement(): BloodPressureMeasurement? {
        val systolic = inpSystolic.numberValue
        val diastolic = inpDiastolic.numberValue
        val frequency = inpFrequency.numberValue

        if (systolic == 0 || diastolic == 0 || frequency == 0) {
            return null
        }
        return BloodPressureMeasurement(systolic, diastolic, frequency)
    }

    fun registerOnChange(changeListener: () -> Unit) {
        inputs.forEach { it.addChangeListener { changeListener() } }
    }
}
