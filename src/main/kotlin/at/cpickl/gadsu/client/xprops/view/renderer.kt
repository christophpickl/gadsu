package at.cpickl.gadsu.client.xprops.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProp
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.XProp
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropTypeCallback
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.components.panels.FormPanel
import com.google.common.eventbus.EventBus
import java.awt.Component
import java.awt.GridBagConstraints
import java.util.HashMap


class CPropsRenderer(
        private val fields: Fields<Client>,
        private val bus: EventBus
) {

    private val map: HashMap<XProp, CPropView> = HashMap()

    fun addXProp(xprop: XProp, form: FormPanel) {
        val ui = buildCPropUI(xprop)
        map.put(xprop, ui)
        form.addFormInput(xprop.label, ui.toComponent(), ui.fillType)
    }

    fun updateFields(client: Client) {
        map.forEach { xprop, ui ->
            ui.updateValue(client)
        }
    }

    fun readCProps(): CProps {
        val cprops = HashMap<XProp, CProp>()
        map.forEach { xprop, ui ->
            val cprop = ui.toCProp()
            if (cprop.isClientValueSet) {
                cprops.put(xprop, cprop)
            }
        }
        return CProps(cprops)
    }

    private fun buildCPropUI(xprop: XProp): CPropView {
        return xprop.onType(object: XPropTypeCallback<CPropView> {
            override fun onEnum(xprop: XPropEnum): CPropView {

                val view = CPropEnumView(xprop, bus)
                // TODO view name
                fields.register(view)
                return view
            }
        })
    }
}

interface CPropView {
    fun updateValue(value: Client)
    fun toComponent(): Component
    fun toCProp(): CProp

    val fillType: GridBagFill
}

enum class GridBagFill(val swingId: Int) {
    None(GridBagConstraints.NONE),
    Horizontal(GridBagConstraints.HORIZONTAL),
    Vertical(GridBagConstraints.VERTICAL),
    Both(GridBagConstraints.BOTH)
}
