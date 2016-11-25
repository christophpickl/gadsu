package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.view.ClientRenderer
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.ViewConstants
import at.cpickl.gadsu.view.components.MyCheckboxTableCellEditor
import at.cpickl.gadsu.view.components.MyCheckboxTableCellRenderer
import at.cpickl.gadsu.view.components.MyDateTimeTableCellRenderer
import at.cpickl.gadsu.view.components.MyTable
import at.cpickl.gadsu.view.components.MyTableModel
import at.cpickl.gadsu.view.registerOnStopped
import java.awt.Component
import javax.swing.AbstractCellEditor
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.TableCellEditor

class SyncTable(
        private val model: MyTableModel<ImportAppointment>
) :
        MyTable<ImportAppointment>(model, "SyncTable"), ImportClientsProvider {

    companion object {
        private val COL_CHECKBOX = 0
        private val COL_CLIENT = 2
        private val COL_DATE = 3
    }

    private val logg = LOG(javaClass)

    init {
        val enabledEditor = MyCheckboxTableCellEditor().apply {
            registerOnStopped {
                val importApp = model.entityAt(selectedRow)
                logg.debug("on stopped for {}, to state: {}", importApp, currentState)
                importApp.enabled = currentState
            }
        }

        // textTable.setDefaultRenderer(String.class, new RowHeightCellRenderer());
        columnModel.getColumn(COL_CHECKBOX).cellEditor = enabledEditor
        columnModel.getColumn(COL_CHECKBOX).cellRenderer = MyCheckboxTableCellRenderer()
        columnModel.getColumn(COL_DATE).cellRenderer = MyDateTimeTableCellRenderer()

        rowHeight = 40

        val clientEditor = ImportAppointmentClientEditor(this).apply {
            registerOnStopped {
                val importApp = model.entityAt(selectedRow)
                importApp.selectedClient = currentClient
            }
        }
        columnModel.getColumn(COL_CLIENT).cellEditor = clientEditor
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return when (column) {
            COL_CHECKBOX, COL_CLIENT -> true
            else -> super.isCellEditable(row, column)
        }
    }

    override fun suggestClients(row: Int) = model.entityAt(row).allClients

    override fun clientByRow(row: Int) = model.entityAt(row).selectedClient

}

private interface ImportClientsProvider {
    fun suggestClients(row: Int): List<Client>
    fun clientByRow(row: Int): Client
}

private class ImportAppointmentClientEditor(
        private val clientsProvider: ImportClientsProvider
) : AbstractCellEditor(), TableCellEditor {

    private val combo = JComboBox<Client>().apply {
        renderer = ClientRenderer()
        putClientProperty("JComboBox.isTableCellEditor", true)
    }

    val currentClient: Client get() = cellEditorValue as Client

    override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
        ViewConstants.Table.changeBackground(combo, isSelected)
        val selectedClient = clientsProvider.clientByRow(row)
        combo.model = DefaultComboBoxModel<Client>(clientsProvider.suggestClients(row).toTypedArray())
        combo.selectedItem = selectedClient
        return combo
    }

    override fun getCellEditorValue(): Any {
        return combo.selectedItem
    }

}

