package at.cpickl.gadsu.view.components

import java.awt.Component
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent


interface ModificationAware {
    fun isModified(): Boolean
}

class ModificationChecker(
        private val modificationAware: ModificationAware,
        private vararg val enableDisableComponents: Component
) {

    fun <T : JTextComponent> enableChangeListener(delegate: T): T {
        delegate.addChangeListener { checkModificationsAndSetEnabled() }
        return delegate
    }

    fun enableChangeListener(delegate: MyDatePicker): MyDatePicker {
        // FIXME implement me
        return delegate
    }

    fun trigger() {
        checkModificationsAndSetEnabled()
    }

    fun disableAll() {
        enableDisableComponents.forEach { it.isEnabled = false }
    }

    private fun checkModificationsAndSetEnabled() {
        val modified = modificationAware.isModified()
        enableDisableComponents.forEach {
            it.isEnabled = modified
        }
    }

}

fun JTextComponent.addChangeListener(listener: () -> Unit) {
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
