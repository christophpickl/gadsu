package at.cpickl.gadsu.view.swing

import java.awt.Container
import java.awt.Window
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JComponent
import javax.swing.JFrame

interface ClosableWindow {
    fun getContentPane(): Container // every JFrame got it ;)
    fun closeWindow()
}

fun <W> W.registerOnCloseWindow() where W : Window, W : ClosableWindow {
    addCloseListener {
        closeWindow()
    }
}

fun <W : ClosableWindow> W.registerCloseOnEscape(): W {
    val component = getContentPane() as JComponent // hacky-da-hack ;)
    component.registerMyKeyListener(MyKeyListener.onEscape("disposeMyWindow", { closeWindow() }))
//    val inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
//    val actionMap = component.actionMap
//
//    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "dispose")
//    actionMap.put("dispose", object : AbstractAction() {
//        override fun actionPerformed(e: ActionEvent?) {
//            closeWindow()
//        }
//    })
    return this
}

fun JFrame.packCenterAndShow() {
    pack()
    setLocationRelativeTo(null)
    isVisible = true
}


fun Window.addCloseListener(body: () -> Unit) {
    addWindowListener(object : WindowAdapter() {
        override fun windowClosing(event: WindowEvent) {
            body()
        }
    })
}
