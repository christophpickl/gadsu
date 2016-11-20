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
import at.cpickl.gadsu.view.swing.Pad
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.awt.GridBagConstraints
import java.sql.ResultSet
import javax.inject.Inject
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel


// MODEL
// =====================================================================================================================

private val BLOOD_TITLE = "Blut"

data class BloodPressureMeasurement(
        val systolic: Int,
        val diastolic: Int,
        val frequency: Int
) {
    companion object {
        fun insertPrototype() = BloodPressureMeasurement(0, 0, 0)
    }
}

data class BloodPressure(
        val before: BloodPressureMeasurement,
        val after: BloodPressureMeasurement
) : DynTreatment {
    companion object {
        fun insertPrototype() = BloodPressure(
                BloodPressureMeasurement.insertPrototype(),
                BloodPressureMeasurement.insertPrototype()
        )
    }

    override val title = BLOOD_TITLE

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
                dynTreatment.before.systolic, dynTreatment.before.diastolic, dynTreatment.before.frequency,
                dynTreatment.after.systolic, dynTreatment.after.diastolic, dynTreatment.after.frequency)
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

private fun mapMeasurement(rs: ResultSet, columnPrefix: String): BloodPressureMeasurement {
    val systolic = rs.getInt("${columnPrefix}_systolic")
    val diastolic = rs.getInt("${columnPrefix}_diastolic")
    val frequency = rs.getInt("${columnPrefix}_frequency")
    return BloodPressureMeasurement(systolic, diastolic, frequency)
}

// VIEW
// =====================================================================================================================

class BloodPressureRenderer(bloodPressure: BloodPressure) : DynTreatmentRenderer {

    private val beforeMeasure = BloodMeasurementPanel("Davor", bloodPressure.before)
    private val afterMeasure = BloodMeasurementPanel("Danach", bloodPressure.after)

    override var originalDynTreatment: DynTreatment = bloodPressure

    override val view: JComponent by lazy {
        GridPanel().apply {
            val icon = ImageIcon(javaClass.getResource("/gadsu/images/hearbeat.png"))

            c.weightx = 1.0
            c.weighty = 0.5
            c.fill = GridBagConstraints.BOTH
            add(JLabel()) // fill gap hack

            c.gridy++
            c.weighty = 0.0
            c.fill = GridBagConstraints.NONE
            c.insets = Pad.bottom(20)
            c.anchor = GridBagConstraints.SOUTH
            add(JLabel(icon))

            c.gridy++
            c.weighty = 0.0
            c.fill = GridBagConstraints.HORIZONTAL
            c.insets = Pad.ZERO
            c.anchor = GridBagConstraints.NORTH
            add(beforeMeasure)

            c.gridy++
            add(afterMeasure)

            c.gridy++
            c.weighty = 0.5
            c.fill = GridBagConstraints.BOTH
            add(JLabel()) // fill gap hack
        }
    }

    override fun readDynTreatment() = BloodPressure(
            beforeMeasure.readMeasurement(),
            afterMeasure.readMeasurement()
    )

    override fun registerOnChange(changeListener: () -> Unit) {
        beforeMeasure.registerOnChange(changeListener)
        afterMeasure.registerOnChange(changeListener)
    }
}

private class BloodMeasurementPanel(label: String, initMeasure: BloodPressureMeasurement?) : GridPanel() {

    private val inpSystolic = NumberField(4, 0)
    private val inpDiastolic = NumberField(4, 0)
    private val inpFrequency = NumberField(4, 0)
    private val inputs = arrayOf(inpSystolic, inpDiastolic, inpFrequency)

    init {
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE

        add(JLabel("$label (Sys/Dia/Freq): "))

        c.gridx++
        add(inpSystolic)
        c.gridx++
        add(inpDiastolic)
        c.gridx++
        add(inpFrequency)

        if (initMeasure != null) {
            inpSystolic.numberValue = initMeasure.systolic
            inpDiastolic.numberValue = initMeasure.diastolic
            inpFrequency.numberValue = initMeasure.frequency
        }
    }

    fun readMeasurement() =
            BloodPressureMeasurement(
                    inpSystolic.numberValue,
                    inpDiastolic.numberValue,
                    inpFrequency.numberValue
            )

    fun registerOnChange(changeListener: () -> Unit) {
        inputs.forEach { it.addChangeListener { changeListener() } }
    }
}
