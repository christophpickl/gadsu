package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.swing.enforceSize
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

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
