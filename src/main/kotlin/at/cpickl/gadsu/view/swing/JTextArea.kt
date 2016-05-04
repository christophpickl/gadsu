package at.cpickl.gadsu.view.swing

import at.cpickl.gadsu.view.logic.beep
import javax.swing.JTextArea
import javax.swing.text.AttributeSet
import javax.swing.text.PlainDocument

/**
 * @see JTextField.enforceMaxCharacters
 */
fun JTextArea.enforceMaxCharacters(enforcedLength: Int) {
    document = object: PlainDocument() {
        override fun insertString(offset: Int, string: String, attributes: AttributeSet?) {
            if (text.length + string.length <= enforcedLength) {
                super.insertString(offset, string, attributes)
            } else {
                beep()
            }
        }
    }
}
