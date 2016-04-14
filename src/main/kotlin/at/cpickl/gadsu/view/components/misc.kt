package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.view.SwingFactory
import com.google.common.eventbus.EventBus
import javax.swing.AbstractListModel
import javax.swing.JButton

class SimpleListModel<T>(private val values: List<T>) : AbstractListModel<T>() {
    override fun getElementAt(index: Int): T = values[index]
    override fun getSize(): Int = values.size
}


class EventButton(label: String, eventBuilder: () -> UserEvent, private val eventBus: EventBus) : JButton(label) {
    init {
        addActionListener { eventBus.post(eventBuilder()) }
    }
}

fun SwingFactory.newEventButton(label: String, eventBuilder: () -> UserEvent) = EventButton(label, eventBuilder, eventBus)
