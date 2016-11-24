package at.cpickl.gadsu.view

import javax.swing.JComponent
import javax.swing.UIManager

object ViewConstants {

    object Table {
        val backgroundColor = UIManager.getColor("Table.background")
        val backgroundSelectionColor = UIManager.getColor("Table.selectionBackground")

        fun changeBackground(component: JComponent, isSelected: Boolean) {
            component.background = if (isSelected) backgroundSelectionColor else backgroundColor

        }
    }
}
