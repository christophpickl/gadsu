package at.cpickl.gadsu.view.components

import javax.swing.AbstractListModel

class SimpleListModel<T>(private val values: List<T>) : AbstractListModel<T>() {
    override fun getElementAt(index: Int): T = values[index]
    override fun getSize(): Int = values.size
}
