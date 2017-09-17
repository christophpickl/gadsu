package at.cpickl.gadsu.view.logic

import at.cpickl.gadsu.global.Labeled
import at.cpickl.gadsu.view.ElRichTextArea
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.RichTextArea
import at.cpickl.gadsu.view.components.ShortcutEvent
import at.cpickl.gadsu.view.components.ShortcutListener
import at.cpickl.gadsu.view.components.inputs.MyCheckBox
import at.cpickl.gadsu.view.components.inputs.MyComboBox
import at.cpickl.gadsu.view.datepicker.view.MyDatePicker
import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.event.ItemEvent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent


interface ModificationAware {
    fun isModified(): Boolean
}

interface ChangeAware {
    fun onChange(changeListener: () -> Unit)
}

open class ModificationChecker(
        private val modificationAware: ModificationAware,
        private vararg val enableDisableComponents: Component
) {
    private val log = LoggerFactory.getLogger(javaClass)
    var enableModificationsCheck: Boolean = true

    fun <T : JTextComponent> enableChangeListener(delegate: T): T {
        // MINOR check if adding without removing listener does not lead to memory leak
        delegate.addChangeListener {
//            log.trace("JTextComponent fired change event. (Event: {}, Source: {})", event, delegate)
            checkModificationsAndUpdateIsEnabledField()
        }
        if (delegate is ElRichTextArea<*>) {
            delegate.registerListener(object : ShortcutListener {
                override fun onShortcut(event: ShortcutEvent) {
                    checkModificationsAndUpdateIsEnabledField()
                }
            })
        }
        return delegate
    }

    fun enableChangeListener(delegate: MyDatePicker): MyDatePicker {
        delegate.addChangeListener {
            checkModificationsAndUpdateIsEnabledField()
        }
        return delegate
    }

    fun enableChangeListener(delegate: MyCheckBox): MyCheckBox {
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

    fun <C : ChangeAware> enableChangeListener(changeAware: C): C {
        changeAware.onChange {
            checkModificationsAndUpdateIsEnabledField()
        }
        return changeAware
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
            return
        }

//        log.trace("checkModificationsAndUpdateIsEnabledField()")
        val modified = modificationAware.isModified()
        enableDisableComponents.forEach {
            it.isEnabled = modified
        }
    }

}

fun JTextComponent.addChangeListener(listener: (DocumentEvent) -> Unit) {
    val thiz = this
    document.addDocumentListener(object : DocumentListener {
        override fun changedUpdate(e: DocumentEvent) {
            listener(e)
        }

        override fun insertUpdate(e: DocumentEvent) {
            if (thiz is RichTextArea && thiz.isReformatting) {
                return
            }
            listener(e)
        }

        override fun removeUpdate(e: DocumentEvent) {
            if (thiz is RichTextArea && thiz.isReformatting) {
                return
            }
            listener(e)
        }

    })
}
