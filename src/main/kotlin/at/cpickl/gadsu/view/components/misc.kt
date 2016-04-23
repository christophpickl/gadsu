package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
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
    maximumSize = Dimension(myWidth, Int.MAX_VALUE)
    minimumSize = Dimension(myWidth, 50)
    preferredSize = Dimension(myWidth, 80)
    return this
}
