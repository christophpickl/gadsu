package at.cpickl.gadsu.view.components.inputs

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javax.swing.text.JTextComponent

private val log = LOG {}

fun JTextComponent.removeLastChar() {
    if (document.length == 0) {
        log.warn { "Tried to remove last char but length is empty from: $this" }
        return
    }
    document.remove(document.length - 1, 1)
}
