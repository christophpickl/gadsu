package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.tcm.model.IsEnumOption
import at.cpickl.gadsu.tcm.model.XProps
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
    Schleim,
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
        val source: SymptomSource
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


    // ATMUNG
    abstract class AtmungSymptom(source: SymptomSource) : Symptom(SymptomCategory.Atmung, source)
    object Asthma : AtmungSymptom(NOT_IMPLEMENTED)
    object FlacheAtmung : AtmungSymptom(NOT_IMPLEMENTED)
    object Kurzatmigkeit : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockenerHals : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockenerRachen : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockeneNase : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockenerMund : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockenerHusten : AtmungSymptom(NOT_IMPLEMENTED)
    object Husten : AtmungSymptom(NOT_IMPLEMENTED)
    object Stimmbaender : AtmungSymptom(NOT_IMPLEMENTED)
    object Heiserkeit : AtmungSymptom(NOT_IMPLEMENTED)
    object KratzenderHals : AtmungSymptom(NOT_IMPLEMENTED)
    object Halsschmerzen : AtmungSymptom(NOT_IMPLEMENTED)
    object RoterHals : AtmungSymptom(NOT_IMPLEMENTED)
    object RoterRachen : AtmungSymptom(NOT_IMPLEMENTED)
    object Schnupfen : AtmungSymptom(NOT_IMPLEMENTED)
    object VerstopfteNase : AtmungSymptom(NOT_IMPLEMENTED)
    object Nasenbluten : AtmungSymptom(NOT_IMPLEMENTED)

    // EMOTION
    abstract class EmotionSymptom(source: SymptomSource) : Symptom(SymptomCategory.Emotion, source)
    object TrauererloseDepression : EmotionSymptom(NOT_IMPLEMENTED)

    // ENERGIE
    abstract class EnergieSymptom(source: SymptomSource) : Symptom(SymptomCategory.Energie, source)
    object EnergieMangel : EnergieSymptom(NOT_IMPLEMENTED)
    object Muedigkeit : EnergieSymptom(NOT_IMPLEMENTED)

    // ESSEN
    abstract class EssenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Essen, source)

    // FARBE
    abstract class FarbeSymptom(source: SymptomSource) : Symptom(SymptomCategory.Farbe, source)
    object Blaesse : FarbeSymptom(NOT_IMPLEMENTED)

    // GESICHT
    abstract class GesichtSymptom(source: SymptomSource) : Symptom(SymptomCategory.Gesicht, source)

    // HOEREN
    abstract class HoerenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Hoeren, source)

    // MENS
    abstract class MensSymptom(source: SymptomSource) : Symptom(SymptomCategory.Mens, source)

    // MISC
    abstract class MiscSymptom(source: SymptomSource) : Symptom(SymptomCategory.Misc, source)

    // PSYCHO
    abstract class PsychoSymptom(source: SymptomSource) : Symptom(SymptomCategory.Psycho, source)

    // PULS
    abstract class PulsSymptom(source: SymptomSource) : Symptom(SymptomCategory.Puls, source) {
        constructor(pulse: PulseProperty) : this(SymptomSource.PulseSource(pulse))
    }
    object PulsLeer : PulsSymptom(NOT_IMPLEMENTED)
    object PulsWeich : PulsSymptom(PulseProperty.Soft)
    object PulsSchwach : PulsSymptom(NOT_IMPLEMENTED)
    object PulsDuenn : PulsSymptom(NOT_IMPLEMENTED)
    object PulsOberflaechlich : PulsSymptom(NOT_IMPLEMENTED)
    object PulsGespannt : PulsSymptom(NOT_IMPLEMENTED)
    object PulsVerlangsamt : PulsSymptom(NOT_IMPLEMENTED)
    object PulsBeschleunigt : PulsSymptom(NOT_IMPLEMENTED)


    // SCHMERZEN
    abstract class SchmerzenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schmerzen, source)
    object LeichteKopfschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Kopfschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Muskelschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)

    // SCHLAF
    abstract class SchlafSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schlaf, source)
    object Traeumen : SchlafSymptom(NOT_IMPLEMENTED)
    object EinschlafStoerungen : SchlafSymptom(NOT_IMPLEMENTED)

    // SCHLEIM
    abstract class SchleimSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schleim, source)
    object VermehrtDuennesSputum : SchleimSymptom(NOT_IMPLEMENTED)
    object SchleimFehlend : SchleimSymptom(NOT_IMPLEMENTED)
    object SchleimWenig : SchleimSymptom(NOT_IMPLEMENTED)
    object SchleimKlebrig : SchleimSymptom(NOT_IMPLEMENTED)
    object SchleimBissiBlut : SchleimSymptom(NOT_IMPLEMENTED)
    object SchleimGelb : SchleimSymptom(NOT_IMPLEMENTED)
    object KlarerSputum : SchleimSymptom(NOT_IMPLEMENTED)
    object WaessrigerSputum : SchleimSymptom(NOT_IMPLEMENTED)
    object ZaeherSchleim : SchleimSymptom(NOT_IMPLEMENTED)

    // SCHWEISS
    abstract class SchweissSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schweiss, source)
    object LeichtesSchwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object Schwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object KeinSchwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))

    // SEHEN
    abstract class SehenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Sehen, source)

    // SPRECHEN
    abstract class SprechenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Sprechen, source)
    object WenigLeiseSprechen : SprechenSymptom(NOT_IMPLEMENTED)

    // STUHL
    abstract class StuhlSymptom(source: SymptomSource) : Symptom(SymptomCategory.Stuhl, source)

    // TEMPERATUR
    abstract class TemperaturSymptom(source: SymptomSource) : Symptom(SymptomCategory.Temperatur, source)
    object AversionKaelte : TemperaturSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.AversionCold))
    object AversionWind : TemperaturSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.AversionCold))
    object Erkaeltungen : TemperaturSymptom(NOT_IMPLEMENTED)
    object HitzeGefuehlAbends : TemperaturSymptom(NOT_IMPLEMENTED)
    object FroestelnMitEtwasFieber : TemperaturSymptom(NOT_IMPLEMENTED)
    object FroestelnStarkerAlsFieber : TemperaturSymptom(NOT_IMPLEMENTED)
    object FieberStaerkerAlsFroesteln : TemperaturSymptom(NOT_IMPLEMENTED)

    // TRINKEN
    abstract class TrinkenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Trinken, source)
    object WenigDurst : TrinkenSymptom(NOT_IMPLEMENTED)
    object KeinDurst : TrinkenSymptom(NOT_IMPLEMENTED)
    object MehrDurst : TrinkenSymptom(NOT_IMPLEMENTED)
    object MoechteKaltesTrinken : TrinkenSymptom(NOT_IMPLEMENTED)

    // URIN
    abstract class UrinSymptom(source: SymptomSource) : Symptom(SymptomCategory.Urin, source)

    // VERDAUUNG
    abstract class VerdauungSymptom(source: SymptomSource) : Symptom(SymptomCategory.Verdauung, source)

    // ZUNGE
    abstract class ZungeSymptom(source: SymptomSource) : Symptom(SymptomCategory.Zunge, source)
    object ZungeNormal : ZungeSymptom(NOT_IMPLEMENTED)
    object ZungeRoteSpitze : ZungeSymptom(NOT_IMPLEMENTED)
    object ZungeGeschwollen : ZungeSymptom(NOT_IMPLEMENTED)
    object ZungeTrocken : ZungeSymptom(NOT_IMPLEMENTED)
    // farbe
    object ZungeBlass : ZungeSymptom(NOT_IMPLEMENTED)
    object ZungeRot : ZungeSymptom(NOT_IMPLEMENTED)
    // belag
    object WenigBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object VermehrterBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object WeisserBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object GelberBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object DuennerBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object TrockenerBelag : ZungeSymptom(NOT_IMPLEMENTED)

}

