package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.formatDateWithDayNoYear
import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import at.cpickl.gadsu.view.ViewConstants
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.swing.opaque
import org.joda.time.DateTime
import java.awt.Component
import javax.swing.AbstractCellEditor
import javax.swing.JCheckBox
import javax.swing.JTable
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer


class DateRangeTableCellRenderer : TableCellRenderer {

    private val text = HtmlEditorPane().apply {
        opaque()
    }

    override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        val (start, end) = @Suppress("UNCHECKED_CAST") (value as Pair<DateTime, DateTime>)
        text.text = "${start.formatDateWithDayNoYear()}<br/>${start.formatTimeWithoutSeconds()} - ${end.formatTimeWithoutSeconds()}"
        ViewConstants.Table.changeBackground(text, isSelected)
        ViewConstants.Table.changeForeground(text, isSelected)
        return text
    }

}

class MyCheckboxTableCellRenderer : TableCellRenderer {

    private val box = JCheckBox()

    override fun getTableCellRendererComponent(table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        return box.apply {
            ViewConstants.Table.changeBackground(this, isSelected)
            this.isSelected = value as Boolean
        }
    }
}

data class MyEnableValue(val enabled: Boolean, val selected: Boolean)
class MyEnableCheckboxTableCellRenderer : TableCellRenderer {

    private val box = JCheckBox()

    override fun getTableCellRendererComponent(table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        return box.apply {
            ViewConstants.Table.changeBackground(this, isSelected)
            val enableValue = value as MyEnableValue
            this.isEnabled = enableValue.enabled
            this.isSelected = enableValue.selected
        }
    }
}


class MyCheckboxTableCellEditor() : AbstractCellEditor(), TableCellEditor {

    private val checkbox = JCheckBox()
    var currentState = false

    init {
        checkbox.opaque()
        checkbox.addActionListener {
            currentState = checkbox.isSelected
        }
    }

    override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
        ViewConstants.Table.changeBackground(checkbox, isSelected)

        val isValueTrue = value as Boolean
        currentState = isValueTrue
        checkbox.isSelected = isValueTrue
        return checkbox
    }

    override fun getCellEditorValue() = currentState
}

class MyEnableCheckboxTableCellEditor() : AbstractCellEditor(), TableCellEditor {

    private val checkbox = JCheckBox()
    var currentState = false

    init {
        checkbox.opaque()
        checkbox.addActionListener {
            currentState = checkbox.isSelected
        }
    }

    override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
        val enableValue = value as MyEnableValue
        currentState = enableValue.selected
        checkbox.isSelected = enableValue.selected
        checkbox.isEnabled = enableValue.enabled
        ViewConstants.Table.changeBackground(checkbox, isSelected)
        return checkbox
    }

    override fun getCellEditorValue() = currentState
}
