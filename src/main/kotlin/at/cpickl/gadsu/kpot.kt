package at.cpickl.gadsu

import com.github.christophpickl.kpotpourri.common.logging.LOG
import java.awt.Color
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.KeyEvent
import java.util.regex.Pattern
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.UIManager

private val log = LOG {}

// OUTSOURCE to kpotpourri

// ----- COMMON4k

val mailPattern = Pattern.compile("""^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$""")
fun String.isNotValidMail() = !this.isValidMail()
fun String.isValidMail(): Boolean {
    return mailPattern.matcher(this).matches()
}


fun firstNotEmpty(vararg strings: String) = strings.toList().firstOrNull { it.isNotEmpty() } ?: ""

annotation class KotlinNoArg

fun <T> List<T>.duplicates(): List<T> {
    val foundDuplicates = LinkedHashSet<T>()
    val tmp = mutableSetOf<T>()
    forEach {
        if (!tmp.add(it)) {
            foundDuplicates += it
        }
    }
    return foundDuplicates.toList()
}

// ----- SWING4k

// ===> package at.cpickl.gadsu.view.swing

fun initSwingLookAndFeel() {
    JFrame.setDefaultLookAndFeelDecorated(true)
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        log.error("Could not set native look&feel!", e)
    }
}

fun <T : JTextField> T.validateMailEntered() = apply {
    val originalForeground = foreground
    addFocusListener(object : FocusAdapter() {
        override fun focusLost(e: FocusEvent?) {
            val txt = text
            foreground = if (txt.isEmpty() || txt.isValidMail()) {
                originalForeground
            } else {
                Color.RED
            }
        }
    })
}

val KeyEvent.isEnter get() = keyCode == KeyEvent.VK_ENTER
val KeyEvent.isEscape get() = keyCode == KeyEvent.VK_ESCAPE
val KeyEvent.isW get() = keyCode == KeyEvent.VK_W

fun JLabel.color(newForeground: Color) = apply {
    foreground = newForeground
}
