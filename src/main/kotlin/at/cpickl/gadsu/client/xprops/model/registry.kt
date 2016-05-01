package at.cpickl.gadsu.client.xprops.model

import at.cpickl.gadsu.GadsuException
import java.util.HashMap



object XPropsRegistry {

    val Sleep_TiredInMorning = newEnumOpt(1, "Sleep_TiredInMorning")
    val Sleep_TiredInEvening = newEnumOpt(2, "Sleep_TiredInEvening")
    val Sleep = newEnum("Sleep", Sleep_TiredInMorning, Sleep_TiredInEvening)

    fun findByKey(key: String): XProp {
        if (!xpropByKey.containsKey(key)) {
            throw GadsuException("Invalid xproperty key '$key'!")
        }
        return xpropByKey.get(key)!!
    }

    fun findEnumValueByKey(key: String): XPropEnumOpt {
        if (!xpropEnumOptByKey.containsKey(key)) {
            throw GadsuException("Invalid xproperty enum value key '$key'!")
        }
        return xpropEnumOptByKey.get(key)!!
    }


    //    enum class Strings(val key: String) {
    //        MoodOfToday("MoodOfToday")
    //    }
    //
    //    object Enums {
    //        enum class SleepEnum(override val key: String) : HasKey {
    //            ProblemsFallAsleep("ProblemsFallAsleepKeeey"),
    //            ProblemsWakeUp("ProblemsWakeUp"),
    //            TiredInTheMorning("TiredInTheMorning"),
    //            TiredInTheEvening("TiredInTheEvening");
    //
    //            companion object : HasKey {
    //                override val key = "Sleep"
    //            }
    //        }
    //
    //    }
}


private val xpropByKey: HashMap<String, XProp> = HashMap()
private val xpropEnumOptByKey: HashMap<String, XPropEnumOpt> = HashMap()

private fun newEnum(key: String, vararg values: XPropEnumOpt): XPropEnum {
    val label = key
    val xprop = XPropEnum(key, label, listOf(*values))
    if (xpropByKey.containsKey(key)) {
        throw GadsuException("Duplicate xproperty key '$key'!")
    }
    xpropByKey.put(key, xprop)
    return xprop
}

private fun newEnumOpt(order: Int, key: String): XPropEnumOpt {
    val label = key
    val value = XPropEnumOpt(order, key, label)
    if (xpropEnumOptByKey.containsKey(key)) {
        throw GadsuException("Duplicate xproperty enum value key '$key'!")
    }
    xpropEnumOptByKey.put(key, value)
    return value
}
