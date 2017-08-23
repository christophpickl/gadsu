package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.tcm.model.IsEnumOption
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.tcm.model.ZangOrgan
import at.cpickl.gadsu.tcm.patho.Symptom.SymptomSource.NOT_IMPLEMENTED
import at.cpickl.gadsu.treatment.dyn.treats.PulseProperty


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
        val source: SymptomSource,
        val leitSymptomFor: ZangOrgan? = null
        // maybe add: weighting: Double ???
) {
    companion object {

        private val allMutable = ArrayList<Symptom>()

        val all: List<Symptom> = allMutable

        val byXpropEnumOpt: Map<XPropEnumOpt, Symptom> by lazy {
            all
                    .filter { it.source is SymptomSource.XPropSource }
                    .associateBy { (it.source as SymptomSource.XPropSource).option.opt }
        }

        val byPulseProperty: Map<PulseProperty, Symptom> by lazy {
            all
                    .filter { it.source is SymptomSource.PulseSource }
                    .associateBy { (it.source as SymptomSource.PulseSource).property }
        }

    }

    init {
        allMutable += this
    }

    // -----------------------------------------------------------------------------------------------------------------

    sealed class SymptomSource {
        abstract val label: String

        class XPropSource(val xenum: XPropEnum, val option: IsEnumOption) : SymptomSource() {
            override val label = option.opt.label
        }

        class PulseSource(val property: PulseProperty) : SymptomSource() {
            override val label = "Puls ${property.label}"
        }

        object NOT_IMPLEMENTED : SymptomSource() {
            override val label = "N/A"
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    abstract class AtmungSymptom(source: SymptomSource, leitSymptomFor: ZangOrgan? = null) : Symptom(SymptomCategory.Atmung, source, leitSymptomFor)
    object Asthma : AtmungSymptom(NOT_IMPLEMENTED)
    object FlacheAtmung : AtmungSymptom(NOT_IMPLEMENTED)
    object Husten : AtmungSymptom(NOT_IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)
    object TrockenerHusten : AtmungSymptom(NOT_IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)
    object Kurzatmigkeit : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockenerHals : AtmungSymptom(NOT_IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)
    object Heiserkeit : AtmungSymptom(NOT_IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)
    object Stimmbaender : AtmungSymptom(NOT_IMPLEMENTED, leitSymptomFor = ZangOrgan.Lung)


    abstract class EmotionSymptom(source: SymptomSource) : Symptom(SymptomCategory.Emotion, source)
    object TrauererloseDepression : EmotionSymptom(NOT_IMPLEMENTED)

    abstract class EnergieSymptom(source: SymptomSource) : Symptom(SymptomCategory.Energie, source)
    object EnergieMangel : EnergieSymptom(NOT_IMPLEMENTED)
    object Muedigkeit : EnergieSymptom(NOT_IMPLEMENTED)

    abstract class EssenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Essen, source)

    abstract class FarbeSymptom(source: SymptomSource) : Symptom(SymptomCategory.Farbe, source)
    object Blaesse : FarbeSymptom(NOT_IMPLEMENTED)

    abstract class GesichtSymptom(source: SymptomSource) : Symptom(SymptomCategory.Gesicht, source)

    abstract class HoerenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Hoeren, source)

    abstract class MensSymptom(source: SymptomSource) : Symptom(SymptomCategory.Mens, source)

    abstract class MiscSymptom(source: SymptomSource) : Symptom(SymptomCategory.Misc, source)

    abstract class PsychoSymptom(source: SymptomSource) : Symptom(SymptomCategory.Psycho, source)

    abstract class PulsSymptom(source: SymptomSource) : Symptom(SymptomCategory.Puls, source) {
        constructor(pulse: PulseProperty) : this(SymptomSource.PulseSource(pulse))
    }

    object PulsLeer : PulsSymptom(NOT_IMPLEMENTED)
    object PulsWeich : PulsSymptom(PulseProperty.Soft)
    object PulsSchwach : PulsSymptom(NOT_IMPLEMENTED)
    object PulsBeschleunigt : PulsSymptom(NOT_IMPLEMENTED)
    object PulsDuenn : PulsSymptom(NOT_IMPLEMENTED)

    abstract class SchmerzenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schmerzen, source)

    abstract class SchlafSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schlaf, source)
    object Traeumen : SchlafSymptom(NOT_IMPLEMENTED)
    object EinschlafStoerungen : SchlafSymptom(NOT_IMPLEMENTED)

    abstract class SchweissSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schweiss, source)
    object LeichtesSchwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))

    abstract class SehenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Sehen, source)

    abstract class SprechenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Sprechen, source)
    object WenigLeiseSprechen : SprechenSymptom(NOT_IMPLEMENTED)

    abstract class StuhlSymptom(source: SymptomSource) : Symptom(SymptomCategory.Stuhl, source)

    abstract class TemperaturSymptom(source: SymptomSource) : Symptom(SymptomCategory.Temperatur, source)
    object AversionKaelte : TemperaturSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.AversionCold))
    object Erkaeltungen : TemperaturSymptom(NOT_IMPLEMENTED)
    object HitzeGefuehlAbends : TemperaturSymptom(NOT_IMPLEMENTED)

    abstract class TrinkenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Trinken, source)

    abstract class UrinSymptom(source: SymptomSource) : Symptom(SymptomCategory.Urin, source)

    abstract class VerdauungSymptom(source: SymptomSource) : Symptom(SymptomCategory.Verdauung, source)

    abstract class ZungeSymptom(source: SymptomSource) : Symptom(SymptomCategory.Zunge, source)
    object ZungeRot : ZungeSymptom(NOT_IMPLEMENTED)
    object ZungeTrocken : ZungeSymptom(NOT_IMPLEMENTED)
    object WenigBelag : ZungeSymptom(NOT_IMPLEMENTED)

}

