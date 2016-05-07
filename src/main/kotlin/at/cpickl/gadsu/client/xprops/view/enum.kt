package at.cpickl.gadsu.client.xprops.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.view.ElField
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.EventBus
import java.awt.GridBagConstraints
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel

interface ElFieldForProps<V> : ElField<V> {
    fun enableFor(modifications: ModificationChecker)
}

class CPropEnumView(
        override val icon: ImageIcon?,
        private val xprop: XPropEnum,
        bus: EventBus
): CPropView, ElFieldForProps<Client> {

    private val list: MyList<XPropEnumOpt>

    override val formLabel = xprop.label
    override val fillType = GridBagFill.Both

    init {
        val model = MyListModel<XPropEnumOpt>()
        model.resetData(xprop.options)
        list = MyList("FIXME_VIEWNAME", model, bus, object: MyListCellRenderer<XPropEnumOpt>() {
            override fun newCell(value: XPropEnumOpt) = XPropEnumCell(value)
        })
        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        list.visibleRowCount = 1
    }

    override fun updateValue(value: Client) {
        list.clearSelection()
        val cprop = value.cprops.findOrNull(xprop) ?: return
        list.addSelectedValues((cprop as CPropEnum).clientValue)
    }

    override fun toCProp() = CPropEnum(xprop, list.selectedValuesList)

    override fun toComponent() = list.scrolled(hPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)

    override fun isModified(value: Client): Boolean {
        val selected = list.selectedValuesList
        val cprop = value.cprops.findOrNull(xprop) ?: return selected.isNotEmpty()

        if (selected.isEmpty()) {
            return false
        }

        val enumProp = cprop as CPropEnum
        return !enumProp.clientValue.containsAll(selected) ||
               !selected.containsAll(enumProp.clientValue)
    }

    override fun enableFor(modifications: ModificationChecker) {
        modifications.enableChangeListener(list)
    }
}

class XPropEnumCell(val xprop: XPropEnumOpt) : DefaultCellView<XPropEnumOpt>(xprop) {
    private val txtLabel = JLabel(xprop.label)
    override val applicableForegrounds: Array<JComponent> = arrayOf(txtLabel)

    init {
        c.anchor = GridBagConstraints.WEST
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(txtLabel)
    }

}
