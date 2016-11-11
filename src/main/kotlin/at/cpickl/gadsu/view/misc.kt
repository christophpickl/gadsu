package at.cpickl.gadsu.view

import org.slf4j.LoggerFactory
import java.awt.Graphics
import javax.swing.FocusManager
import javax.swing.JFrame

private val log = LoggerFactory.getLogger(JFrame::class.java)

fun currentActiveJFrame(): JFrame? {
    val win = FocusManager.getCurrentManager().activeWindow ?: return null
    if (win is JFrame) {
        return win
    }
//        throw GadsuException("Current active window is not a JFrame but: $win")
    log.warn("Could not determine currentActiveJFrame() as the currently focused window was not of type JFrame but a: {}", win)
    return null
}
