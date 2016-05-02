package at.cpickl.gadsu.view.swing

import java.awt.Dimension
import javax.swing.JButton


fun JButton.changeSize(size: Dimension) {
    preferredSize = size
    minimumSize = size
    maximumSize = size
}
