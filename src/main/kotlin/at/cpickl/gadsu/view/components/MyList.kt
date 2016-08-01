package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.service.HasId
import at.cpickl.gadsu.view.components.panels.SingleButtonPanel
import at.cpickl.gadsu.view.logic.*
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import javax.swing.DefaultListModel
import javax.swing.DefaultListSelectionModel
import javax.swing.JList
import javax.swing.JPanel


abstract class ListyView<T : Comparable<T>>(
        protected val list: MyList<T>,
        protected val createButton: EventButton
) : JPanel() {

    init {
        transparent()
        enforceWidth(250)
        layout = BorderLayout()

        add(list.scrolled(), BorderLayout.CENTER)
        add(SingleButtonPanel(createButton), BorderLayout.SOUTH)
    }
}

open class MyList<T : Comparable<T>>(
        viewName: String,
        protected val myModel: MyListModel<T>,
        private val bus: EventBus,
        myCellRenderer: MyListCellRenderer<T>? = null
) : JList<T>(myModel) {

    init {
        if (myCellRenderer != null) {
            cellRenderer = myCellRenderer
        }
        name = viewName
    }

    // http://stackoverflow.com/questions/2528344/jlist-deselect-when-clicking-an-already-selected-item
    fun enableToggleSelectionMode() {
        selectionModel = object : DefaultListSelectionModel() {
            private var gestureStarted = false
            override fun setSelectionInterval(index0: Int, index1: Int) {
                if (!gestureStarted) {
                    if (isSelectedIndex(index0)) {
                        super.removeSelectionInterval(index0, index1)
                    } else {
                        super.addSelectionInterval(index0, index1)
                    }
                }
                gestureStarted = true
            }

            override fun setValueIsAdjusting(isAdjusting: Boolean) {
                if (!isAdjusting) {
                    gestureStarted = false
                }
            }
        }
    }

    fun addElementAtTop(element: T) {
        myModel.insertElementAt(element, 0)
    }

    fun addElementAtBottom(element: T) {
        myModel.addElement(element)
    }

    fun addProperIndex(element: T) {
        val index = myModel.calculateInsertIndex(element)
        myModel.add(index, element)
    }

    fun removeElementByComparator(idComparator: (T) -> Boolean) {
        myModel.removeElementByComparator(idComparator)
    }

    fun setElementByComparator(element: T, idComparator: (T) -> Boolean) {
        myModel.setElementByComparator(element, idComparator)
    }

    fun resetData(elements: List<T>) {
        myModel.resetData(elements)
    }

    fun clear() {
        myModel.clear()
    }

    protected fun initDoubleClicked(eventFunction: (T) -> UserEvent) {
        registerDoubleClicked { row, element -> bus.post(eventFunction(element)) }
    }

    protected fun initSinglePopup(label: String, eventFunction: (T) -> UserEvent) {
        enablePopup(bus, Pair<String, (T) -> UserEvent>(label, { eventFunction(it) }))
    }

    fun addSelectedValues(entries: List<T>) {
        entries.forEach {
            val index = myModel.indexOf(it)
            if (index == -1) {
                throw GadsuException("Not found element in list model: '$it'")
            }
            addSelectionInterval(index, index)
        }
    }
}


class MyListModel<E> : DefaultListModel<E>(), IndexableModel<E> {
    override val indexableSize: Int get() = size
    override fun indexableElementAt(index: Int): E = getElementAt(index)

    fun setElementByComparator(newValue: E, comparator: (current: E) -> Boolean) {
        val index = findIndexByComparator(comparator)
        setElementAt(newValue, index)
    }

    fun removeElementByComparator(comparator: (current: E) -> Boolean) {
        val index = findIndexByComparator(comparator)
        removeElementAt(index)
    }

    fun resetData(newElements: List<E>) {
        removeAllElements()
        newElements.forEach { addElement(it) }
    }

}


fun <E : HasId> MyListModel<E>.containsById(element: E): Boolean {
    val it = this.elements().iterator()
    while (it.hasNext()) {
        if (it.next().id == element.id) {
            return true
        }
    }
    return false
}

