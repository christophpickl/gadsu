package at.cpickl.gadsu.view.swing

import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.text.JTextComponent

fun <T : JTextComponent> T.disabled(): T {
    isEditable = false
    isEnabled = false
    return this
}

fun <T : JTextComponent> T.selectAllOnFocus(): T {
    addFocusListener(object : FocusListener {
        override fun focusGained(e: FocusEvent) {
            selectAll()
        }
        override fun focusLost(e: FocusEvent) { }
    })
    return this
}

fun <T : JTextComponent> T.disableFocusable(): T {
    this.isFocusable = false
    return this
}
