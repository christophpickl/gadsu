package at.cpickl.gadsu.view.swing

import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.TitledBorder


fun JPanel.titledBorder(title: String) {
    border = BorderFactory.createTitledBorder(null, title, TitledBorder.CENTER, TitledBorder.TOP)
}

fun JPanel.transparent(): JPanel {
    isTransparent = true
    return this
}

fun JPanel.opaque(): JPanel {
    isOpaque = true
    return this
}
