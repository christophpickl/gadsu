package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.view.logic.MAX_FIELDLENGTH_LONG
import at.cpickl.gadsu.view.swing.enforceMaxCharacters
import at.cpickl.gadsu.view.swing.focusTraversalWithTabs
import org.openmechanics.htmleditor.HTMLEditor
import java.io.IOException
import java.io.StringReader
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.text.html.HTMLDocument
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.parser.ParserDelegator

open class MyTextArea constructor(
        viewName: String,
        visibleRows: Int? = null,
        maxChars: Int? = MAX_FIELDLENGTH_LONG
) : JTextArea() {
    init {
        focusTraversalWithTabs()
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

