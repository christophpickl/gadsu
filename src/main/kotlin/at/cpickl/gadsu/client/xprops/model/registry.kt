package at.cpickl.gadsu.client.xprops.model

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.xprops.model.XPropsFactory.newEnum
import at.cpickl.gadsu.client.xprops.model.XPropsFactory.newEnumOpt
import at.cpickl.gadsu.view.language.Labels
import java.util.HashMap

interface XPropsFinder {
    fun findByKey(key: String): XProp
    fun findEnumValueByKey(key: String): XPropEnumOpt
}

private object XPropsFactory : XPropsFinder {
    private val xpropByKey: HashMap<String, XProp> = HashMap()
    private val xpropEnumOptByKey: HashMap<String, XPropEnumOpt> = HashMap()

    override fun findByKey(key: String): XProp {
        if (!xpropByKey.containsKey(key)) {
            throw GadsuException("Invalid xproperty key '$key'!")
        }
        return xpropByKey.get(key)!!
    }

    override fun findEnumValueByKey(key: String): XPropEnumOpt {
        if (!xpropEnumOptByKey.containsKey(key)) {
            throw GadsuException("Invalid xproperty enum value key '$key'!")
        }
        return xpropEnumOptByKey.get(key)!!
    }

    fun newEnum(key: String, vararg values: XPropEnumOpt): XPropEnum {
        val label = Labels.XProps.labelFor(key)
        val xprop = XPropEnum(key, label, listOf(*values))
        if (xpropByKey.containsKey(key)) {
            throw GadsuException("Duplicate xproperty key '$key'!")
        }
        xpropByKey.put(key, xprop)
        return xprop
    }

    fun newEnumOpt(order: Int, key: String): XPropEnumOpt {
        val label = Labels.XProps.labelFor(key)
        val value = XPropEnumOpt(order, key, label)
        if (xpropEnumOptByKey.containsKey(key)) {
            throw GadsuException("Duplicate xproperty enum value key '$key'!")
        }
        xpropEnumOptByKey.put(key, value)
        return value
    }

}


object XPropsRegistry : XPropsFinder by XPropsFactory {

    val Sleep_TiredInMorning     = newEnumOpt(1, "Sleep_TiredInMorning")
    val Sleep_TiredInEvening     = newEnumOpt(2, "Sleep_TiredInEvening")
    val Sleep_ProblemsFallAsleep = newEnumOpt(3, "Sleep_ProblemsFallAsleep")
    val Sleep_ProblemsWakeUp     = newEnumOpt(4, "Sleep_ProblemsWakeUp")
    val Sleep = newEnum("Sleep", Sleep_TiredInMorning, Sleep_TiredInEvening, Sleep_ProblemsFallAsleep, Sleep_ProblemsWakeUp)



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

