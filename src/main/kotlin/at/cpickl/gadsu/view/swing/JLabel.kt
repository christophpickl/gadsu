package at.cpickl.gadsu.view.swing

import javax.swing.JLabel

fun JLabel.withFontSize(size: Int) = this.apply { font = font.deriveFont(size.toFloat()) }
fun JLabel.withFont(style: Int, size: Int) = this.apply { font = font.deriveFont(style, size.toFloat()) }
