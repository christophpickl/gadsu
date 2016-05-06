package at.cpickl.gadsu.view

import at.cpickl.gadsu.GadsuException
import javax.swing.FocusManager
import javax.swing.JFrame

fun currentActiveJFrame(): JFrame? {
    val win = FocusManager.getCurrentManager().activeWindow
    if (win == null) {
        return null
    }
    if (win !is JFrame) {
        throw GadsuException("Current active window is not a JFrame but: $win")
    }
    return win
}
