package at.cpickl.gadsu.client.props

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.view.detail.ClientTabTcm
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.ModificationAware
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListModel
import com.google.common.eventbus.EventBus
import java.awt.Component
import java.util.HashMap
import java.util.LinkedList
import javax.swing.ListSelectionModel
import kotlin.reflect.KClass
import kotlin.reflect.companionObjectInstance


fun main(args: Array<String>) {
    Framed.showWithContext({ context ->
        ClientTabTcm(Client.INSERT_PROTOTYPE.copy(id = "1"/*, props = ClientProps(mapOf(
                Pair(Props.Enums.SleepEnum.key, MultiEnumProp(listOf(Props.Enums.SleepEnum.ProblemsFallAsleep.key,
                        Props.Enums.SleepEnum.TiredInTheMorning.key)))))*/
        ), ModificationChecker(object : ModificationAware {
            override fun isModified() = false
        }), context.bus)
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


class PropsRenderer(private val fields: Fields<Client>) {

    private val map: HashMap<KClass<*>, Component> = HashMap()

    private fun labelAndComponentFor(propType: KClass<*>): Pair<String, Component> {
        val metaProp = metaFetchProp(propType)
        val label = metaProp.key // TODO lookup proper label by key

        val model = MyListModel<String>()
        model.resetData(metaProp.allOptionsKeys)
        val list = MyList<String>("FIXME", model, EventBus(), null)
        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        list.visibleRowCount = 3


        return Pair(label, list)
    }

    fun add(propType: KClass<*>, form: FormPanel) {
        val pair = labelAndComponentFor(propType)
        map.put(propType, pair.second)
        form.addFormInput(pair.first, pair.second)
    }

    fun updateFields(client: Client) {
//        map.forEach { propType, component ->
//            val metaPropKey = (propType.companionObjectInstance!! as HasKey).key
//            val list = component as MyList<String>
//            list.clearSelection()
//            if (client.props.properties.containsKey(metaPropKey)) {
//                val enumProp = client.props.properties[metaPropKey] as MultiEnumProp
//                list.addSelectedValues(enumProp.entries)
//            }
//        }

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

//fun FormPanel.addProp(renderer: PropsRenderer, propType: KClass<*>) {
//    val pair = renderer.labelAndComponentFor(propType)
//    addFormInput(pair.first, pair.second)
//}
