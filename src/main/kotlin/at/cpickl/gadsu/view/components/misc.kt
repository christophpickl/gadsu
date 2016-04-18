package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Component
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.WindowConstants
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

val SWING_log = LoggerFactory.getLogger("at.cpickl.gadsu.view.components.SWING")

fun showFramed(vararg components: Component) {
    val frame = JFrame()
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.contentPane.layout = BoxLayout(frame.contentPane, BoxLayout.X_AXIS)
    components.forEach { frame.contentPane.add(it) }
    frame.packCenterAndShow()
}

class SwingFactory @Inject constructor(
        val bus: EventBus,
        val clock: Clock
) {
    val log = LoggerFactory.getLogger(javaClass)

    fun newTextArea(viewName: String, initialText: String, enableOn: ModificationChecker): JTextArea {
        val text = JTextArea()
        text.name = viewName
        text.text = initialText
        enableOn.enableChangeListener(text)
        return text
    }

    // via extension methods
}

fun JTextComponent.addChangeListener(listener: () -> Unit) {
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

fun JComponent.scrolled(): JComponent = JScrollPane(this)

interface IndexableModel<E> {
    val indexableSize: Int
    fun indexableElementAt(index: Int): E

}
fun <E : Comparable<E>> MyListModel<E>.calculateInsertIndex(value: E): Int {
    return _calculateInsertIndex(this, value)
}
fun <E : Comparable<E>> MyTableModel<E>.calculateInsertIndex(value: E): Int {
    return _calculateInsertIndex(this, value)
}


private fun <E : Comparable<E>> _calculateInsertIndex(model: IndexableModel<E>, value: E): Int {
    var index = 0
    for (i in 0.rangeTo(model.indexableSize - 1)) {
        val e = model.indexableElementAt(i)
        if (value.compareTo(e) > 0) {
            index++
        } else {
            break
        }
    }
    return index
}

fun <E> MyListModel<E>.findIndexByComparator(comparator: (current: E) -> Boolean): Int {
    return _findIndexByComparator(this, comparator)
}
fun <E> MyTableModel<E>.findIndexByComparator(comparator: (current: E) -> Boolean): Int {
    return _findIndexByComparator(this, comparator)
}

fun <E> _findIndexByComparator(model: IndexableModel<E>, comparator: (current: E) -> Boolean): Int {
    for (i in 0.rangeTo(model.indexableSize- 1)) {
        val c = model.indexableElementAt(i)
        if (comparator(c)) {
            return i
        }
    }
    throw GadsuException("Could not determine index of list entry!")
}

/*

fun <E> ListModel<E>.findIndexByComparator(comparator: (current: E) -> Boolean): Int {
    for (i in 0.rangeTo(size - 1)) {
        val c = getElementAt(i)
        if (comparator(c)) {
            return i
        }
    }
    throw GadsuException("Could not determine index of list entry!")
}

 */

interface ModificationAware {
    fun isModified(): Boolean
}

class ModificationChecker(
        private val modificationAware: ModificationAware,
        private vararg val enableDisableComponents: Component
) {

    fun <T : JTextComponent> enableChangeListener(delegate: T): T {
        delegate.addChangeListener { checkModificationsAndSetEnabled() }
        return delegate
    }

    fun enableChangeListener(delegate: MyDatePicker): MyDatePicker {
        // FIXME implement me
        return delegate
    }

    fun trigger() {
        checkModificationsAndSetEnabled()
    }

    fun disableAll() {
        enableDisableComponents.forEach { it.isEnabled = false }
    }

    private fun checkModificationsAndSetEnabled() {
        val modified = modificationAware.isModified()
        enableDisableComponents.forEach {
            it.isEnabled = modified
        }
    }

}

