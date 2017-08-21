package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.swing.enforceSize
import java.awt.Color
import java.awt.Graphics
import javax.inject.Inject
import javax.swing.JPanel


class ThresholdColorCalculator @Inject constructor(private val prefs: Prefs) {

    private val LIMIT_MODIFIER_A = 0.6
    private val LIMIT_MODIFIER_B = 1.0
    private val LIMIT_MODIFIER_C = 1.4

    fun calc(client: ExtendedClient): ThresholdColor {
        val days = client.differenceDaysToRecentTreatment!!
        val category = client.category
        val nextAppointment = client.upcomingAppointment

        // FIXME #112 implement me

//        if (nextAppointment != null) {
//            return ThresholdColor.GotNextAppointment
//        }
//        val limitModifier = if (category == ClientCategory.A) LIMIT_MODIFIER_A else if (category == ClientCategory.B) LIMIT_MODIFIER_B else LIMIT_MODIFIER_C
//
//        val limitOk = (RecentState.Ok.baseLimit * limitModifier).toInt()
//        val limitAttention = (RecentState.Attention.baseLimit * limitModifier).toInt()
//        val limitWarn = (RecentState.Warn.baseLimit * limitModifier).toInt()
//        val limitCritical = (RecentState.Critical.baseLimit * limitModifier).toInt()
//
//        return if (days < limitOk) RecentState.Ok
//        else if (days < limitAttention) RecentState.Attention
//        else if (days < limitWarn) RecentState.Warn
//        else if (days < limitCritical) RecentState.Critical
//        else RecentState.Fatal

        return ThresholdColor.Attention
    }
}


class RecentTreatmentPanel(daysSinceLastTreatment: Int, color: ThresholdColor) : JPanel() {
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
    private val colorFilling = color.color1
    private val colorBorder = color.color2

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

enum class ThresholdColor(val color1: Color, val color2: Color) {
    GotNextAppointment(Color.GRAY, Color.BLACK),
    Ok(Colors.byHex("02bb1c"), Colors.byHex("015d0e")), // green
    Attention(Colors.byHex("e0b520"), Colors.byHex("705a10")), // orange
    Warn(Colors.byHex("cb3412"), Colors.byHex("651a09")), // red
    Fatal(Colors.byHex("9512cb"), Colors.byHex("4a0965")); // lila
}
