package at.cpickl.gadsu.view.components

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Point
import javax.swing.DefaultListModel
import javax.swing.JList


val <T> JList<T>.log: Logger
    get() = LoggerFactory.getLogger(JList::class.java)


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



// --------------------------------------------------------------------------- extension methods


fun <T> JList<T>.myLocationToIndex(point: Point): Int {
    // uispec4j bug: https://github.com/UISpec4J/UISpec4J/issues/30
    return locationToIndex(point) // returns _closest_ index! :(
    //    for (i in 0.rangeTo(model.size - 1)) {
    //        val bounds = getCellBounds(i, i)
    //        if (point.y <= bounds.y + bounds.height) {
    //            return i
    //        }
    //    }
    //    log.debug("No cell found for given point: {}", point)
    //    return -1
}

fun <T> JList<T>.elementAtPoint(point: Point): Pair<Int, T>? {
    log.trace("elementAtIndex(point={})", point)
    val index = myLocationToIndex(point)
    if (index === -1) {
        return null
    }
    return Pair(index, model.getElementAt(index))
}

