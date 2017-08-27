package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.preferences.ThresholdPrefData
import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.swing.enforceSize
import java.awt.Color
import java.awt.Graphics
import javax.inject.Inject
import javax.swing.JPanel


interface ThresholdCalculator {

    fun calc(client: ExtendedClient): ThresholdResult

}

class ThresholdCalculatorImpl @Inject constructor(private val prefs: Prefs) : ThresholdCalculator {

    private val ClientCategory.threshold: Double
        get() = when (this) {
            ClientCategory.A -> 0.7
            ClientCategory.B -> 1.0
            ClientCategory.C -> 1.5
        }

    private val ClientDonation.threshold: Double
        get() = when (this) {
            ClientDonation.UNKNOWN -> 1.0
            ClientDonation.NONE -> 1.6
            ClientDonation.PRESENT -> 0.9
            ClientDonation.MONEY -> 0.7
        }

    override fun calc(client: ExtendedClient): ThresholdResult {
        if (client.upcomingAppointment != null) {
            return ThresholdResult.GotNextAppointment
        }

        val configuredThreshold = prefs.preferencesData.threshold
        val days = client.differenceDaysToRecentTreatment!!
        return ThresholdIntermedResult.values().firstOrNull { days < calcThresholdCount(it, client, configuredThreshold) }?.previous
                ?: ThresholdResult.Fatal
    }

    private fun calcThresholdCount(result: ThresholdIntermedResult, client: ExtendedClient, configuredThreshold: ThresholdPrefData): Int {
        val baseCount = result.fromConfig(configuredThreshold)
        var calced = baseCount.toDouble()
        calced *= client.category.threshold
        calced *= client.donation.threshold
        return calced.toInt()
    }

    private fun ThresholdIntermedResult.fromConfig(config: ThresholdPrefData) = when (this) {
        ThresholdIntermedResult.Attention -> config.daysAttention
        ThresholdIntermedResult.Warn -> config.daysWarn
        ThresholdIntermedResult.Fatal -> config.daysFatal
    }
}

private enum class ThresholdIntermedResult(val delegate: ThresholdResult, val previous: ThresholdResult) {
    Attention(ThresholdResult.Attention, ThresholdResult.Ok),
    Warn(ThresholdResult.Warn, ThresholdResult.Attention),
    Fatal(ThresholdResult.Fatal, ThresholdResult.Warn)
}

class RecentTreatmentPanel(daysSinceLastTreatment: Int, result: ThresholdResult) : JPanel() {
    companion object {
        private fun labelTextForRecentTreatment(days: Int): String {
            if (days < 0) {
                return "Negativ?!"
            }
            return when (days) {
                0 -> "Heute"
                1 -> "Gestern"
                2 -> "Vorgestern"
                else -> "Vor $days Tagen"
            }
        }

    }

    var labelColor = Color.BLACK!!
    private val labelText = labelTextForRecentTreatment(daysSinceLastTreatment)
    private val colorFilling = result.color1
    private val colorBorder = result.color2

    init {
        enforceSize(138, 12)
    }

    override fun paint(g: Graphics) {
        super.paint(g)

        g.color = colorFilling
        g.fillRect(0, 0, width, height)

        g.color = colorBorder
        g.drawRect(0, 0, width - 1, height - 1)

        g.color = labelColor
        g.font = g.font.deriveFont(9.0F)
        g.drawString(labelText, 4, 9)
    }

}

enum class ThresholdResult(val color1: Color, val color2: Color) {
    GotNextAppointment(Color.GRAY, Color.BLACK),
    Ok(Colors.byHex("02bb1c"), Colors.byHex("015d0e")), // green
    Attention(Colors.byHex("e0b520"), Colors.byHex("705a10")), // orange
    Warn(Colors.byHex("cb3412"), Colors.byHex("651a09")), // red
    Fatal(Colors.byHex("9512cb"), Colors.byHex("4a0965")); // lila
}
