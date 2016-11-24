package at.cpickl.gadsu.view

import javax.swing.JComponent
import javax.swing.UIManager

object ViewConstants {

    object Table {
        val backgroundColor = UIManager.getColor("Table.background")
        val backgroundSelectionColor = UIManager.getColor("Table.selectionBackground")

        fun changeBackground(component: JComponent, isSelected: Boolean) {
            // TODO maybe do this instead: comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
            component.background = if (isSelected) backgroundSelectionColor else backgroundColor

        }
    }
}
