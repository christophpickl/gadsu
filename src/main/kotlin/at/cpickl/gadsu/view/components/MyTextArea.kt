package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.view.logic.MAX_FIELDLENGTH_LONG
import at.cpickl.gadsu.view.swing.enforceMaxCharacters
import javax.swing.JLabel
import javax.swing.JTextArea

open class MyTextArea constructor(
        viewName: String,
        visibleRows: Int? = null
) : JTextArea() {
    init {
        name = viewName
        if (visibleRows != null) {
            rows = visibleRows
        }

//        wrapStyleWord = true
        enforceMaxCharacters(MAX_FIELDLENGTH_LONG)
        lineWrap = true

        if (IS_OS_WIN) {
            font = JLabel().font
        }
    }
}
