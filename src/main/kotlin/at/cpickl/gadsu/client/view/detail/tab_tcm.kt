package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.XPropsRegistry
import at.cpickl.gadsu.client.xprops.view.CPropsRenderer
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.ModificationChecker
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.awt.GridBagConstraints

class ClientTabTcm(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        bus: EventBus
) : DefaultClientTab(Labels.Tabs.ClientTcm) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val fields = Fields<Client>(modificationChecker)
    private val renderer = CPropsRenderer(fields, bus)

    init {

        val form1 = FormPanel()

        renderer.addXProp(XPropsRegistry.Sleep, form1)
//        renderer.add(Props.Enums.SleepEnum::class, form1)
//        form1.addProp(renderer, Props.Enums.SleepEnum::class)

        renderer.updateFields(initialClient)

        c.weightx = 1.0
        c.weighty = 1.0
        c.fill = GridBagConstraints.BOTH
        add(form1)
    }

    override fun isModified(client: Client): Boolean {
        return false
    }

    override fun updateFields(client: Client) {
        renderer.updateFields(client)
    }

    fun readProps(): CProps {
        return renderer.readCProps()
    }
}
