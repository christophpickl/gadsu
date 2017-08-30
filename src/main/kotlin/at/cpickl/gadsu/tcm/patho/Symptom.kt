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
    Herz,
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

    // MISC
    // =================================================================================================================
    abstract class MiscSymptom(source: SymptomSource) : Symptom(SymptomCategory.Misc, source)
    object ThorakalesEngegefuehl : MiscSymptom(NOT_IMPLEMENTED)
    object Schwindel : MiscSymptom(NOT_IMPLEMENTED)

    // ATMUNG
    // =================================================================================================================
    abstract class AtmungSymptom(source: SymptomSource) : Symptom(SymptomCategory.Atmung, source)
    object Asthma : AtmungSymptom(NOT_IMPLEMENTED)
    object FlacheAtmung : AtmungSymptom(NOT_IMPLEMENTED)
    object Kurzatmigkeit : AtmungSymptom(NOT_IMPLEMENTED)
    object Atemnot : AtmungSymptom(NOT_IMPLEMENTED)
    // trockenheit
    object Heiserkeit : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockenerHals : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockenerRachen : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockeneNase : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockenerMund : AtmungSymptom(NOT_IMPLEMENTED)
    object TrockenerHusten : AtmungSymptom(NOT_IMPLEMENTED)
    // nase
    object Schnupfen : AtmungSymptom(NOT_IMPLEMENTED)
    object VerstopfteNase : AtmungSymptom(NOT_IMPLEMENTED)
    object Nasenbluten : AtmungSymptom(NOT_IMPLEMENTED)
    // misc
    object Husten : AtmungSymptom(NOT_IMPLEMENTED)
    object Stimmbaender : AtmungSymptom(NOT_IMPLEMENTED)
    object KratzenderHals : AtmungSymptom(NOT_IMPLEMENTED)
    object RasselndeKehle : AtmungSymptom(NOT_IMPLEMENTED)
    object Halsschmerzen : AtmungSymptom(NOT_IMPLEMENTED)
    object RoterHals : AtmungSymptom(NOT_IMPLEMENTED)
    object RoterRachen : AtmungSymptom(NOT_IMPLEMENTED)

    // EMOTION
    // =================================================================================================================
    abstract class EmotionSymptom(source: SymptomSource) : Symptom(SymptomCategory.Emotion, source)
    object TrauererloseDepression : EmotionSymptom(NOT_IMPLEMENTED)
    object Aengstlichkeit : EmotionSymptom(NOT_IMPLEMENTED)
    object Schreckhaftigkeit : EmotionSymptom(NOT_IMPLEMENTED)
    object Unruhe : EmotionSymptom(NOT_IMPLEMENTED)
    object Gereiztheit : EmotionSymptom(NOT_IMPLEMENTED)

    // ENERGIE
    // =================================================================================================================
    abstract class EnergieSymptom(source: SymptomSource) : Symptom(SymptomCategory.Energie, source)
    object EnergieMangel : EnergieSymptom(NOT_IMPLEMENTED)
    object Muedigkeit : EnergieSymptom(NOT_IMPLEMENTED)
    object MentaleMuedigkeit : EnergieSymptom(NOT_IMPLEMENTED)
    object Schwaeche : EnergieSymptom(NOT_IMPLEMENTED)

    // ESSEN
    // =================================================================================================================
    abstract class EssenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Essen, source)
    object WenigAppetit : EssenSymptom(NOT_IMPLEMENTED)

    // FARBE
    // =================================================================================================================
    abstract class FarbeSymptom(source: SymptomSource) : Symptom(SymptomCategory.Farbe, source)
    object Blaesse : FarbeSymptom(NOT_IMPLEMENTED) // hat mehrere untertypen
    object StumpfeBlaesse : FarbeSymptom(NOT_IMPLEMENTED)
    object LeuchtendeBlaesse : FarbeSymptom(NOT_IMPLEMENTED)

    // GESICHT
    // =================================================================================================================
    abstract class GesichtSymptom(source: SymptomSource) : Symptom(SymptomCategory.Gesicht, source)
    object WeissesGesicht : GesichtSymptom(NOT_IMPLEMENTED)
    object BlassesGesicht : GesichtSymptom(NOT_IMPLEMENTED)

    // HERZ
    // =================================================================================================================
    abstract class HerzSymptom(source: SymptomSource) : Symptom(SymptomCategory.Herz, source)
    object Palpitationen : HerzSymptom(NOT_IMPLEMENTED)

    // HOEREN
    // =================================================================================================================
    abstract class HoerenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Hoeren, source)

    // MENS
    // =================================================================================================================
    abstract class MensSymptom(source: SymptomSource) : Symptom(SymptomCategory.Mens, source)

    // PSYCHO
    // =================================================================================================================
    abstract class PsychoSymptom(source: SymptomSource) : Symptom(SymptomCategory.Psycho, source)
    object SchlechteMerkfaehigkeit : PsychoSymptom(NOT_IMPLEMENTED)
    object Konzentrationsstoerungen : PsychoSymptom(NOT_IMPLEMENTED)

    // PULS
    // =================================================================================================================
    abstract class PulsSymptom(source: SymptomSource) : Symptom(SymptomCategory.Puls, source) {
        constructor(pulse: PulseProperty) : this(SymptomSource.PulseSource(pulse))
    }
    object LeererPuls : PulsSymptom(NOT_IMPLEMENTED)
    object WeicherPuls : PulsSymptom(PulseProperty.Soft)
    object SchwacherPuls : PulsSymptom(NOT_IMPLEMENTED)
    object DuennerPuls : PulsSymptom(NOT_IMPLEMENTED)
    object SchluepfrigerPuls : PulsSymptom(NOT_IMPLEMENTED)
    object OberflaechlicherPuls : PulsSymptom(NOT_IMPLEMENTED)
    object GespannterPuls : PulsSymptom(NOT_IMPLEMENTED)
    object VerlangsamterPuls : PulsSymptom(NOT_IMPLEMENTED)
    object LangsamerPuls : PulsSymptom(NOT_IMPLEMENTED)
    object BeschleunigterPuls : PulsSymptom(NOT_IMPLEMENTED)
    object UnregelmaessigerPuls : PulsSymptom(NOT_IMPLEMENTED) // leitsymptom He

    // SCHMERZEN
    // =================================================================================================================
    abstract class SchmerzenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schmerzen, source)
    object LeichteKopfschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Kopfschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Muskelschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)

    // SCHLAF
    // =================================================================================================================
    abstract class SchlafSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schlaf, source)
    object Traeumen : SchlafSymptom(NOT_IMPLEMENTED)
    object VieleTraeume : SchlafSymptom(NOT_IMPLEMENTED)
    object Schlafstoerungen : SchlafSymptom(NOT_IMPLEMENTED) // hat untertypen
    object EinschlafStoerungen : SchlafSymptom(NOT_IMPLEMENTED)

    // SCHLEIM
    // =================================================================================================================
    abstract class SchleimSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schleim, source)
    // farbe
    object WeisserSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object GelberSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object KlarerSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object TrueberSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    // menge
    object VermehrterSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object ReichlichSchleim : SchleimSymptom(NOT_IMPLEMENTED) // synonym?!
    object WenigSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object KeinSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    // konsistenz
    object WaessrigerSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object KlebrigerSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object ZaeherSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object DuennerSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    // misc
    object BlutInSchleim : SchleimSymptom(NOT_IMPLEMENTED)

    // SCHWEISS
    // =================================================================================================================
    abstract class SchweissSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schweiss, source)
    object StarkesSchwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object Schwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object LeichtesSchwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object KeinSchwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object Nachtschweiss : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))

    // SEHEN
    // =================================================================================================================
    abstract class SehenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Sehen, source)

    // SPRECHEN
    // =================================================================================================================
    abstract class SprechenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Sprechen, source)
    object WenigLeiseSprechen : SprechenSymptom(NOT_IMPLEMENTED)

    // STUHL
    // =================================================================================================================
    abstract class StuhlSymptom(source: SymptomSource) : Symptom(SymptomCategory.Stuhl, source)

    // TEMPERATUR
    // =================================================================================================================
    abstract class TemperaturSymptom(source: SymptomSource) : Symptom(SymptomCategory.Temperatur, source)
    object AversionKaelte : TemperaturSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.AversionCold))
    object AversionWind : TemperaturSymptom(NOT_IMPLEMENTED)
    object Erkaeltungen : TemperaturSymptom(NOT_IMPLEMENTED)
    object HitzeGefuehlAbends : TemperaturSymptom(NOT_IMPLEMENTED)
    object FroestelnMitEtwasFieber : TemperaturSymptom(NOT_IMPLEMENTED)
    object FroestelnStarkerAlsFieber : TemperaturSymptom(NOT_IMPLEMENTED)
    object FieberStaerkerAlsFroesteln : TemperaturSymptom(NOT_IMPLEMENTED)
    object KaelteGefuehl : TemperaturSymptom(NOT_IMPLEMENTED)
    object Fieber : TemperaturSymptom(NOT_IMPLEMENTED)
    // TODO #115 dieses sollte dann auf viele andere symptoms matchen
    object HitzeZeichen : TemperaturSymptom(NOT_IMPLEMENTED)
    object KalteHaende : TemperaturSymptom(NOT_IMPLEMENTED)
    object KalteFuesse : TemperaturSymptom(NOT_IMPLEMENTED)
    object FuenfZentrenHitze : TemperaturSymptom(NOT_IMPLEMENTED)
    object RoteWangenflecken : TemperaturSymptom(NOT_IMPLEMENTED)

    // TRINKEN
    // =================================================================================================================
    abstract class TrinkenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Trinken, source)
    object WenigDurst : TrinkenSymptom(NOT_IMPLEMENTED)
    object KeinDurst : TrinkenSymptom(NOT_IMPLEMENTED)
    object MehrDurst : TrinkenSymptom(NOT_IMPLEMENTED)
    object MehrDurstSpaeter : TrinkenSymptom(NOT_IMPLEMENTED) // untertyp von MehrDurst
    object MoechteKaltesTrinken : TrinkenSymptom(NOT_IMPLEMENTED)

    // URIN
    // =================================================================================================================
    abstract class UrinSymptom(source: SymptomSource) : Symptom(SymptomCategory.Urin, source)

    // VERDAUUNG
    // =================================================================================================================
    abstract class VerdauungSymptom(source: SymptomSource) : Symptom(SymptomCategory.Verdauung, source)
    object Breichreiz : VerdauungSymptom(NOT_IMPLEMENTED)
    object VoelleGefuehl : MiscSymptom(NOT_IMPLEMENTED)

    // ZUNGE
    // =================================================================================================================
    abstract class ZungeSymptom(source: SymptomSource) : Symptom(SymptomCategory.Zunge, source)
    object NormaleZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object GeschwolleneZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object TrockeneZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object FeuchteZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object LaengsrissInZunge : ZungeSymptom(NOT_IMPLEMENTED)
    // farbe
    object BlasseZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object RoteZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object RoteZungenspitze : ZungeSymptom(NOT_IMPLEMENTED)
    // belag - farbe
    object WeisserBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object GelberBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object BraunerBelag : ZungeSymptom(NOT_IMPLEMENTED)
    // belag - menge
    object FehlenderBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object WenigBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object VermehrterBelag : ZungeSymptom(NOT_IMPLEMENTED) // synonym?!
    object DickerBelag : ZungeSymptom(NOT_IMPLEMENTED)
    // belag - misc
    object DuennerBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object TrockenerBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object FeuchterBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object SchmierigerBelag : ZungeSymptom(NOT_IMPLEMENTED)

}

