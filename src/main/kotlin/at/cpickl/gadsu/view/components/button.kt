package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.service.Persistable
import at.cpickl.gadsu.view.Labels
import com.google.common.eventbus.EventBus
import java.awt.Dimension
import javax.swing.JButton


open class EventButton(label: String, viewName: String, eventBuilder: () -> UserEvent, private val eventBus: EventBus) : JButton(label) {
    init {
        addActionListener { eventBus.post(eventBuilder()) }
        name = viewName
    }
}
fun SwingFactory.newEventButton(label: String, viewName: String, eventBuilder: () -> UserEvent) = EventButton(label, viewName, eventBuilder, bus)


class PersistableEventButton(viewName: String, eventBuilder: () -> UserEvent, eventBus: EventBus) :
        EventButton(Labels.Buttons.Insert, viewName, eventBuilder, eventBus) {

    fun changeLabel(persistable: Persistable) {
        text = if (persistable.yetPersisted) Labels.Buttons.Update else Labels.Buttons.Insert
    }
}
fun SwingFactory.newPersistableEventButton(viewName: String, eventBuilder: () -> UserEvent) = PersistableEventButton(viewName, eventBuilder, bus)




fun JButton.changeSize(size: Dimension) {
    preferredSize = size
    minimumSize = size
    maximumSize = size
}
