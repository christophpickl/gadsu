package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.view.swing.disableFocusable
import javax.swing.JTextField


class DisabledTextField(initialValue: String = ""): JTextField(initialValue) {
    init {
        isEnabled = false
        disableFocusable()
    }
}
