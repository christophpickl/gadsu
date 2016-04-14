package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.Development
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.view.SwingFactory
import com.google.common.eventbus.EventBus
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel

class EventButton(label: String, pName: String, eventBuilder: () -> UserEvent, private val eventBus: EventBus) : JButton(label) {
    init {
        addActionListener { eventBus.post(eventBuilder()) }
        name = pName
    }
}

fun SwingFactory.newEventButton(label: String, name: String, eventBuilder: () -> UserEvent) = EventButton(label, name, eventBuilder, eventBus)


open class GridPanel : JPanel() {
    protected val c = GridBagConstraints()
    init {
        if (Development.ENABLED) background = Color.GREEN

        val gridBagLayout = GridBagLayout()
        layout = gridBagLayout
        gridBagLayout.setConstraints(this, c)

        c.gridx = 0
        c.gridy = 0
    }

    override fun add(comp: Component): Component? {
        super.add(comp, c)
        return null
    }

}
