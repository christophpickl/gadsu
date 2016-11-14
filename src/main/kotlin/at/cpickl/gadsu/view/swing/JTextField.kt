package at.cpickl.gadsu.view.swing

import at.cpickl.gadsu.view.logic.beep
import java.util.regex.Pattern
import javax.swing.JTextField
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.DocumentFilter
import javax.swing.text.PlainDocument

fun JTextField.enforceMaxCharacters(enforcedLength: Int) {
    // instead of replace, maybe delegate to original document?!?
    document = object : PlainDocument() {
        override fun insertString(offset: Int, string: String?, attributes: AttributeSet?) {
            if (string == null) return
            if ((length + string.length) <= enforcedLength) {
                super.insertString(offset, string, attributes)
            } else {
                beep()
            }
        }
    }
}

fun JTextField.enforceCharactersByRegexp(regexp: Pattern) {
    (document as AbstractDocument).documentFilter = object : DocumentFilter() {
        override fun replace(fb: FilterBypass?, offset: Int, length: Int, text: String?, attrs: AttributeSet?) {
            if (!regexp.matcher(text).matches()) {
                return
            }
            super.replace(fb, offset, length, text, attrs)
        }
    }
}
