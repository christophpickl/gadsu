package at.cpickl.gadsu.view.swing

import java.util.regex.Pattern
import javax.swing.JTextField
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.DocumentFilter


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
