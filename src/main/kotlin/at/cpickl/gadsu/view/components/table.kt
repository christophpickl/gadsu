package at.cpickl.gadsu.view.components

import org.slf4j.LoggerFactory
import java.util.ArrayList
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel


class TableColumn<E>(val name: String, val width: Int, val transform: (value: E) -> Any)

class MyTable<E>(private val _model: MyTableModel<E>, viewName: String) : JTable(_model) {

    val log = LoggerFactory.getLogger(javaClass)

    init {
        name = viewName
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN)
        for (i in 0.rangeTo(_model.columns.size - 1)) {
            if (i === _model.columns.size - 1) {
                columnModel.getColumn(i).maxWidth = Int.MAX_VALUE
            } else {
                columnModel.getColumn(i).maxWidth = _model.columns[i].width
            }
        }

        // http://docs.oracle.com/javase/tutorial/uiswing/components/table.html#selection
        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        columnSelectionAllowed = false
    }

    fun getEntityAt(row: Int): E = _model.entityAt(row)

}
class MyTableModel<E>(val columns: List<TableColumn<E>>) : AbstractTableModel(), IndexableModel<E> {

    private val data: MutableList<E> = ArrayList()

    override fun getRowCount() = data.size
    override fun getColumnCount() = columns.size
    override fun getColumnName(columnIndex: Int) = columns[columnIndex].name
    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = false
    override fun getValueAt(rowIndex: Int, columnIndex: Int) = columns[columnIndex].transform(data[rowIndex])

    override val indexableSize: Int get() = size
    override fun indexableElementAt(index: Int) = data[index]

    val size: Int
        get() = data.size

    fun resetData(newData: List<E>) {
        data.clear()
        data.addAll(newData)
        fireTableDataChanged()
    }

    fun addElementAt(index: Int, element: E) {
        data.add(index, element)
    }

    fun setElementByComparator(newValue: E, comparator: (current: E) -> Boolean) {
        val index = findIndexByComparator(comparator)
        data.set(index, newValue)
        fireTableDataChanged()
    }

    fun removeElementByComparator(comparator: (current: E) -> Boolean) {
        val index = findIndexByComparator(comparator)
        data.removeAt(index)
        fireTableDataChanged()
    }

    fun entityAt(index: Int): E = data[index]

}
