package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.global.UserEvent
import at.cpickl.gadsu.persistence.Persistable
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.swing.changeSize
import com.google.common.eventbus.EventBus
import java.awt.Dimension
import javax.swing.JButton

val GADSU_BUTTON_WIDTH = 200

fun <B : JButton> B.gadsuWidth() = apply {
    changeSize(Dimension(GADSU_BUTTON_WIDTH, preferredSize.height))
}

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

