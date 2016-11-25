package at.cpickl.gadsu.view

import java.awt.Color
import javax.swing.JComponent
import javax.swing.UIManager

object ViewConstants {

    object Table {
        private val backgroundColor = UIManager.getColor("Table.background")
        private val backgroundSelectionColor = UIManager.getColor("Table.selectionBackground")

        fun changeBackground(component: JComponent, useSelectionColor: Boolean) {
            component.background = if (useSelectionColor) backgroundSelectionColor else backgroundColor
        }
        fun changeForeground(component: JComponent, useSelectionColor: Boolean) {
            component.foreground = if (useSelectionColor) Color.WHITE else Color.BLACK
        }
    }
}
