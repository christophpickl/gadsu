package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.Development
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.view.MainWindow
import at.cpickl.gadsu.view.SwingFactory
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

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

fun JTextField.addChangeListener(listener: () -> Unit) {
    document.addDocumentListener(object : DocumentListener {
        override fun changedUpdate(e: DocumentEvent) {
            listener()
        }
        override fun insertUpdate(e: DocumentEvent) {
            listener()
        }
        override fun removeUpdate(e: DocumentEvent) {
            listener()
        }

    })
}

val <T> JList<T>.log: Logger
    get() = LoggerFactory.getLogger(JList::class.java)

fun <T> JList<T>.myLocationToIndex(point: Point): Int {
    // val index = list.locationToIndex(point) // returns _closest_ index! :(
    for (i in 0.rangeTo(model.size - 1)) {
        val bounds = getCellBounds(i, i)
        if (point.y <= bounds.y + bounds.height) {
            return i
        }
    }
    log.debug("No cell found for given point: {}", point)
    return -1
}

// MINOR SwingFactory reference is very ugly :(
fun <T> JList<T>.enablePopup(swing: SwingFactory, label: String, eventProvider: (element: T) -> UserEvent) {
    val list = this // TODO i dont know kotlin!!! https://kotlinlang.org/docs/reference/this-expressions.html
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
                swing.createAndShowPopup(list, e.point, label, { eventProvider(element) })
            }
        }


    })
}

enum class DialogType(val swingConstant: Int) {
    PLAIN(-1),
    INFO(1),
    WARN(2),
    QUESTION(3),
    ERROR(0)
}

class Dialogs @Inject constructor(
        private val window: MainWindow
) {
    /**
     * @param buttonLabels for each button its label
     * @param defaultButton if null, the first option of buttonLabels will be used
     * @return the label which was selected or null if user just hit the close button
     */
    fun show(title: String, message: String, buttonLabels: Array<String>, defaultButton: String? = null, type: DialogType = DialogType.PLAIN): String? {
        val selected = JOptionPane.showOptionDialog(window.asComponent(), message, title,
                JOptionPane.DEFAULT_OPTION, type.swingConstant, null, buttonLabels, defaultButton?:buttonLabels[0])
        if (selected == JOptionPane.CLOSED_OPTION) {
            return null
        }
        return buttonLabels[selected]
    }

}
