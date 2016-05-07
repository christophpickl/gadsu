package at.cpickl.gadsu.view.swing

import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Point
import java.awt.event.InputEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.Timer
import java.util.TimerTask
import javax.swing.JComponent
import javax.swing.JScrollPane


fun JComponent.scrolled(hPolicy: Int? = null, vPolicy: Int? = null): JScrollPane {
    return JScrollPane(this).apply {
        if (hPolicy != null) horizontalScrollBarPolicy = hPolicy
        if (vPolicy != null) verticalScrollBarPolicy = vPolicy
    }
}


fun <T : JComponent> T.bold(): T {
    font = font.deriveFont(Font.BOLD)
    return this
}

fun <T : JComponent> T.italic(): T {
    font = font.deriveFont(Font.ITALIC)
    return this
}


fun JComponent.changeBackgroundForASec(tempBackground: Color) {
    background = tempBackground

    Timer("dev-blinking", true).schedule(object : TimerTask() {
        override fun run() {
            background = null
        }
    }, 1000L)
}


// MINOR @UI - this is some kind of a hacky method (height calculation does not work properly!)
fun <T : JComponent> T.enforceWidth(myWidth: Int): T {
    size = Dimension(myWidth, size.height)
    maximumSize = Dimension(myWidth, maximumSize.height)
    minimumSize = Dimension(myWidth, minimumSize.height)
    preferredSize = Dimension(myWidth, preferredSize.height)
    return this
}
fun <T : JComponent> T.enforceSize(myWidth: Int, myHeight: Int): T {
    size = Dimension(myWidth, size.height)
    maximumSize = Dimension(myWidth, myHeight)
    minimumSize = Dimension(myWidth, myHeight)
    preferredSize = Dimension(myWidth, myHeight)
    return this
}


fun Component.addSingleLeftClickListener(function: (Point) -> Unit) {
    addMouseListener(object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            val isLeftClick = (e.modifiers and InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK
            if (e.clickCount == 1 && isLeftClick) {
                function.invoke(e.point)
            }
        }
    })
}

var JComponent.isTransparent: Boolean
    get() { return !isOpaque }
    set(value) { isOpaque = !value }


fun <T : JComponent> T.transparent(): T {
    isTransparent = true
    return this
}
