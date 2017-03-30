package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.view.ClientRenderer
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.ViewConstants
import at.cpickl.gadsu.view.components.DateRangeTableCellRenderer
import at.cpickl.gadsu.view.components.MyCheckboxTableCellEditor
import at.cpickl.gadsu.view.components.MyCheckboxTableCellRenderer
import at.cpickl.gadsu.view.components.MyEnableCheckboxTableCellEditor
import at.cpickl.gadsu.view.components.MyEnableCheckboxTableCellRenderer
import at.cpickl.gadsu.view.components.MyTable
import at.cpickl.gadsu.view.components.MyTableModel
import at.cpickl.gadsu.view.registerOnStopped
import java.awt.Component
import javax.swing.AbstractCellEditor
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.TableCellEditor

// MINOR bg color of column confirmation is white
class SyncTable(
        private val model: MyTableModel<ImportAppointment>
) :
        MyTable<ImportAppointment>(model, "SyncTable", columnResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS), ImportClientsProvider {

    companion object {
        private val COL_CHECKBOX = 0
        private val COL_CLIENT = 2
        private val COL_DATE = 3
        private val COL_CONFIRMATION = 4
    }

    private val logg = LOG(javaClass)

    init {
        val enabledEditor = MyCheckboxTableCellEditor().apply {
            registerOnStopped {
                model.entityAt(selectedRow).enabled = currentState
            }
        }
        val confirmationEditor = MyEnableCheckboxTableCellEditor().apply {
            registerOnStopped {
                // MINOR UI glitch: when deselecting, selectedRow could be -1
                model.entityAt(selectedRow).sendConfirmation = currentState
            }
        }

        // textTable.setDefaultRenderer(String.class, new RowHeightCellRenderer());
        columnModel.getColumn(COL_CHECKBOX).cellEditor = enabledEditor
        columnModel.getColumn(COL_CHECKBOX).cellRenderer = MyCheckboxTableCellRenderer()
        columnModel.getColumn(COL_DATE).cellRenderer = DateRangeTableCellRenderer()
        columnModel.getColumn(COL_CONFIRMATION).cellEditor = confirmationEditor
        columnModel.getColumn(COL_CONFIRMATION).cellRenderer = MyEnableCheckboxTableCellRenderer()

        rowHeight = 40

        val clientEditor = ImportAppointmentClientEditor(this).apply {
            registerOnStopped {
                logg.trace("Selected client: {}", currentClient)
                val importApp = model.entityAt(selectedRow)
                importApp.selectedClient = currentClient
                importApp.sendConfirmation = currentClient.hasMail
            }
        }
        columnModel.getColumn(COL_CLIENT).cellEditor = clientEditor
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return when (column) {
            COL_CHECKBOX, COL_CLIENT, COL_CONFIRMATION -> true
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

