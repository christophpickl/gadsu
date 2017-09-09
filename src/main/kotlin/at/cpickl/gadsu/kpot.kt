package at.cpickl.gadsu

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javax.swing.JFrame
import javax.swing.UIManager

private val log = LOG {}

// OUTSOURCE to kpotpourri

// ----- COMMON4k

fun firstNotEmpty(vararg strings: String) = strings.toList().firstOrNull { it.isNotEmpty() } ?: ""

annotation class KotlinNoArg


// ----- SWING4k

fun initSwingLookAndFeel() {

    JFrame.setDefaultLookAndFeelDecorated(true)
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        log.error("Could not set native look&feel!", e)
    }
}

