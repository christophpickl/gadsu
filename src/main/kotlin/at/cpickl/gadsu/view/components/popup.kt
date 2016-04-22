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


fun <T> JList<T>.enablePopup(bus: EventBus, vararg entries: Pair<String, (element: T) -> UserEvent>) {
    val list = this
    addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            maybeShowPopup(e)
        }

        override fun mouseReleased(e: MouseEvent) {
            maybeShowPopup(e)
        }

        @Suppress("UNUSED_VARIABLE")
        private fun maybeShowPopup(e: MouseEvent) {
            if (e.isPopupTrigger) {
                val (index, element) = elementAtPoint(e.point) ?: return

                val rawifiedEntries = entries.map { Pair(it.first, { it.second(element) }) }.toList()
                createAndShowPopup(bus, list, e.point, rawifiedEntries)
            }
        }
    })
}

fun SwingFactory.createAndShowPopup(invoker: Component, point: Point, vararg entries: Pair<String, () -> UserEvent>) {
    createAndShowPopup(bus, invoker, point, entries.toList())
}

// label: String, eventFunction: () -> UserEvent
fun createAndShowPopup(bus: EventBus, invoker: Component, point: Point, entries: List<Pair<String, () -> UserEvent>>) {
    SWING_log.trace("createAndShowPopup(bus, invoker, point={}, entries={})", point, entries)
    val popup = JPopupMenu()

    for (entry in entries) {
        val label = entry.first
        val eventFunction = entry.second
        val item = JMenuItem(label)
        item.addActionListener { bus.post(eventFunction()) }
        popup.add(item)
    }

    popup.show(invoker, point.x, point.y)
}
