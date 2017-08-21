package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.xprops.model.CProp
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.tcm.model.IsEnumOption
import at.cpickl.gadsu.tcm.model.XProps


enum class SymptomCategory {
    Generic,
    Pulse,
    Eat;
}

sealed class Symptom(
        val category: SymptomCategory,
        val xprop: IsEnumOption
        // maybe add: weighting: Double ???
) {
    companion object {
        private val allMutable = ArrayList<Symptom>()
        val all: List<Symptom> = allMutable
        val byXpropEnumOpt: Map<XPropEnumOpt, Symptom> by lazy { all.associateBy { it.xprop.opt } }
        fun findByCProp(cprop: CProp): Symptom? {
            cprop.clientValue
            return null
        }
    }
    init {
        allMutable += this
    }

    // -----------------------------------------------------------------------------------------------------------------

    abstract class GenericSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Generic, xprop)
    object Traeumen : GenericSymptom(XProps.SleepOpts.Dreams)
    object EinschlafStoerungen : GenericSymptom(XProps.SleepOpts.ProblemsFallAsleep)

    // TODO #114 add pulse symptom, extracting its value from any treatment
//    abstract class PulseSymptom : Symptom(SymptomCategory.Pulse)
//    object SchwacherPuls : PulseSymptom()
//    object WeicherPuls : PulseSymptom()
//    object LeererPuls : PulseSymptom()

    abstract class EatSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Eat, xprop)
    object BigHunger: EatSymptom(XProps.HungryOpts.BigHunger)
    object LittleHunger : EatSymptom(XProps.HungryOpts.LittleHunger)

}

