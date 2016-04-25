package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
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
import javax.swing.JTextArea

val SWING_log = LoggerFactory.getLogger("at.cpickl.gadsu.view.components.SWING")


class SwingFactory @Inject constructor(
        val bus: EventBus,
        val clock: Clock
) {
    val log = LoggerFactory.getLogger(javaClass)

    fun newTextArea(viewName: String, initialText: String, enableOn: ModificationChecker): JTextArea {
        val text = JTextArea()
        text.name = viewName
        text.text = initialText
        enableOn.enableChangeListener(text)
        return text
    }

    // via extension methods
}

fun JComponent.scrolled(): JScrollPane = JScrollPane(this)


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


fun <T : JComponent> T.enforceWidth(myWidth: Int): T {
    size = Dimension(myWidth, size.height)
    maximumSize = Dimension(myWidth, maximumSize.height)
    minimumSize = Dimension(myWidth, minimumSize.height)
    preferredSize = Dimension(myWidth, preferredSize.height)
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
