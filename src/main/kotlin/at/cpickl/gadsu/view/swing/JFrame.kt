package at.cpickl.gadsu.view.swing

import java.awt.Window
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame


fun JFrame.packCenterAndShow() {
    pack()
    setLocationRelativeTo(null)
    setVisible(true)
}


fun Window.addCloseListener(body: () -> Unit) {
    addWindowListener(object : WindowAdapter() {
        override fun windowClosing(event: WindowEvent) {
            body()
        }
    })
}
