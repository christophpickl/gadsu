package at.cpickl.gadsu.view.swing

import java.awt.Insets

object Pad {
    val ZERO   = Insets(0, 0, 0, 0)
    val NONE   = ZERO
    val TOP    = Insets(5, 0, 0, 0)
    val LEFT   = Insets(0, 5, 0, 0)
    val BOTTOM = Insets(0, 0, 5, 0)
    val RIGHT  = Insets(0, 0, 0, 5)

    val LEFTRIGHT= Insets(0, 5, 0, 5)
}


fun Insets.increaseLeft(increaseLeftBy: Int): Insets {
    return Insets(top, left + increaseLeftBy, bottom, right)
}
