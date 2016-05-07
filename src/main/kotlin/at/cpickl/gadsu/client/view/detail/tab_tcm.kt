package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.view.CPropsRenderer
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.Pad
import com.google.common.eventbus.EventBus
import java.awt.Color
import java.awt.GridBagConstraints

class ClientTabTcm(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        bus: EventBus
) : DefaultClientTab(
        title = Labels.Tabs.ClientTcm,
        scrolled = false
) {

    private val fields = Fields<Client>(modificationChecker)
    private val renderer = CPropsRenderer(fields, bus)

    private fun form(vararg xprops: XPropEnum): FormPanel {
        return FormPanel(labelAnchor = GridBagConstraints.NORTH).apply {
            xprops.forEach { enum(it) }
//            addLastColumnsFilled()
        }
    }

    init {
        debugColor = Color.PINK

        renderer.updateFields(initialClient)

        c.weightx = 0.3
        c.weighty = 1.0
        c.fill = GridBagConstraints.BOTH
        c.anchor = GridBagConstraints.NORTH
        c.insets = Pad.RIGHT

        add(form(XProps.BodyConception, XProps.Sleep, XProps.Hungry))
        c.gridx++
        // got additional string field: "TemperatureColdLocation" / "MenstruationTimes"
        add(form(XProps.Taste, XProps.Temperature, XProps.Menstruation))
        c.gridx++
        c.insets = Pad.ZERO
        add(form(XProps.Tongue, XProps.Pulse, XProps.ChiLocation))

//        c.gridx++
//        c.weightx = 1.0
//        c.fill = GridBagConstraints.BOTH
//        c.insets = Pad.ZERO
//        add(JPanel().transparent()) // ui hack ;)
    }

    fun FormPanel.enum(xprop: XPropEnum): Unit {
        renderer.addXProp(xprop, this)
    }

    override fun isModified(client: Client): Boolean {
        return fields.isAnyModified(client)
    }

    override fun updateFields(client: Client) {
        renderer.updateFields(client)
    }

    fun readProps(): CProps {
        return renderer.readCProps()
    }
}
