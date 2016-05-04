package at.cpickl.gadsu.view.swing

import java.awt.GridBagConstraints


fun GridBagConstraints.fatComponent() {
    fill = GridBagConstraints.BOTH
    weightx = 1.0
    weighty = 1.0
}

fun GridBagConstraints.weightxy(value: Double) {
    weightx = value
    weighty = value
}
