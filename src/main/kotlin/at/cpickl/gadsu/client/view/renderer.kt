package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.ViewConstants
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.opaque
import java.awt.Component
import java.awt.GridBagConstraints
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
        ViewConstants.Table.changeBackground(panel, cellHasFocus)


        with(panel.c) {
            fill = GridBagConstraints.VERTICAL
            anchor = GridBagConstraints.WEST
            weightx = 0.0
            panel.add(JLabel(value?.picture?.toViewLilRepresentation()))

            gridx++
            insets = Pad.LEFT
            fill = GridBagConstraints.BOTH
            weightx = 1.0
            panel.add(JLabel(value?.preferredName ?: "null"))

        }
        return panel
    }

}
