package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.GadsuException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Point
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.ListModel


val <T> JList<T>.log: Logger
    get() = LoggerFactory.getLogger(JList::class.java)

fun <T> JList<T>.myLocationToIndex(point: Point): Int {
    // MINOR there is a bug: https://github.com/UISpec4J/UISpec4J/issues/30
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


fun <E> DefaultListModel<E>.setElementByComparator(newValue: E, comparator: (current: E) -> Boolean) {
    val index = findIndexByComparator(comparator)
    setElementAt(newValue, index)
}

fun <E> DefaultListModel<E>.removeElementByComparator(comparator: (current: E) -> Boolean) {
    val index = findIndexByComparator(comparator)
    removeElementAt(index)
}

fun <E> ListModel<E>.findIndexByComparator(comparator: (current: E) -> Boolean): Int {
    for (i in 0.rangeTo(size - 1)) {
        val c = getElementAt(i)
        if (comparator(c)) {
            return i
        }
    }
    throw GadsuException("Could not determine index of list entry!")
}
