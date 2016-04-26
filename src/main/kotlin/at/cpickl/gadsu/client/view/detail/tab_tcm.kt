package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientProps
import at.cpickl.gadsu.client.MultiEnumProp
import at.cpickl.gadsu.client.props.HasKey
import at.cpickl.gadsu.client.props.Props
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.Labels
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.ModificationAware
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListModel
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.GridBagConstraints
import java.util.LinkedList
import javax.swing.ListSelectionModel
import kotlin.reflect.KClass
import kotlin.reflect.companionObjectInstance

fun main(args: Array<String>) {
    Framed.showWithContext({ context ->
        ClientTabTcm(Client.INSERT_PROTOTYPE.copy(id = "1", props = ClientProps(mapOf(
                Pair(Props.Enums.SleepEnum.key, MultiEnumProp(listOf(Props.Enums.SleepEnum.ProblemsFallAsleep.key,
                        Props.Enums.SleepEnum.TiredInTheMorning.key)))
        ))
        ), ModificationChecker(object : ModificationAware {
            override fun isModified() = false
        }))
    })
}

data class MetaEnumProp(val key: String, val allOptionsKeys: List<String>)// val options: Map</*key:*/String, /*selected*/Boolean>)

fun metaFetchProp(propType: KClass<*>): MetaEnumProp {
    val allOptionsKeys = LinkedList<String>()

    val propKey = (propType.companionObjectInstance!! as HasKey).key
    propType.java.declaredFields.filter { it.isEnumConstant }.forEach {
        val optionKey = (it.get(null) as HasKey).key
        allOptionsKeys.add(optionKey)
    }

    return MetaEnumProp(propKey, allOptionsKeys)
}


class PropsRenderer(
        private val client: Client,
        private val fields: Fields<Client>) {

    fun labelAndComponentFor(propType: KClass<*>): Pair<String, Component> {
        val metaProp = metaFetchProp(propType)
        val label = metaProp.key // TODO lookup proper label by key


        val model = MyListModel<String>()
        model.resetData(metaProp.allOptionsKeys)
        val list = MyList<String>("FIXME", model, EventBus(), null)
        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        list.visibleRowCount = 3

        if (client.props.properties.containsKey(metaProp.key)) {
            val enumProp = client.props.properties[metaProp.key] as MultiEnumProp
            list.addSelectedValues(enumProp.entries)
        }

        return Pair(label, list)
    }

//    fun selectedOptions(metaProp: MetaEnumProp): Map<String, Boolean> {
//        val map = HashMap<String, Boolean>()
//        metaProp.allOptionsKeys.forEach { map.put(it, false) }
//        val enumProp = client.props.properties[metaProp.key] as MultiEnumProp
//        enumProp.entries.forEach {
//            val oldValue = map.put(it, true)
//            assert(oldValue == false)
//        }
//        return map
//    }

}

fun FormPanel.addProp(renderer: PropsRenderer, propType: KClass<*>) {
    val pair = renderer.labelAndComponentFor(propType)
    addFormInput(pair.first, pair.second)
}

class ClientTabTcm(
        initialClient: Client,
        modificationChecker: ModificationChecker
) : DefaultClientTab(Labels.Tabs.ClientTcm) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val fields = Fields<Client>(modificationChecker)

    init {
        val renderer = PropsRenderer(initialClient, fields)
        val form1 = FormPanel()

        form1.addProp(renderer, Props.Enums.SleepEnum::class)

        c.weightx = 1.0
        c.weighty = 1.0
        c.fill = GridBagConstraints.BOTH
        add(form1)
    }

    override fun isModified(client: Client): Boolean {
        return false
    }

    override fun updateFields(client: Client) {

    }
}
