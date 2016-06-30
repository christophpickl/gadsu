package at.cpickl.gadsu.view.logic

import org.slf4j.LoggerFactory
import java.awt.Toolkit

private val LOG_misc = LoggerFactory.getLogger("at.cpickl.gadsu.view.logic.misc.kt")

/**
 * Limit as defined in SQL, used for JTextField.
 */
val MAX_FIELDLENGTH_SHORT = 128

/**
 * Limit as defined in SQL, used for JTextArea.
 */
val MAX_FIELDLENGTH_LONG = 1024

fun beep() {
    LOG_misc.trace("beep()")
    Toolkit.getDefaultToolkit().beep()
}
