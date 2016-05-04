package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.opaque
import at.cpickl.gadsu.view.swing.transparent
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
        opaque()
        border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
    }

    override final fun changeForeground(foreground: Color) {
        applicableForegrounds.forEach { it.foreground = foreground }
    }

    override final fun changeBackground(background: Color) {
        this.background = background
    }

    override final fun changeToTransparent() {
        asComponent().transparent()
    }

    override final fun asComponent() = this
}


abstract class MyListCellRenderer<T> : ListCellRenderer<T> {
    companion object {
        val ALTERNATE_BG_COLOR = Colors.LIGHT_GRAY
    }

    protected abstract fun newCell(value: T): CellView

    override fun getListCellRendererComponent(list: JList<out T>, value: T, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        // MINOR UI improve performance (memory) by reusing existing cells and simply change its state
        val cell = newCell(value)

        if (isSelected) {
            cell.changeForeground(UIManager.getColor("List.selectionForeground"))
            cell.changeBackground(UIManager.getColor("List.selectionBackground"))
        } else if (index % 2 == 1) {
            cell.changeBackground(ALTERNATE_BG_COLOR)
        } else {
            cell.changeToTransparent()
        }

        return cell.asComponent()
    }
}
