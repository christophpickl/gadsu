package at.cpickl.gadsu.view.swing

import com.github.christophpickl.kpotpourri.common.logging.LOG
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

private val log = LOG {}

fun <T : JTextComponent> T.disabled(): T {
    isEditable = false
    isEnabled = false
    return this
}

fun <T : JTextComponent> T.selectAllOnFocus(): T {
    addFocusListener(object : FocusAdapter() {
        override fun focusGained(e: FocusEvent) {
            log.trace("focusGained() select all")
            selectAll()
        }
    })
    return this
}
fun <T : JTextComponent> T.clearSelectionOnFocusLost(): T {
    addFocusListener(object : FocusAdapter() {
        override fun focusLost(e: FocusEvent?) {
            log.trace("focusLost() clear selection")
            select(selectionEnd, selectionEnd)
        }
    })
    return this
}

fun <T : JTextField> T.leftAligned(): T {
    horizontalAlignment = JTextField.LEFT
    return this
}

fun <T : JTextField> T.rightAligned(): T {
    horizontalAlignment = JTextField.RIGHT
    return this
}

fun <T : JTextComponent> T.disableFocusable(): T {
    this.isFocusable = false
    return this
}


typealias TextChangeListener = (String) -> Unit

class TextChangeDispatcher(private val field: JTextField) {

    private val listeners = mutableListOf<TextChangeListener>()

    init {
        field.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent) {
                listeners.forEach { it(field.text) }
            }

            override fun insertUpdate(e: DocumentEvent) {
                changedUpdate(e)
            }

            override fun removeUpdate(e: DocumentEvent) {
                changedUpdate(e)
            }
        })
    }

    fun addListener(listener: TextChangeListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: TextChangeListener) {
        listeners.remove(listener)
    }
}
