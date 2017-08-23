package at.cpickl.gadsu.view.logic

import at.cpickl.gadsu.EnumBase
import at.cpickl.gadsu.Labeled
import at.cpickl.gadsu.Ordered
import at.cpickl.gadsu.SqlEnum
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.view.swing.elementAtPoint
import com.google.common.eventbus.EventBus
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JList
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

fun <T> JList<T>.addKPopup(withMenu: JPopupMenu.(T) -> Unit) {
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
                val (_, element) = elementAtPoint(e.point) ?: return

                val menu = JPopupMenu()
                withMenu(menu, element)
                menu.show(list, e.point.x, e.point.y)
            }
        }
    })
}

fun JPopupMenu.addKPopupItem(bus: EventBus, label: String, eventBuilder: () -> UserEvent) {
    add(JMenuItem(label).apply {
        addActionListener { bus.post(eventBuilder()) }
    })
}

fun JMenu.addKPopupItem(bus: EventBus, label: String, eventBuilder: () -> UserEvent, withItem: JMenuItem.() -> Unit = {}) {
    add(JMenuItem(label).apply {
        addActionListener { bus.post(eventBuilder()) }
        withItem(this)
    })
}

fun <T, E : EnumBase<T>> JPopupMenu.addEnumMenu(
        bus: EventBus,
        topLabel: String,
        enums: E,
        fnCreateEvent: (T) -> UserEvent,
        fnClientPropExtractor: () -> T
) where T : Ordered, T : SqlEnum, T : Labeled {
    add(JMenu(topLabel).apply {
        enums.orderedValues.forEach { enum ->
            addKPopupItem(bus, enum.label, { fnCreateEvent(enum) }) {
                isEnabled = enum != fnClientPropExtractor()
            }
        }
    })
}
