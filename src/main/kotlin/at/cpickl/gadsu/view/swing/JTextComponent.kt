package at.cpickl.gadsu.view.swing

import org.slf4j.LoggerFactory
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.text.JTextComponent

val LOG_JTextComponent = LoggerFactory.getLogger(JTextComponent::class.java)

fun <T : JTextComponent> T.disabled(): T {
    isEditable = false
    isEnabled = false
    return this
}

fun <T : JTextComponent> T.selectAllOnFocus(): T {
    addFocusListener(object : FocusAdapter() {
        override fun focusGained(e: FocusEvent) {
            LOG_JTextComponent.trace("focusGained; selectAll()")
            selectAll()
        }
    })
    return this
}

fun <T : JTextComponent> T.disableFocusable(): T {
    this.isFocusable = false
    return this
}
