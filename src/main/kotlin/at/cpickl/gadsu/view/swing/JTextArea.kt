package at.cpickl.gadsu.view.swing

import at.cpickl.gadsu.view.components.RichTextArea
import at.cpickl.gadsu.view.logic.beep
import javax.swing.text.AttributeSet
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.JTextComponent
import javax.swing.text.PlainDocument

/**
 * see JTextField.enforceMaxCharacters
 */
fun JTextComponent.enforceMaxCharacters(enforcedLength: Int) {
    document = object: PlainDocument() {
        override fun insertString(offset: Int, string: String, attributes: AttributeSet?) {
            if (text.length + string.length <= enforcedLength) {
                super.insertString(offset, string, attributes)
            } else {
                onTriedToInsertTooManyChars()
            }
        }
    }
}

// its not 100% perfect, but it is sufficient for now
// some troubles with when chars are already near max and formatting chars doesnt get triggered properly
fun RichTextArea.enforceMaxCharacters(enforcedLength: Int) {
    document = object : DefaultStyledDocument() {
        override fun insertString(offset: Int, string: String, attributes: AttributeSet?) {
            val enrichedText = toEnrichedText()
            if (enrichedText.length + string.length <= enforcedLength) {
                super.insertString(offset, string, attributes)
            } else {
                onTriedToInsertTooManyChars()
            }
        }
    }
}

fun onTriedToInsertTooManyChars() {
    beep()
}
