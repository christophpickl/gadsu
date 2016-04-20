package at.cpickl.gadsu.view.components

import java.awt.Color
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.UIManager


interface CellView {
    fun changeForeground(foreground: Color)
    fun changeBackground(background: Color)
    fun changeToTransparent()
    fun asComponent(): JComponent
}

abstract class DefaultCellView<T>(protected val value: T): GridPanel(), CellView {

    protected abstract val applicableForegrounds: Array<JComponent>

    init {
        border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
    }

    override final fun changeForeground(foreground: Color) {
        applicableForegrounds.forEach { it.foreground = foreground }
    }

    override final fun changeBackground(background: Color) {
        this.background = background
    }

    override final fun changeToTransparent() {
        asComponent().isOpaque = false
    }

    override final fun asComponent() = this
}


abstract class MyListCellRenderer<T> : ListCellRenderer<T> {

    protected abstract fun newCell(value: T): CellView

    override fun getListCellRendererComponent(list: JList<out T>, value: T, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        val cell = newCell(value)

        if (isSelected) {
            cell.changeForeground(UIManager.getColor("List.selectionForeground"))
            cell.changeBackground(UIManager.getColor("List.selectionBackground"))
        } else {
            cell.changeToTransparent()
        }

        return cell.asComponent()
    }
}
