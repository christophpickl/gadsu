package at.cpickl.gadsu.view.swing

import javax.swing.JLabel


fun JLabel.withFont(style: Int, size: Int): JLabel {
    font = font.deriveFont(style, size.toFloat())
    return this
}
