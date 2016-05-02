package at.cpickl.gadsu.view.logic

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.CreateNewClientEvent
import at.cpickl.gadsu.view.SWING_log
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.swing.elementAtPoint
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import java.awt.Component
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JList
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu

//<editor-fold desc="main">
fun main(args: Array<String>) {
    val dummyEvent = CreateNewClientEvent()

    Framed.showWithContext( { context ->
            context.bus.register(object: Any() {
                @Subscribe fun onEvent(event: Any) {
                    println("event dispatched: $event")
                }
            })
            val list = JList<String>(arrayOf("erster eintrag", "zweiter eintrag"))

//            list.enablePopup(context.bus, Pair("menu item 1", { element -> dummyEvent }))

        val menu1 = Pair("menu item 1 just for ERSTER", { dummyEvent })
        val menu2 = Pair("menu item 2", { dummyEvent })
            list.enableSmartPopup(context.bus, { selectedEntry ->
                when (selectedEntry) {
                    "erster eintrag" ->  listOf(menu1, menu2)
                    else -> listOf(menu2)
                }
            })
            val panel = JPanel()
            panel.add(list.scrolled())
            panel
    })
}
//</editor-fold>


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


fun <T> JList<T>.enableSmartPopup(bus: EventBus, entriesFunction: (element: T) -> List<Pair<String, () -> UserEvent>>) {
    _enablePopup(bus, entriesFunction)
}
fun <T> JList<T>.enablePopup(bus: EventBus, vararg entries: Pair<String, (element: T) -> UserEvent>) {

    val builderFunction = { element: T ->
        entries.map { Pair(it.first, { it.second.invoke(element) }) }.toList()
    }
    _enablePopup(bus = bus, entriesFunction = builderFunction)
}

private fun <T> JList<T>._enablePopup(bus: EventBus, entriesFunction: (element: T) -> List<Pair<String, () -> UserEvent>>) { //entries: List<Pair<String, (element: T) -> UserEvent>>) {
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
                val entries = entriesFunction.invoke(element)
                createAndShowPopup(bus, list, e.point, entries) //entries.map { Pair(it.first, {it.second.invoke(element)}) })
            }
        }
    })
}
