package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolGeneratedEvent
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolRepository
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.colorByPercentage
import at.cpickl.gadsu.view.swing.enforceSize
import com.google.common.eventbus.Subscribe
import java.awt.Color
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import javax.inject.Inject
import javax.swing.JPanel

@Logged
open class TreatmentGoalController @Inject constructor(
    prefs: Prefs,
    private val treatmentRepository: TreatmentRepository,
    private val multiProtocolRepository: MultiProtocolRepository
) {

    val enabled = prefs.preferencesData.treatmentGoal != null
    val view = TreatmentGoalView(prefs.preferencesData.treatmentGoal ?: 0, 0)

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        if (enabled) {
            updateTreatmentsNumber()
        }
    }

    @Subscribe open fun onTreatmentCreatedEvent(event: TreatmentCreatedEvent) {
        view.increaseCurrent()
    }

    @Subscribe open fun onTreatmentDeletedEvent(event: TreatmentDeletedEvent) {
        if (!event.treatmentHasBeenProtocolizedYet) {
            view.decreaseCurrent()
        }
    }

    @Subscribe open fun onMultiProtocolGeneratedEvent(event: MultiProtocolGeneratedEvent) {
        updateTreatmentsNumber()
    }

    private fun updateTreatmentsNumber() {
        val currentTreatments = treatmentRepository.countAllNonProtocolized()
        view.updateCurrent(currentTreatments)
    }
}

class TreatmentGoalView(private val goal: Int, private var current: Int) : JPanel() {

    companion object {
        private val RECT_HEIGHT = 2
        private val COMPONENT_HEIGHT = 17
        private val PAD = 2
        private val SIZE_ADJUSTER = 5
    }

    private var progressText: String = ""
    private var percentDone: Double = 0.0

    init {
        enforceSize(preferredSize.width, COMPONENT_HEIGHT)
        updateCurrent(current)
    }

    fun increaseCurrent() {
        updateCurrent(current + 1)
    }

    fun decreaseCurrent() {
        updateCurrent(current - 1)
    }

    fun updateCurrent(newCurrent: Int) {
        if (newCurrent < 0) throw IllegalArgumentException("Treatment goal must not be negative! Was: $newCurrent")
        current = newCurrent
        progressText = "$current/$goal"
        percentDone = (current.toDouble() / goal)
        repaint()
    }

    override fun paintComponent(rawGraphics: Graphics) {
        super.paintComponent(rawGraphics)
        val g = rawGraphics as Graphics2D

        val rectWidth = size.width - SIZE_ADJUSTER
        val yPos = height - (PAD + RECT_HEIGHT)

        // greenish background
        val cleanPercent = Math.min(percentDone, 1.0)
        val properColor = colorByPercentage(cleanPercent)
        g.paint = GradientPaint(0.0F, 0.0F, properColor, 0.0F, RECT_HEIGHT.toFloat(), properColor.darker())
        val greenishWidth = (rectWidth.toDouble() * cleanPercent).toInt()
        g.fillRect(PAD, yPos, greenishWidth, RECT_HEIGHT)

        if (percentDone > 1.0) {
            // overgoal
            val overGoalX = (rectWidth * (goal.toDouble() / current)).toInt()
            g.color = Color.BLUE
            g.fillRect(overGoalX, yPos, rectWidth - overGoalX + 1, RECT_HEIGHT)
        } else {
            // undergoal
            g.color = Color.WHITE
            g.fillRect(PAD + greenishWidth, yPos, rectWidth - greenishWidth, RECT_HEIGHT)
        }

        // info text
        g.font = Font("Arial", Font.PLAIN, 11)
        g.color = Color.BLACK
//        val metrics = g.getFontMetrics(g.font)
//        val width = metrics.stringWidth(progressText)
        g.drawString(progressText, PAD, yPos - 3)
    }

}
