package at.cpickl.gadsu.client.xprops.model

import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.tcm.model.XPropsFinder
import at.cpickl.gadsu.view.language.Labels
import org.slf4j.LoggerFactory
import java.util.HashMap
import java.util.LinkedList


object XPropsFactory : XPropsFinder {

    private val log = LoggerFactory.getLogger(javaClass)

    private val xpropByKey: HashMap<String, XProp> = HashMap()
    private val allMutableXProps = mutableListOf<XProp>()
    val allXProps: List<XProp> get() = allMutableXProps

    private val xpropEnumOptByKey: HashMap<String, XPropEnumOpt> = HashMap()

    private var collectedEnumOpts: LinkedList<XPropEnumOpt> = LinkedList()
    private var previousEnumOptKeyPrefix: String = "xxxxx"
    private var currentEnumOrder: Int = 1

    override fun findByKey(key: String): XProp {
        if (!xpropByKey.containsKey(key)) {
            throw GadsuException("Invalid xproperty key '$key'!")
        }
        return xpropByKey[key]!!
    }

    override fun findEnumValueByKey(key: String): XPropEnumOpt {
        if (!xpropEnumOptByKey.containsKey(key)) {
            throw GadsuException("Invalid xproperty enum value key '$key'!")
        }
        return xpropEnumOptByKey[key]!!
    }


    fun enum(key: String, label: String): XPropEnum {
//        val label = Labels.XProps.labelFor(key) disable for the moment...
        val xprop = XPropEnum(key, label, collectedEnumOpts.sorted())
        collectedEnumOpts = LinkedList()
        if (xpropByKey.containsKey(key)) {
            throw GadsuException("Duplicate xproperty key '$key'!")
        }
        allMutableXProps += xprop
        xpropByKey.put(key, xprop)
        return xprop
    }

    fun opt(key: String, staticLabel: String? = null): XPropEnumOpt {
        val keyPrefix = (key.substring(0, key.indexOf("_")))
        if (!keyPrefix.equals(previousEnumOptKeyPrefix)) {
            currentEnumOrder = 1
        }
        val label = staticLabel ?: Labels.XProps.labelFor(key)
        val value = XPropEnumOpt(currentEnumOrder++, key, label)
        if (xpropEnumOptByKey.containsKey(key)) {
            throw GadsuException("Duplicate xproperty enum value key '$key'!")
        }
        xpropEnumOptByKey.put(key, value)

        collectedEnumOpts.add(value)
        previousEnumOptKeyPrefix = keyPrefix
        return value
    }


}
