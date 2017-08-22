package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.xprops.model.CProp
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.tcm.model.IsEnumOption
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.tcm.model.ZangOrgan


enum class SymptomCategory {
    Atmung,
    Emotion,
    Energie,
    Essen,
    Farbe,
    Gesicht,
    Hoeren,
    Mens,
    Misc,
    Psycho,
    Puls,
    Schmerzen,
    Schlaf,
    Schweiss,
    Sehen,
    Sprechen,
    Stuhl,
    Temperatur,
    Trinken,
    Urin,
    Verdauung,
    Zunge,
    ;
}

sealed class Symptom(
        val category: SymptomCategory,
        val xprop: IsEnumOption,
        val leitSymptomFor: ZangOrgan? = null
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

    abstract class AtmungSymptom(xprop: IsEnumOption, leitSymptomFor: ZangOrgan? = null) : Symptom(SymptomCategory.Atmung, xprop, leitSymptomFor)
    object Asthma : AtmungSymptom(XProps.NOT.IMPLEMENTED)
    object FlacheAtmung : AtmungSymptom(XProps.NOT.IMPLEMENTED)
    object Husten : AtmungSymptom(XProps.NOT.IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)
    object TrockenerHusten : AtmungSymptom(XProps.NOT.IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)
    object Kurzatmigkeit : AtmungSymptom(XProps.NOT.IMPLEMENTED)
    object TrockenerHals : AtmungSymptom(XProps.NOT.IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)
    object Heiserkeit : AtmungSymptom(XProps.NOT.IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)
    object Stimmbaender : AtmungSymptom(XProps.NOT.IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)

    abstract class EmotionSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Emotion, xprop)
    object TrauererloseDepression : EmotionSymptom(XProps.NOT.IMPLEMENTED)

    abstract class EnergieSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Energie, xprop)
    object EnergieMangel : EnergieSymptom(XProps.NOT.IMPLEMENTED)
    object Muedigkeit : EnergieSymptom(XProps.NOT.IMPLEMENTED)

    abstract class EssenSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Essen, xprop)

    abstract class FarbeSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Farbe, xprop)
    object Blaesse : FarbeSymptom(XProps.NOT.IMPLEMENTED)

    abstract class GesichtSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Gesicht, xprop)

    abstract class HoerenSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Hoeren, xprop)

    abstract class MensSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Mens, xprop)

    abstract class MiscSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Misc, xprop)

    abstract class PsychoSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Psycho, xprop)

    // TODO #114 add pulse symptom, extracting its value from any treatment
    abstract class PulsSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Puls, xprop)
    object PulsLeer : PulsSymptom(XProps.NOT.IMPLEMENTED)
    object PulsWeich : PulsSymptom(XProps.NOT.IMPLEMENTED)
    object PulsSchwach : PulsSymptom(XProps.NOT.IMPLEMENTED)
    object PulsBeschleunigt : PulsSymptom(XProps.NOT.IMPLEMENTED)
    object PulsDuenn : PulsSymptom(XProps.NOT.IMPLEMENTED)

    abstract class SchmerzenSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Schmerzen, xprop)

    abstract class SchlafSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Schlaf, xprop)
    object Traeumen : SchlafSymptom(XProps.NOT.IMPLEMENTED)
    object EinschlafStoerungen : SchlafSymptom(XProps.NOT.IMPLEMENTED)

    abstract class SchweissSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Schweiss, xprop)
    object LeichtesSchwitzen : SchlafSymptom(XProps.TemperatureOpts.SweatEasily)

    abstract class SehenSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Sehen, xprop)

    abstract class SprechenSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Sprechen, xprop)
    object WenigLeiseSprechen : SprechenSymptom(XProps.NOT.IMPLEMENTED)

    abstract class StuhlSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Stuhl, xprop)

    abstract class TemperaturSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Temperatur, xprop)
    object AversionKaelte : TemperaturSymptom(XProps.TemperatureOpts.AversionCold)
    object Erkaeltungen : TemperaturSymptom(XProps.NOT.IMPLEMENTED)
    object HitzeGefuehlAbends : TemperaturSymptom(XProps.NOT.IMPLEMENTED)

    abstract class TrinkenSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Trinken, xprop)

    abstract class UrinSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Urin, xprop)

    abstract class VerdauungSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Verdauung, xprop)

    abstract class ZungeSymptom(xprop: IsEnumOption) : Symptom(SymptomCategory.Zunge, xprop)
    object ZungeRot : ZungeSymptom(XProps.NOT.IMPLEMENTED)
    object ZungeTrocken : ZungeSymptom(XProps.NOT.IMPLEMENTED)
    object WenigBelag : ZungeSymptom(XProps.NOT.IMPLEMENTED)

}

