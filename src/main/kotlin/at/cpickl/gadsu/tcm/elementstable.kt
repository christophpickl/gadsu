package at.cpickl.gadsu.tcm

import at.cpickl.gadsu.global.QuitEvent
import at.cpickl.gadsu.global.UserEvent
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.swing.ClosableWindow
import at.cpickl.gadsu.view.swing.registerCloseOnEscape
import com.google.common.eventbus.Subscribe
import java.awt.BorderLayout
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel

class ShowElementsTableViewEvent : UserEvent()

@Logged
open class ElementsTableController {

    private var window: ElementsTableWindow? = null

    @Subscribe open fun onShowElementsTableViewEvent(event: ShowElementsTableViewEvent) {
        if (window == null) {
            window = ElementsTableWindow()
        }
        window!!.isVisible = true
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        if (window != null) {
            window!!.destroyWindow()
            window = null
        }
    }

}

class ElementsTableWindow : MyFrame("Entsprechungstabelle"), ClosableWindow {

    init {
        registerCloseOnEscape()

        contentPane.add(JPanel(BorderLayout()).apply {
            add(JLabel(ImageIcon(ElementsTableWindow::class.java.getResource("/gadsu/images/elementstable.png"))), BorderLayout.CENTER)
        })
        pack()
        isResizable = false
    }

    override fun closeWindow() {
        isVisible = false
    }

    fun destroyWindow() {
        closeWindow()
        dispose()
    }
}
