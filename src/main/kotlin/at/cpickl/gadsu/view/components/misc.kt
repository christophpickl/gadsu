package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

val SWING_log = LoggerFactory.getLogger("at.cpickl.gadsu.view.components.SWING")

class SwingFactory @Inject constructor(
        val bus: EventBus,
        val clock: Clock
) {
    val log = LoggerFactory.getLogger(javaClass)

    // via extension methods
}


open class GridPanel : JPanel() {
    val c = GridBagConstraints()
    init {
        val gridBagLayout = GridBagLayout()
        layout = gridBagLayout
        gridBagLayout.setConstraints(this, c)

        c.gridx = 0
        c.gridy = 0
    }

    override fun add(comp: Component): Component? {
        super.add(comp, c)
        return null
    }

}

fun JTextField.addChangeListener(listener: () -> Unit) {
    document.addDocumentListener(object : DocumentListener {
        override fun changedUpdate(e: DocumentEvent) {
            listener()
        }
        override fun insertUpdate(e: DocumentEvent) {
            listener()
        }
        override fun removeUpdate(e: DocumentEvent) {
            listener()
        }

    })
}
