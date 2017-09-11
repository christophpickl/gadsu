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

fun <W : ClosableWindow> W.registerCloseOnEscape(): W = apply {
    registerCloseOn(MyKeyListener.onEscape("disposeWindowOnEscape", { closeWindow() }))
}

fun <W : ClosableWindow> W.registerCloseOnEscapeOrShortcutW(): W = apply {
    registerCloseOn(MyKeyListener.onEscapeOrShortcutW("disposeWindowOnEscapeOrShortcutW", { closeWindow() }))
}

private fun <W : ClosableWindow> W.registerCloseOn(listener: MyKeyListener): W = apply {
    val component = getContentPane() as JComponent // hacky-da-hack ;)
    component.registerMyKeyListener(listener)
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
