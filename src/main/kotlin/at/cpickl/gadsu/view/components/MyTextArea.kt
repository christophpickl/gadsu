package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.view.logic.MAX_FIELDLENGTH_LONG
import at.cpickl.gadsu.view.swing.enforceMaxCharacters
import java.awt.KeyboardFocusManager
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextArea

open class MyTextArea constructor(
        viewName: String,
        visibleRows: Int? = null
) : JTextArea() {
    init {
        focusTraversalWithTabs()
        name = viewName
        if (visibleRows != null) {
            rows = visibleRows
        }

        lineWrap = true
        wrapStyleWord = true
        enforceMaxCharacters(MAX_FIELDLENGTH_LONG)

        if (IS_OS_WIN) {
            font = JLabel().font
        }

    }
}

fun <J : JComponent> J.focusTraversalWithTabs() = apply {
    setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null)
    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null)
}
