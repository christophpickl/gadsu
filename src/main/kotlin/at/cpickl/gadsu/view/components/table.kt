package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.view.ViewConstants
import at.cpickl.gadsu.view.swing.opaque
import java.awt.Component
import javax.swing.AbstractCellEditor
import javax.swing.JCheckBox
import javax.swing.JTable
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer


class MyCheckboxTableCellRenderer : TableCellRenderer {
    private val box = JCheckBox()
    override fun getTableCellRendererComponent(table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
//        println("renderer: row=$row, value=$value")
        return box.apply {
            ViewConstants.Table.changeBackground(this, isSelected)
            this.isSelected = value as Boolean
        }
    }
}


class MyCheckboxTableCellEditor : AbstractCellEditor(), TableCellEditor {

    private val checkbox = JCheckBox()
    var currentState = false

    init {
        checkbox.opaque()
        checkbox.addActionListener {
            currentState = checkbox.isSelected
        }
    }

    override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
//        println("editor: row=$row, value=$value, isSelected=$isSelected")
        val isValueTrue = value as Boolean
        currentState = isValueTrue
        checkbox.isSelected = isValueTrue
        ViewConstants.Table.changeBackground(checkbox, isSelected)
        return checkbox
    }

    override fun getCellEditorValue() = currentState
}
