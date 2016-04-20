package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
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


object Pad {
    val ZERO   = Insets(0, 0, 0, 0)
    val TOP    = Insets(5, 0, 0, 0)
    val LEFT   = Insets(0, 5, 0, 0)
    val BOTTOM = Insets(0, 0, 5, 0)
    val RIGHT  = Insets(0, 0, 0, 5)
}


fun GridBagConstraints.fatComponent() {
    fill = GridBagConstraints.BOTH
    weightx = 1.0
    weighty = 1.0
}
