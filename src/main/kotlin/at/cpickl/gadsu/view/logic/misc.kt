package at.cpickl.gadsu.view.logic

import at.cpickl.gadsu.view.UiTestEnabler
import org.slf4j.LoggerFactory
import java.awt.Toolkit

private val LOG_misc = LoggerFactory.getLogger("at.cpickl.gadsu.view.logic.misc.kt")

/**
 * Limit as defined in SQL, used for JTextField.
 */
val MAX_FIELDLENGTH_SHORT = 512

/**
 * Limit as defined in SQL, used for JTextArea.
 */
val MAX_FIELDLENGTH_LONG = 5120

private var hasBeeped = false

fun beep() {
    if (UiTestEnabler.isEnabled()) {
        LOG_misc.trace("beep() disabled during UI tests")
    } else {
        LOG_misc.trace("beep()")
        Toolkit.getDefaultToolkit().beep()
    }
    hasBeeped = true
}

fun consumeBeep(): Boolean {
    val result = hasBeeped
    hasBeeped = false
    return result
}
