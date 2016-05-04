package at.cpickl.gadsu.view.logic

import java.awt.Toolkit

/**
 * Limit as defined in SQL, used for JTextField.
 */
val MAX_FIELDLENGTH_SHORT = 128

/**
 * Limit as defined in SQL, used for JTextArea.
 */
val MAX_FIELDLENGTH_LONG = 1024

fun beep() {
    Toolkit.getDefaultToolkit().beep()
}
