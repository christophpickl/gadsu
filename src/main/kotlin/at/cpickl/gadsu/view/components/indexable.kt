package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.GadsuException


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
