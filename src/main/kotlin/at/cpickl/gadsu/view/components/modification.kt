package at.cpickl.gadsu.view.components

import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.event.ItemEvent
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
    private val log = LoggerFactory.getLogger(javaClass)
    var enableModificationsCheck: Boolean = true

    fun <T : JTextComponent> enableChangeListener(delegate: T): T {
        delegate.addChangeListener { event ->
//            log.trace("JTextComponent fired change event. (Event: {}, Source: {})", event, delegate)
            checkModificationsAndUpdateIsEnabledField()
        }
        return delegate
    }

    fun enableChangeListener(delegate: MyDatePicker): MyDatePicker {
        delegate.addChangeListener {
            checkModificationsAndUpdateIsEnabledField()
        }
        return delegate
    }

    fun <T : Comparable<T>> enableChangeListener(delegate: MyList<T>): MyList<T> {
        delegate.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                checkModificationsAndUpdateIsEnabledField()
            }
        }
        return delegate
    }

    fun <C : Labeled> enableChangeListener(delegate: MyComboBox<C>): MyComboBox<C> {
//        delegate.addActionListener({checkModificationsAndUpdateIsEnabledField()}) ... maybe this would work as well
        delegate.addItemListener { event ->
            if (event.stateChange == ItemEvent.SELECTED) {
                checkModificationsAndUpdateIsEnabledField()
            }
        }
        return delegate
    }

    fun trigger() {
        log.debug("trigger()")
        checkModificationsAndUpdateIsEnabledField()
    }

    fun disableAll() {
        log.debug("disableAll()")
        enableDisableComponents.forEach { it.isEnabled = false }
    }

    private fun checkModificationsAndUpdateIsEnabledField() {
        if (!enableModificationsCheck) {
            log.trace("checkModificationsAndUpdateIsEnabledField() ... disabled, most likely currently in update method")
            return
        }

        log.trace("checkModificationsAndUpdateIsEnabledField()")
        val modified = modificationAware.isModified()
        enableDisableComponents.forEach {
            it.isEnabled = modified
        }
    }

}

fun JTextComponent.addChangeListener(listener: (DocumentEvent) -> Unit) {
    document.addDocumentListener(object : DocumentListener {
        override fun changedUpdate(e: DocumentEvent) {
            listener(e)
        }
        override fun insertUpdate(e: DocumentEvent) {
            listener(e)
        }
        override fun removeUpdate(e: DocumentEvent) {
            listener(e)
        }

    })
}
