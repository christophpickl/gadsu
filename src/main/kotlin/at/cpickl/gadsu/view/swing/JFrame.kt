package at.cpickl.gadsu.view.swing

import javax.swing.JFrame


fun JFrame.packCenterAndShow() {
    pack()
    setLocationRelativeTo(null)
    setVisible(true)
}
