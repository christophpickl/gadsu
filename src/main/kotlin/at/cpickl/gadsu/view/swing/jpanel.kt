package at.cpickl.gadsu.view.swing

import javax.swing.BorderFactory
import javax.swing.JPanel


fun JPanel.titledBorder(title: String) {
    border = BorderFactory.createTitledBorder(title)
}

fun JPanel.transparent(): JPanel {
    isTransparent = true
    return this
}

fun JPanel.opaque(): JPanel {
    isOpaque = true
    return this
}
