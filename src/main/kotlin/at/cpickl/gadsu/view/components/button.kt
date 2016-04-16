package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.UserEvent
import com.google.common.eventbus.EventBus
import java.awt.Dimension
import javax.swing.JButton


class EventButton(label: String, pName: String, eventBuilder: () -> UserEvent, private val eventBus: EventBus) : JButton(label) {
    init {
        addActionListener { eventBus.post(eventBuilder()) }
        name = pName
    }
}

fun SwingFactory.newEventButton(label: String, name: String, eventBuilder: () -> UserEvent) = EventButton(label, name, eventBuilder, bus)


fun JButton.changeSize(size: Dimension) {
    preferredSize = size
    minimumSize = size
    maximumSize = size
}
