package at.cpickl.gadsu.client.xprops.view

import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.*
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.components.EditorRendererSwitchable
import at.cpickl.gadsu.view.components.panels.FormPanel
import com.google.common.eventbus.EventBus
import java.awt.Component
import java.awt.GridBagConstraints
import java.util.*
import javax.swing.ImageIcon


class CPropsRenderer(
        private val fields: Fields<Client>,
        private val bus: EventBus
) {

    companion object {
        private val log = LOG(javaClass)
    }

    private val xpropToCPropView: HashMap<XProp, CPropView> = HashMap()

    val allSwitchables: List<EditorRendererSwitchable> get() = xpropToCPropView.values.toList()

    fun addXProp(xprop: XProp, form: FormPanel) {
        log.trace("addXProp(xprop={}, form", xprop)

        val ui = buildCPropUI(xprop)
        xpropToCPropView.put(xprop, ui)
        form.addFormInput(xprop.label, ui.toComponent(), ui.fillType, ui.icon)
    }

    fun updateFields(client: Client) {
        xpropToCPropView.forEach { _, ui ->
            ui.updateValue(client)
        }
    }

    fun readCProps(): CProps {
        val cprops = HashMap<XProp, CProp>()
        xpropToCPropView.forEach { xprop, ui ->
            val cprop = ui.toCProp()
            if (cprop.isValueOrNoteSet) {
                cprops.put(xprop, cprop)
            }
        }
        return CProps(cprops)
    }

    private fun buildCPropUI(xprop: XProp): CPropView {
        return xprop.onType(object: XPropTypeCallback<CPropView> {
            override fun onEnum(xprop: XPropEnum): CPropView {

                val iconResource = javaClass.getResource(xprop.resourcePath())
                        ?: throw GadsuException("Please create an icon for property '${xprop.label}' located at: ${xprop.resourcePath()}")
                val icon = ImageIcon(iconResource)
                val view = CPropEnumView(icon, xprop, bus)
                // got no view name yet, as no tests yet ;)
                fields.register(view)
                return view
            }
        })
    }

    private fun XPropEnum.resourcePath() = "/gadsu/images/tcm_props/${this.key}.png"

}

interface CPropView : EditorRendererSwitchable {
    // keep it this way
    fun updateValue(value: Client)
    fun toComponent(): Component
    fun toCProp(): CProp

    val fillType: GridBagFill
    val icon: ImageIcon?
}

enum class GridBagFill(val swingId: Int) {
    None(GridBagConstraints.NONE),
    Horizontal(GridBagConstraints.HORIZONTAL),
    Vertical(GridBagConstraints.VERTICAL),
    Both(GridBagConstraints.BOTH)
}
