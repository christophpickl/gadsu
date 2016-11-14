package at.cpickl.gadsu.view

import java.awt.Graphics


fun Graphics.drawStringCentered(string: String, w: Int, h: Int, heightAdjuster: Int) {
    val metrics = getFontMetrics(font)
    val stringWidth = metrics.stringWidth(string)
    val x = (w - stringWidth) / 2
    val y = h - heightAdjuster // metrics.height does not work properly...

    drawString(string, x, y)
}
