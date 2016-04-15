package at.cpickl.gadsu.view

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.Point
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.WindowConstants

class SwingFactory @Inject constructor(
        val eventBus: EventBus,
        val clock: Clock
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // via extension methods

    fun createAndShowPopup(invoker: Component, point: Point, label: String, eventFunction: () -> UserEvent) {
        log.trace("createAndShowPopup(..)")
        val popup = JPopupMenu()
        val item = JMenuItem(label)
        item.addActionListener { eventBus.post(eventFunction()) }
        popup.add(item)
        popup.show(invoker, point.x, point.y)
    }
}

open class MyWindow(private val myTitle: String) : JFrame() {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        title = myTitle
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    }

    protected fun addCloseListener(body: () -> Unit) {
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(event: WindowEvent) {
                log.trace("windowClosing(event)")
                body()
            }
        })
    }

//    protected fun packAndShow(locationRelativeTo: Component? = null) {
//        pack()
//        setLocationRelativeTo(locationRelativeTo)
//        setVisible(true)
//    }

    protected fun hideAndClose() {
        setVisible(false)
        dispose()
    }

}

fun JFrame.packCenterAndShow() {
    pack()
    setLocationRelativeTo(null)
    setVisible(true)
}
