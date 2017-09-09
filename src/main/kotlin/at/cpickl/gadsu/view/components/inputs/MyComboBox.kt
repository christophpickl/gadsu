package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.global.Labeled
import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.swing.opaque
import at.cpickl.gadsu.view.swing.transparent
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Component
import java.util.Vector
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.UIManager



open class MyComboBox<T : Labeled>(data: List<T>, initValue: T) : JComboBox<T>(Vector(data)) {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        assert(data.contains(initValue))
        selectedItem = initValue
        setRenderer(LabeledCellRenderer(getRenderer()))
//        prototypeDisplayValue = initValue // fail: trying to get proper width
    }

    @Suppress("UNCHECKED_CAST")
    var selectedItemTyped: T
        get() = selectedItem as T
        set(value) {
            selectedItem = value
            if (!selectedItem.equals(value)) {
                log.warn("Seems as you requested to select an item ({}) which was not contained in this combo box model: {}",
                        value, model)
            }
        }

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
        val label = rendering as JLabel
        label.text = value.label
//        println("value=$value, index=$index, isSelected=$isSelected, focus=$cellHasFocus")

        // alternate row background
        if (index == -1) {
            label.transparent()
        } else if (isSelected) {
            label.opaque()
            label.background = UIManager.getColor("List.selectionBackground")
        } else {
            label.opaque()
            if (index % 2 == 1) {
                label.background = Colors.BG_ALTERNATE
            } else {
                label.background = Color.WHITE
            }
        }
        return rendering
    }
}
