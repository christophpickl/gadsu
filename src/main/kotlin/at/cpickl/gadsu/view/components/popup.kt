package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.UserEvent
import com.google.common.eventbus.EventBus
import java.awt.Component
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JList
import javax.swing.JMenuItem
import javax.swing.JPopupMenu


fun <T> JList<T>.enablePopup(bus: EventBus, label: String, eventProvider: (element: T) -> UserEvent) {
    val list = this
    addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            maybeShowPopup(e)
        }

        override fun mouseReleased(e: MouseEvent) {
            maybeShowPopup(e)
        }

        private fun maybeShowPopup(e: MouseEvent) {
            if (e.isPopupTrigger) {
                val index = myLocationToIndex(e.point)
                if (index == -1) {
                    return
                }
                val element = model.getElementAt(index)
                createAndShowPopup(bus, list, e.point, label, { eventProvider(element) })
            }
        }


    })
}

fun SwingFactory.createAndShowPopup(invoker: Component, point: Point, label: String, eventFunction: () -> UserEvent) {
    createAndShowPopup(bus, invoker, point, label, eventFunction)
}

fun createAndShowPopup(bus: EventBus, invoker: Component, point: Point, label: String, eventFunction: () -> UserEvent) {
    SWING_log.trace("createAndShowPopup(..)")
    val popup = JPopupMenu()
    val item = JMenuItem(label)
    item.addActionListener { bus.post(eventFunction()) }
    popup.add(item)
    popup.show(invoker, point.x, point.y)
}
