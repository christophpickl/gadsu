package at.cpickl.gadsu.client.xprops.model

import at.cpickl.gadsu.client.xprops.model.XPropsFactory.enum
import at.cpickl.gadsu.client.xprops.model.XPropsFactory.opt
import at.cpickl.gadsu.service.LogConfigurator


fun main(args: Array<String>) {
    LogConfigurator(true).configureLog()
    println("Sleep.options: " + XProps.Sleep.options)
    println("Hungry.options: " + XProps.Hungry.options)
}

interface XPropsFinder {
    fun findByKey(key: String): XProp
    fun findEnumValueByKey(key: String): XPropEnumOpt
}

object XProps : XPropsFinder by XPropsFactory {

    /*
    Schlafverhalten: brauche viel/wenig Schlaf, Probleme mit Einschlafen/Durchschlafen, fühle mich morgens/untertags müde
Essverhalten/Verdauung: habe eher viel/weniger Appetit, Verdauung eher träge/schnell, Stuhl gut geformt und hart/eher weiche und ungeformt
Ich esse gerne: süss, salzig, sauer, scharf, bitter
Temperaturhaushalt: mir ist meistens warm/kalt (wo?), ich schwitze selten/leicht, Nachtschweiß, schnell bei Stress
Zyklus: Dauer (Intervall, Dauer), schwache/kärftige Menstruation, Blut dunkel/hell und dünn, deutliche Vorzeichen vor dem Zyklus, Pille oder hormonelle Verhütung, Klimakterium, Operationen
Konstitution yin/yang, Kondition yin/yang
     */

    private val eager_sleep = SleepOpts.TiredInEvening
    enum class SleepOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        TiredInMorning     (opt("Sleep_TiredInMorning")),
        TiredInEvening     (opt("Sleep_TiredInEvening")),
        ProblemsWakeUp     (opt("Sleep_ProblemsWakeUp")),
        ProblemsFallAsleep (opt("Sleep_ProblemsFallAsleep"))
    }
    val Sleep = enum("Sleep")


    private val eager_hungry = HungryOpts.BigHunger
    enum class HungryOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        BigHunger    (opt("Hungry_BigHunger")),
        LittleHunger (opt("Hungry_LittleHunger"))
    }
    val Hungry = enum("Hungry")

}

interface IsEnumOption {
    val opt: XPropEnumOpt
}
