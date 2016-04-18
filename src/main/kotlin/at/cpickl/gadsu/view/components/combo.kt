package at.cpickl.gadsu.view.components

import java.awt.Component
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer


interface Labeled {
    val label: String
}


open class MyComboBox<T : Labeled>(array: Array<T>, initValue: T) : JComboBox<T>(array) {

    init {
        assert(array.contains(initValue))
        selectedItem = initValue
        setRenderer(LabeledCellRenderer(getRenderer()))
    }

    var selectedItemTyped: T
        get() = selectedItem as T
        set(value) { selectedItem = value }

    //    fun setLabeledCellRenderer(): MyComboBox<T> {
    //        setRenderer(LabeledCellRenderer(getRenderer()))
    //        return this
    //    }

}
/**
 * Anything that inherits Labeled, can be rendered with this.
 */
class LabeledCellRenderer<L : Labeled>(private val original: ListCellRenderer<in L>) : ListCellRenderer<L> {

    override fun getListCellRendererComponent(
            list: JList<out L>, value: L, index: Int,
            isSelected: Boolean, cellHasFocus: Boolean): Component? {
        val rendering = original.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        (rendering as JLabel).text = value.label
        return rendering
    }
}
