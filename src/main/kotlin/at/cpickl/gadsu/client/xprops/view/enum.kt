package at.cpickl.gadsu.client.xprops.view

import at.cpickl.gadsu.client.xprops.model.CProp
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import com.google.common.eventbus.EventBus
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.ListSelectionModel


class CPropEnumView(private val xprop: XPropEnum, bus: EventBus): CPropView {
    private val list: MyList<XPropEnumOpt>

    init {
        val model = MyListModel<XPropEnumOpt>()
        model.resetData(xprop.options)
        list = MyList<XPropEnumOpt>("FIXME_VIEWNAME", model, bus, object: MyListCellRenderer<XPropEnumOpt>() {
            override fun newCell(value: XPropEnumOpt) = XPropEnumCell(value)
        })
        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        list.visibleRowCount = 3
    }

    override fun updateField(cprop: CProp?) {
        list.clearSelection()
        if (cprop == null) {
            return
        }
        list.addSelectedValues((cprop as CPropEnum).clientValue)
    }

    override fun toCProp() = CPropEnum(xprop, list.selectedValuesList)

    override fun toComponent() = list

}

class XPropEnumCell(val xprop: XPropEnumOpt) : DefaultCellView<XPropEnumOpt>(xprop) {
    private val txtLabel = JLabel(xprop.label)
    override val applicableForegrounds: Array<JComponent> = arrayOf(txtLabel)

    init {
//        c.anchor = GridBagConstraints.NORTHWEST
//        c.weightx = 1.0
//        c.fill = GridBagConstraints.HORIZONTAL
//        add(txtTitle)
//
//        c.gridy++
        add(txtLabel)
    }
}
