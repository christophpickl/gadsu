package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.ViewConstants
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.opaque
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

//class ClientRenderer : DefaultListCellRenderer() {
//
//    override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
//        val comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
//        (comp as JLabel).text = (value as Client?)?.fullName ?: "null"
//        return comp
//    }
//
//}

class ClientRenderer : ListCellRenderer<Client> {
    override fun getListCellRendererComponent(list: JList<out Client>?, value: Client?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        val panel = GridPanel()
        panel.opaque()
        panel.background = if (cellHasFocus) ViewConstants.Table.backgroundSelectionColor else ViewConstants.Table.backgroundColor

        panel.add(JLabel(value?.picture?.toViewLilRepresentation()))
        panel.c.gridx++
        panel.add(JLabel(value?.fullName ?: "null"))
        return panel
    }

}
