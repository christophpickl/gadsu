package at.cpickl.gadsu.client.xprops.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProp
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.view.ElField
import at.cpickl.gadsu.view.components.CellView
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MultiProperties
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.logic.ModificationChecker
import com.google.common.eventbus.EventBus
import java.awt.GridBagConstraints
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel


fun CProp?.formatData(): String {
    if (this == null) {
        return ""
    }
    val selectedEnumOpts = this.formatClientValues()
    return selectedEnumOpts + (
            if (this.note.isEmpty()) "" else
                (if (selectedEnumOpts.isNotEmpty()) "\n\n" else "") + "[NOTIZ]\n" + this.note
            )
}

fun CProp.formatClientValues() = this.clientValue.map { "* " + it.label }.joinToString("\n")

interface ElFieldForProps<in V> : ElField<V> {
    fun enableFor(modifications: ModificationChecker)
}

object XPropCellRenderer : MyListCellRenderer<XPropEnumOpt>(shouldHoverChangeSelectedBg = true)  {
    override fun newCell(value: XPropEnumOpt): CellView = XPropEnumCell(value)
}

class CPropEnumView(
        override val icon: ImageIcon?,
        private val xprop: XPropEnum,
        bus: EventBus
) : CPropView, ElFieldForProps<Client> {

    override val formLabel = xprop.label
    override val fillType = GridBagFill.Both

    private val multiProperties: MultiProperties = MultiProperties(xprop, bus, XPropCellRenderer, xprop.key)

    override fun updateValue(value: Client) {
        multiProperties.updateValue(value)
    }

    override fun toCProp(): CPropEnum = CPropEnum(xprop, multiProperties.selectedValues, multiProperties.enteredNote)

    override fun toComponent() = multiProperties.toComponent()

    override fun isModified(value: Client): Boolean {
        val enteredNote = multiProperties.enteredNote
        val originalNote = value.cprops.findOrNull(xprop)?.note ?: ""
        val noteChanged = enteredNote != originalNote
        val selectionModified = isSelectionModified(value)
        return selectionModified || noteChanged // || editorView.note.text != value.cprops.findOrNull(xprop)?.note ?: ""
    }

    override fun enableFor(modifications: ModificationChecker) {
        multiProperties.enableFor(modifications)
    }

    private fun isSelectionModified(value: Client): Boolean {
        val selected = multiProperties.selectedValues
        val cprop = value.cprops.findOrNull(xprop) ?: return selected.isNotEmpty()

        if (selected.isEmpty() && cprop.isClientValueEmpty) {
            return false
        }

        val enumProp = cprop as CPropEnum
        return !enumProp.clientValue.containsAll(selected) ||
                !selected.containsAll(enumProp.clientValue)
    }
}

private class XPropEnumCell(val xprop: XPropEnumOpt) : DefaultCellView<XPropEnumOpt>(xprop) {

    private val txtLabel = JLabel(xprop.label)
    override val applicableForegrounds: Array<JComponent> = arrayOf(txtLabel)

    init {
        c.anchor = GridBagConstraints.WEST
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(txtLabel)
    }

}
