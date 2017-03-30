package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.view.CPropsRenderer
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.addFormInput
import at.cpickl.gadsu.view.components.MultiPropExpander
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.components.panels.VFillFormPanel
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
        tabTitle = Labels.Tabs.ClientTcm,
        type = ClientTabType.TCM,
        scrolled = false
) {

    private val fields = Fields<Client>(modificationChecker)
    private val renderer = CPropsRenderer(fields, bus)

    val inpTcmNote = fields.newTextArea("Notiz", { it.tcmNote }, ViewNames.Client.InputTcmNote, bus)

    private fun form(vararg xprops: XPropEnum): FormPanel {
        return FormPanel(labelAnchor = GridBagConstraints.NORTH).apply {
            xprops.forEach { addXPropEnum(it) }
//            addLastColumnsFilled()
        }
    }

    init {
        debugColor = Color.PINK

        renderer.updateFields(initialClient)
        val row1 = form(XProps.Impression, XProps.Temperature, XProps.Sleep)
        val row2 = form(XProps.BodyConception, XProps.Hungry, XProps.Digestion)
        val row3 = form(XProps.ChiStatus, XProps.Liquid, XProps.Menstruation)

        c.anchor = GridBagConstraints.EAST
        c.gridwidth = 3
        add(MultiPropExpander(renderer.allSwitchables)) // requires all xprops already added to renderer!
        c.gridy++

        c.gridwidth = 1
        c.weightx = 0.3
        c.weighty = 1.0
        c.fill = GridBagConstraints.BOTH
        c.anchor = GridBagConstraints.NORTH
        c.insets = Pad.RIGHT

        add(row1)
        c.gridx++
        add(row2)
        c.gridx++
        c.insets = Pad.ZERO
        add(row3)

        c.gridx = 0
        c.gridy++
        c.weightx = 0.0
        c.weighty = 0.0
        c.gridwidth = 3
        c.fill = GridBagConstraints.HORIZONTAL
//        inpTcmNote.enforceHeight(60)
        add(VFillFormPanel().apply {
            addFormInput(inpTcmNote)
        })

    }

    fun FormPanel.addXPropEnum(xprop: XPropEnum): Unit {
        renderer.addXProp(xprop, this)
    }

    override fun isModified(client: Client) =
            fields.isAnyModified(client)

    override fun updateFields(client: Client) {
        renderer.updateFields(client)
        fields.updateAll(client)
    }

    fun readProps(): CProps = renderer.readCProps()
}
