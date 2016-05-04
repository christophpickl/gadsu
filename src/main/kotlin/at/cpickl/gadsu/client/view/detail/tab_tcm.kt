package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.XProps
import at.cpickl.gadsu.client.xprops.view.CPropsRenderer
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import java.awt.Color
import java.awt.GridBagConstraints
import javax.swing.JPanel

class ClientTabTcm(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        bus: EventBus
) : DefaultClientTab(Labels.Tabs.ClientTcm) {

    private val fields = Fields<Client>(modificationChecker)
    private val renderer = CPropsRenderer(fields, bus)

    init {
        debugColor = Color.WHITE
        val form1 = FormPanel()
        form1.enforceWidth(200)
        form1.debugColor = Color.YELLOW
        renderer.addXProp(XProps.Sleep, form1)
        form1.addLastColumnsFilled()

        val form2 = FormPanel()
        form2.enforceWidth(200)
        form2.debugColor = Color.GREEN
        renderer.addXProp(XProps.Hungry, form2)
        form2.addLastColumnsFilled()


        renderer.updateFields(initialClient)

        c.weightx = 0.0
        c.weighty = 1.0
        c.fill = GridBagConstraints.VERTICAL
        c.anchor = GridBagConstraints.NORTH
        c.insets = Pad.RIGHT
        add(form1)

        c.gridx++
        add(form2)

        c.gridx++
        c.weightx = 1.0
        c.fill = GridBagConstraints.BOTH
        add(JPanel().transparent()) // ui hack ;)
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
