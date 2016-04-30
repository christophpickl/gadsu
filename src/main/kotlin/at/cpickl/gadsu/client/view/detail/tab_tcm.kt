package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.props.PropsRenderer
import at.cpickl.gadsu.client.xprops.ClientXProps
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.Labels
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.ModificationChecker
import org.slf4j.LoggerFactory
import java.awt.GridBagConstraints

class ClientTabTcm(
        initialClient: Client,
        modificationChecker: ModificationChecker
) : DefaultClientTab(Labels.Tabs.ClientTcm) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val fields = Fields<Client>(modificationChecker)
    private val renderer = PropsRenderer(fields)

    init {

        val form1 = FormPanel()

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

    fun readProps(): ClientXProps {
        return ClientXProps.empty
//        return ClientProps(mapOf(
//            Pair(Props.Enums.SleepEnum.key,
//            MultiEnumProp(listOf(
//                Props.Enums.SleepEnum.ProblemsFallAsleep.key,
//                Props.Enums.SleepEnum.TiredInTheMorning.key
//            )))
//        ))
    }
}
