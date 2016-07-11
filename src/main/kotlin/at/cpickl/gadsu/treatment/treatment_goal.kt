package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.colorByPercentage
import com.google.common.eventbus.Subscribe
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.inject.Inject
import javax.swing.JPanel

@Logged
open class TreatmentGoalController @Inject constructor(
    prefs: Prefs,
    treatmentRepository: TreatmentRepository
) {

    val enabled: Boolean
    val view: TreatmentGoalView
    init {
        enabled = prefs.preferencesData.treatmentGoal != null
        if (enabled) {
            val currentTreatments = treatmentRepository.countAll()
            view = TreatmentGoalView(prefs.preferencesData.treatmentGoal!!, currentTreatments)
        } else {
            view = TreatmentGoalView(0, 0)
        }
    }

    @Subscribe open fun onTreatmentCreatedEvent(event: TreatmentCreatedEvent) {
        view.increaseCurrent()
    }

    @Subscribe open fun onTreatmentDeletedEvent(event: TreatmentDeletedEvent) {
        view.decreaseCurrent()
    }
}

class TreatmentGoalView(private val goal: Int, private var current: Int) : JPanel() {

    companion object {
        private val PAD = 2
        private val SIZE_ADJUSTER = 5
        private val OVERGOAL_COLOR = Color.GREEN
    }

    private var progressText: String = ""
    private var percentDone: Double = 0.0

    init {
        updateCurrent(current)
    }

    fun increaseCurrent() {
        updateCurrent(current + 1)
    }

    fun decreaseCurrent() {
        updateCurrent(current - 1)
    }

    fun updateCurrent(newCurrent: Int) {
        current = newCurrent
        progressText = "$current/$goal"
        percentDone = (current.toDouble() / goal)
        repaint()
    }

    override fun paintComponent(rawGraphics: Graphics) {
        super.paintComponent(rawGraphics)
        val g = rawGraphics as Graphics2D

        val rectWidth = size.width - SIZE_ADJUSTER
        val rectHeight = size.height - SIZE_ADJUSTER

        // greenish background
        val cleanPercent = Math.min(percentDone, 1.0)
        g.color = colorByPercentage(cleanPercent)
        val greenishWidth = (rectWidth.toDouble() * cleanPercent).toInt()
        g.fillRect(PAD, PAD, greenishWidth, rectHeight)

        if (percentDone > 1.0) {
            // overgoal
            val overGoalX = (rectWidth * (goal.toDouble() / current)).toInt()

            g.color = OVERGOAL_COLOR
            g.fillRect(overGoalX, PAD, rectWidth - overGoalX + 1, rectHeight)

            g.color = Color.BLACK
            g.drawLine(overGoalX, PAD, overGoalX, rectHeight + 1) // haha, one pixel hack ;)
        } else {
            // undergoal
            g.color = Color.WHITE
            g.fillRect(PAD + greenishWidth, PAD, rectWidth - greenishWidth, rectHeight)
        }

        // border
        g.color = Color.BLACK
        g.drawRect(PAD, PAD, rectWidth, rectHeight)

        // info text
        g.color = Color.BLACK
        g.drawString(progressText, 10, 18)
    }

}