package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Font
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

