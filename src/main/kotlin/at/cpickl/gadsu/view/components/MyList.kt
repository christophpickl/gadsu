package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.view.components.panels.SingleButtonPanel
import at.cpickl.gadsu.view.logic.*
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import javax.swing.DefaultListModel
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

    final fun addProperIndex(treatment: T) {
        val index = myModel.calculateInsertIndex(treatment)
        myModel.add(index, treatment)
    }

    final fun removeElementByComparator(idComparator: (T) -> Boolean) {
        myModel.removeElementByComparator(idComparator)
    }

    final fun setElementByComparator(treatment: T, idComparator: (T) -> Boolean) {
        myModel.setElementByComparator(treatment, idComparator)
    }

    final fun resetData(treatments: List<T>) {
        myModel.resetData(treatments)
    }

    final fun clear() {
        myModel.clear()
    }

    protected fun initDoubleClicked(eventFunction: (T) -> UserEvent) {
        registerDoubleClicked { row, treatment -> bus.post(eventFunction(treatment)) }
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
    override fun indexableElementAt(index: Int) = getElementAt(index)

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


