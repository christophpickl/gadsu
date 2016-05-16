package at.cpickl.gadsu.view.swing

import java.awt.Insets

object Pad {

    val DEFAULT_SIZE = 5

    val ZERO   = all(0)
    val NONE   = ZERO // alias

    val TOP    = top(DEFAULT_SIZE)
    val LEFT   = left(DEFAULT_SIZE)
    val BOTTOM = bottom(DEFAULT_SIZE)
    val RIGHT  = right(DEFAULT_SIZE)

    val LEFT_RIGHT = Insets(0, DEFAULT_SIZE, 0, DEFAULT_SIZE)
    val TOP_BOTTOM = Insets(DEFAULT_SIZE, 0, DEFAULT_SIZE, 0)

    fun all(size: Int) = Insets(size, size, size, size)
    fun top   (size: Int) = Insets(size, 0, 0, 0)
    fun left  (size: Int) = Insets(0, size, 0, 0)
    fun bottom(size: Int) = Insets(0, 0, size, 0)
    fun right (size: Int) = Insets(0, 0, 0, size)
}


fun Insets.addTop(increase: Int): Insets {
    return Insets(top + increase, left, bottom, right)
}

fun Insets.addLeft(increase: Int): Insets {
    return Insets(top, left + increase, bottom, right)
}

fun Insets.addBottom(increase: Int): Insets {
    return Insets(top, left, bottom + increase, right)
}

fun Insets.addRight(increase: Int): Insets {
    return Insets(top, left, bottom, right + increase)
}
