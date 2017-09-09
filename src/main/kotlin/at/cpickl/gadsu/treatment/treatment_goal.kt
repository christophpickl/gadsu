package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.global.AppStartupEvent
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolGeneratedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.view.colorByPercentage
import at.cpickl.gadsu.view.swing.enforceSize
import com.google.common.annotations.VisibleForTesting
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
    private val treatmentRepository: TreatmentRepository
) {
    private val log = LOG(javaClass)
    val enabled = prefs.preferencesData.treatmentGoal != null
    val view = TreatmentGoalView(prefs.preferencesData.treatmentGoal ?: 0, 0)

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        if (enabled) {
            updateTreatmentsNumber()
        }
    }

    @Subscribe open fun onTreatmentCreatedEvent(event: TreatmentCreatedEvent) {
        if (enabled) {
            view.increaseCount()
        }
    }

    @Subscribe open fun onTreatmentDeletedEvent(event: TreatmentDeletedEvent) {
        if (enabled && !event.treatmentHasBeenProtocolizedYet) {
            view.decreaseCount()
        }
    }

    @Subscribe open fun onMultiProtocolGeneratedEvent(event: MultiProtocolGeneratedEvent) {
        if (enabled) {
            updateTreatmentsNumber()
        }
    }

    private fun updateTreatmentsNumber() {
        val currentTreatments = treatmentRepository.countAllNonProtocolized()
        log.trace("updateTreatmentsNumber() ... currentTreatments = {}", currentTreatments)
        view.updateCount(currentTreatments)
    }
}

class TreatmentGoalView(
        private val goal: Int,
        @VisibleForTesting internal var count: Int
) : JPanel() {

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
        updateCount(count)
    }

    fun increaseCount() {
        updateCount(count + 1)
    }

    fun decreaseCount() {
        updateCount(count - 1)
    }

    fun updateCount(newCount: Int) {
        if (newCount < 0) {
            throw IllegalArgumentException("Treatment goal must not be negative! Was: $newCount")
        }
        count = newCount
        progressText = "$count/$goal"
        percentDone = (count.toDouble() / goal)
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
            val overGoalX = (rectWidth * (goal.toDouble() / count)).toInt()
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
