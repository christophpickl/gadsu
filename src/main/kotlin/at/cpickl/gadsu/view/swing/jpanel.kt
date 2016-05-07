package at.cpickl.gadsu.view.swing

import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.TitledBorder


fun JPanel.titledBorder(title: String) {
    border = BorderFactory.createTitledBorder(null, title, TitledBorder.LEFT, TitledBorder.ABOVE_TOP)
}

fun JPanel.opaque(): JPanel {
    isOpaque = true
    return this
}
