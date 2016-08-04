package at.cpickl.gadsu.view

import java.awt.Color
import javax.swing.UIManager

object Colors {

    // used for ALTERNATE_BG_COLOR in lists
    val LIGHT_GRAY = byHex("#F2F2F2")

    val SELECTED_BG = UIManager.getColor("List.selectionBackground")
    val SELECTED_AND_HOVERED_BG = SELECTED_BG.brighter()
    val SELECTED_FG = UIManager.getColor("List.selectionForeground")

    val BG_ALTERNATE = Colors.LIGHT_GRAY
    val BG_COLOR_HOVER = byHex("#bed9fe")

    fun byHex(hex: String) = Color.decode(hex)!!
}
/*
Color aColor = new Color(0xFF0096); // Use the hex number syntax
// Alternatively, use Color.decode
Color bColor = Color.decode("FF0096");
 */

fun Color.toHexString() = Integer.toHexString(this.rgb).substring(2)
// String hex = String.format("#%02x%02x%02x", r, g, b);



fun Color.brighterIfTrue(value: Boolean): Color {
    if (value) {
        return brighter()
    }
    return this
}
