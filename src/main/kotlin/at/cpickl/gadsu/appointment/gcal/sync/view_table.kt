package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.view.ViewConstants
import at.cpickl.gadsu.view.components.MyTable
import at.cpickl.gadsu.view.components.MyTableModel
import at.cpickl.gadsu.view.swing.opaque
import java.awt.Component
import java.util.EventObject
import javax.swing.AbstractCellEditor
import javax.swing.DefaultCellEditor
import javax.swing.DefaultComboBoxModel
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.event.CellEditorListener
import javax.swing.event.ChangeEvent
import javax.swing.event.EventListenerList
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class SyncTable(model: MyTableModel<ImportAppointment>) :
        MyTable<ImportAppointment>(model, "SyncTable") {

    companion object {
        private val COL_CHECKBOX = 0
        private val COL_CLIENT = 2
    }

    init {
        val editor = MyCheckboxTableCellEditor()
        editor.addCellEditorListener(object : CellEditorListener {
            override fun editingStopped(e: ChangeEvent) {
                println("editingStopped(): selectedRow = $selectedRow, editor.currentState = ${editor.currentState}")
                val importApp = model.entityAt(selectedRow)
                importApp.enabled = editor.currentState
            }
            override fun editingCanceled(e: ChangeEvent?) {
            }
        })
        columnModel.getColumn(COL_CHECKBOX).cellEditor = editor
        columnModel.getColumn(COL_CHECKBOX).cellRenderer = CheckboxTableCellRenderer
//        columnModel.getColumn(COL_CHECKBOX).cellEditor = DefaultCellEditor(JCheckBox())

        val comboModel = DefaultComboBoxModel<String>(arrayOf("xclient1", "xclient2"))
        val combo = JComboBox(comboModel)
        columnModel.getColumn(COL_CLIENT).cellEditor = DefaultCellEditor(combo)
    }


    override fun isCellEditable(row: Int, column: Int): Boolean {
        if (column == COL_CHECKBOX) {
            return true
        }
        if (column == COL_CLIENT) {
            return true
        }
        return super.isCellEditable(row, column)
    }
}


private object CheckboxTableCellRenderer : TableCellRenderer {
    private val box = JCheckBox()
    override fun getTableCellRendererComponent(table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        println("renderer: row=$row, value=$value")
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
        checkbox.addActionListener { currentState = checkbox.isSelected }
    }

    override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
        println("editor: row=$row, value=$value")
        val isValueTrue = value as Boolean
        currentState = isValueTrue
        checkbox.isSelected = isValueTrue
        ViewConstants.Table.changeBackground(checkbox, isSelected)
        return checkbox
    }

    override fun getCellEditorValue() = currentState
}



private object CheckboxTableCellEditor : JCheckBox(), TableCellEditor {

    private val myChangeEvent = ChangeEvent(this)

    init {
        opaque()
        addActionListener { stopCellEditing() }
    }

    private val listeners = EventListenerList()

    override fun getCellEditorValue() = isSelected
    override fun shouldSelectCell(anEvent: EventObject?) = true
    override fun isCellEditable(anEvent: EventObject?) = true

    override fun addCellEditorListener(l: CellEditorListener?) {
        listeners.add(CellEditorListener::class.java, l)
    }

    override fun removeCellEditorListener(l: CellEditorListener?) {
        listeners.remove(CellEditorListener::class.java, l)
    }

    override fun stopCellEditing(): Boolean {
        forEachListener { it.editingStopped(myChangeEvent) }
        return true
    }

    override fun cancelCellEditing() {
        forEachListener { it.editingCanceled(myChangeEvent) }
    }

    override fun getTableCellEditorComponent(table: JTable, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
        val isEventEnabled = value as Boolean
        this.isSelected = isEventEnabled
        ViewConstants.Table.changeBackground(this, isSelected)
        return this
    }

    private fun forEachListener(action: (CellEditorListener) -> Unit) {
        val listeners = listeners.listenerList
        listeners.indices
                .filter { listeners[it] === CellEditorListener::class.java }
                .map { listeners[it + 1] as CellEditorListener }
                .forEach { action(it) }
    }
}
