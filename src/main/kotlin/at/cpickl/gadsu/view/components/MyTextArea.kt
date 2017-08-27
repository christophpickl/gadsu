package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.view.logic.MAX_FIELDLENGTH_LONG
import at.cpickl.gadsu.view.swing.clearSelectionOnFocusLost
import at.cpickl.gadsu.view.swing.enforceMaxCharacters
import at.cpickl.gadsu.view.swing.focusTraversalWithTabs
import javax.swing.JLabel
import javax.swing.JTextArea

open class MyTextArea constructor(
        viewName: String,
        visibleRows: Int? = null,
        maxChars: Int? = MAX_FIELDLENGTH_LONG
) : JTextArea() {
    init {
        focusTraversalWithTabs()
        clearSelectionOnFocusLost()
        name = viewName
        if (visibleRows != null) {
            rows = visibleRows
        }

        lineWrap = true
        wrapStyleWord = true
        if (maxChars != null) {
            enforceMaxCharacters(maxChars)
        }

        if (IS_OS_WIN) {
            font = JLabel().font
        }

    }
}

