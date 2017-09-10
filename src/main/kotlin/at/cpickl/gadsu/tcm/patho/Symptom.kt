package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.tcm.model.IsEnumOption
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.tcm.patho.Symptom.SymptomSource.NOT_IMPLEMENTED
import at.cpickl.gadsu.treatment.dyn.treats.PulseProperty


// TODO #115 allgemeine symptoms includen andere spezifische symptoms
// MINOR #115 optiona field limited to a specific Gender
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

    //<editor-fold desc="MISC">
    // =================================================================================================================
    abstract class MiscSymptom(source: SymptomSource) : Symptom(SymptomCategory.Misc, source)

    object ThorakalesEngegefuehl : MiscSymptom(NOT_IMPLEMENTED)
    object Schwindel : MiscSymptom(NOT_IMPLEMENTED)
    object Druckgefuehl : MiscSymptom(NOT_IMPLEMENTED)
    object Zahnausfall : MiscSymptom(NOT_IMPLEMENTED)
    object GestoerteZahnentwicklung : MiscSymptom(NOT_IMPLEMENTED)
    object ProblemeZaehne : MiscSymptom(NOT_IMPLEMENTED)
    object Infektanfaelligkeit : MiscSymptom(NOT_IMPLEMENTED)
    object Desorientiertheit : MiscSymptom(NOT_IMPLEMENTED)
    object Schlaganfall : MiscSymptom(NOT_IMPLEMENTED)
    object SpannungZwerchfell : MiscSymptom(NOT_IMPLEMENTED)
    object DruckgefuehlBrustbein : MiscSymptom(NOT_IMPLEMENTED)
    object Knoten : MiscSymptom(NOT_IMPLEMENTED)
    object Tumore : MiscSymptom(NOT_IMPLEMENTED)
    object LeberVergroesserung : MiscSymptom(NOT_IMPLEMENTED)
    object Milzvergroesserung : MiscSymptom(NOT_IMPLEMENTED)
    object GestauteVenen : MiscSymptom(NOT_IMPLEMENTED)
    object Benommenheit : MiscSymptom(NOT_IMPLEMENTED)
    object Koma : MiscSymptom(NOT_IMPLEMENTED)
    object Delirium : MiscSymptom(NOT_IMPLEMENTED)
    object Schwanken : MiscSymptom(NOT_IMPLEMENTED)
    object KopfUnwillkuerlichBewegt : MiscSymptom(NOT_IMPLEMENTED)
    object Hautausschlag : MiscSymptom(NOT_IMPLEMENTED)
    object Kontraktionen : MiscSymptom(NOT_IMPLEMENTED)
    object UntenZiehendeBauchorgane : MiscSymptom(NOT_IMPLEMENTED)
    object Schweregefuehl : MiscSymptom(NOT_IMPLEMENTED)
    object OrgansenkungUnterleibsorgane : MiscSymptom(NOT_IMPLEMENTED)
    object Haemorrhoiden : MiscSymptom(NOT_IMPLEMENTED)
    object Petechien : MiscSymptom(NOT_IMPLEMENTED) // Haut- oder Schleimhautblutung in Form einer Kapillarblutung
    object Purpura : MiscSymptom(NOT_IMPLEMENTED) // multiple, kleinfleckige Kapillarblutungen in die Haut, Unterhaut (Subkutis) oder die Schleimhäute
    object BlaueFlecken : MiscSymptom(NOT_IMPLEMENTED)

    // leber/wind
    object TaubheitsgefuehlExtremitaeten : MiscSymptom(NOT_IMPLEMENTED)

    object SteifeSehnen : MiscSymptom(NOT_IMPLEMENTED)
    object Zittern : MiscSymptom(NOT_IMPLEMENTED)
    object Laehmung : MiscSymptom(NOT_IMPLEMENTED)
    object HalbseitigeLaehmung : MiscSymptom(NOT_IMPLEMENTED)
    object Kraempfe : MiscSymptom(NOT_IMPLEMENTED)
    object Tics : MiscSymptom(NOT_IMPLEMENTED)
    object PloetzlicheBewegungen : MiscSymptom(NOT_IMPLEMENTED)
    object UnkoordinierteBewegungen : MiscSymptom(NOT_IMPLEMENTED)
    object Zuckungen : MiscSymptom(NOT_IMPLEMENTED)
    object AllgemeineUnregelmaessigkeiten : MiscSymptom(NOT_IMPLEMENTED)
    object OberkoerperStaerkerBetroffen : MiscSymptom(NOT_IMPLEMENTED)
    object KopfStaerkerBetroffen : MiscSymptom(NOT_IMPLEMENTED)
    object YangLokalisationenStaerkerBetroffen : MiscSymptom(NOT_IMPLEMENTED)

    // koerperfluessigkeiten/wasser/niere
    object Aszites : MiscSymptom(NOT_IMPLEMENTED) // "Wasserbauch"

    object Oedeme : MiscSymptom(NOT_IMPLEMENTED)
    object OedemeBauch : MiscSymptom(NOT_IMPLEMENTED)
    object StarkeOedemeBeine : MiscSymptom(NOT_IMPLEMENTED)
    object LungenOedem : MiscSymptom(NOT_IMPLEMENTED)
    // niere jing
    object VerzoegerteGeistigeEntwicklung : MiscSymptom(NOT_IMPLEMENTED)

    object VerzoegerteKoerperlicheEntwicklung : MiscSymptom(NOT_IMPLEMENTED)
    object VerzoegerteKnochenEntwicklung : MiscSymptom(NOT_IMPLEMENTED)
    object VeroegertesWachstum : MiscSymptom(NOT_IMPLEMENTED)
    object VerzoegerteReifung : MiscSymptom(NOT_IMPLEMENTED)
    object VerfruehtesSenium : MiscSymptom(NOT_IMPLEMENTED)
    object ProblemeUntererRuecken : MiscSymptom(NOT_IMPLEMENTED)
    object ProblemeKnie : MiscSymptom(NOT_IMPLEMENTED)
    object ProblemeFussknoechel : MiscSymptom(NOT_IMPLEMENTED)
    object ProblemeKnochen : MiscSymptom(NOT_IMPLEMENTED)
    object ProblemeGedaechtnis : MiscSymptom(NOT_IMPLEMENTED)
    object ProblemeGehirn : MiscSymptom(NOT_IMPLEMENTED)
    object ProblemeHaare : MiscSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="ATMUNG">
    // =================================================================================================================
    abstract class AtmungSymptom(source: SymptomSource) : Symptom(SymptomCategory.Atmung, source)

    object Asthma : AtmungSymptom(NOT_IMPLEMENTED)
    object FlacheAtmung : AtmungSymptom(NOT_IMPLEMENTED)
    object Kurzatmigkeit : AtmungSymptom(NOT_IMPLEMENTED)
    object Atemnot : AtmungSymptom(NOT_IMPLEMENTED)
    object Seufzen : AtmungSymptom(NOT_IMPLEMENTED)
    object FroschImHals : AtmungSymptom(NOT_IMPLEMENTED)
    object BlutHusten : AtmungSymptom(NOT_IMPLEMENTED)
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

    //</editor-fold>
    //<editor-fold desc="EMOTION">
    // =================================================================================================================
    abstract class EmotionSymptom(source: SymptomSource) : Symptom(SymptomCategory.Emotion, source)

    object TrauererloseDepression : EmotionSymptom(NOT_IMPLEMENTED)
    object Aengstlichkeit : EmotionSymptom(NOT_IMPLEMENTED)
    object Schreckhaftigkeit : EmotionSymptom(NOT_IMPLEMENTED)
    object Unruhe : EmotionSymptom(NOT_IMPLEMENTED)
    object Gereiztheit : EmotionSymptom(NOT_IMPLEMENTED)
    object Zornesanfaelle : EmotionSymptom(NOT_IMPLEMENTED)
    object Reizbarkeit : EmotionSymptom(NOT_IMPLEMENTED)
    object Aufbrausen : EmotionSymptom(NOT_IMPLEMENTED)
    object Zornesausbrueche : EmotionSymptom(NOT_IMPLEMENTED)
    object Launisch : EmotionSymptom(NOT_IMPLEMENTED)
    object Frustration : EmotionSymptom(NOT_IMPLEMENTED)
    object UnterdrueckteGefuehle : EmotionSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="ENERGIE">
    // =================================================================================================================
    abstract class EnergieSymptom(source: SymptomSource) : Symptom(SymptomCategory.Energie, source)

    object EnergieMangel : EnergieSymptom(NOT_IMPLEMENTED)
    object Muedigkeit : EnergieSymptom(NOT_IMPLEMENTED)
    object MentaleMuedigkeit : EnergieSymptom(NOT_IMPLEMENTED)
    object Schwaeche : EnergieSymptom(NOT_IMPLEMENTED)
    object Antriebslosigkeit : EnergieSymptom(NOT_IMPLEMENTED)
    object Ausgezehrt : EnergieSymptom(NOT_IMPLEMENTED)
    object AnstrengungSchnellErschoepft : EnergieSymptom(NOT_IMPLEMENTED)
    object KraftloseMuskeln : EnergieSymptom(NOT_IMPLEMENTED)
    object Schlappheit : EnergieSymptom(NOT_IMPLEMENTED)
    object SchwererKopf : EnergieSymptom(NOT_IMPLEMENTED)
    object SchwereGliedmassen : EnergieSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="ESSEN">
    // =================================================================================================================
    abstract class EssenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Essen, source)

    object KeinAppetit : EssenSymptom(NOT_IMPLEMENTED)
    object WenigAppetit : EssenSymptom(NOT_IMPLEMENTED)
    object VielAppetit : EssenSymptom(NOT_IMPLEMENTED)
    object VerminderterGeschmackssinn : EssenSymptom(NOT_IMPLEMENTED)
    object BittererMundgeschmack : EssenSymptom(NOT_IMPLEMENTED)
    object FaderMundgeschmack : EssenSymptom(NOT_IMPLEMENTED)
    object BlanderMundgeschmack : EssenSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="Frau">
    // =================================================================================================================
    abstract class FrauSymptom(source: SymptomSource) : Symptom(SymptomCategory.Frau, source)

    object GelberAusfluss : FrauSymptom(NOT_IMPLEMENTED)
    object RiechenderAusfluss : FrauSymptom(NOT_IMPLEMENTED)
    object JuckreizScheide : FrauSymptom(NOT_IMPLEMENTED)
    object Entzuendungsherde : FrauSymptom(NOT_IMPLEMENTED)
    object Hautpilz : FrauSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="FARBE">
    // =================================================================================================================
    abstract class FarbeSymptom(source: SymptomSource) : Symptom(SymptomCategory.Farbe, source)

    object Blaesse : FarbeSymptom(NOT_IMPLEMENTED) // MINOR hat mehrere untertypen
    object StumpfeBlaesse : FarbeSymptom(NOT_IMPLEMENTED)
    object LeuchtendeBlaesse : FarbeSymptom(NOT_IMPLEMENTED)
    object BlaufaerbungNaegeln : FarbeSymptom(NOT_IMPLEMENTED)
    //</editor-fold>
    //<editor-fold desc="GESICHT">
    // =================================================================================================================
    abstract class GesichtSymptom(source: SymptomSource) : Symptom(SymptomCategory.Gesicht, source)

    object WeissesGesicht : GesichtSymptom(NOT_IMPLEMENTED)
    object BlassesGesicht : GesichtSymptom(NOT_IMPLEMENTED)
    object RotesGesicht : GesichtSymptom(NOT_IMPLEMENTED)
    object DunkleGesichtsfarbe : GesichtSymptom(NOT_IMPLEMENTED)
    object StumpfeGesichtsfarbe : GesichtSymptom(NOT_IMPLEMENTED)
    object LeuchtendWeissesGesicht : GesichtSymptom(NOT_IMPLEMENTED)
    object BlaufaerbungGesicht : GesichtSymptom(NOT_IMPLEMENTED) // wange, lippen, zunge
    object Gelbsucht : GesichtSymptom(NOT_IMPLEMENTED)


    //</editor-fold>
    //<editor-fold desc="HAUT">
    // =================================================================================================================
    abstract class HautSymptom(source: SymptomSource) : Symptom(SymptomCategory.Haut, source)

    object Erosionen : HautSymptom(NOT_IMPLEMENTED) // wie krater bei blasen beim spiegelei
    //</editor-fold>
    //<editor-fold desc="HERZ">
    // =================================================================================================================
    abstract class HerzSymptom(source: SymptomSource) : Symptom(SymptomCategory.Herz, source)

    object Palpitationen : HerzSymptom(NOT_IMPLEMENTED)
    //</editor-fold>
    //<editor-fold desc="HOEREN">
    // =================================================================================================================
    abstract class HoerenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Hoeren, source)

    object LeichterTinnitus : HoerenSymptom(NOT_IMPLEMENTED) // MINOR ===> Ni
    object StarkerTinnitus : HoerenSymptom(NOT_IMPLEMENTED) // MINOR ===> Le
    object Tinnitus : HoerenSymptom(NOT_IMPLEMENTED)
    object Hoerverlust : HoerenSymptom(NOT_IMPLEMENTED)
    object HoervermoegenVermindert : HoerenSymptom(NOT_IMPLEMENTED)
    object Hoerstoerung : HoerenSymptom(NOT_IMPLEMENTED)
    object ProblemeOhren : HoerenSymptom(NOT_IMPLEMENTED)
    object Hoersturz : HoerenSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="MANN">
    // =================================================================================================================
    abstract class MannSymptom(source: SymptomSource) : Symptom(SymptomCategory.Mann, source)

    object Hodenschmerzen : MannSymptom(NOT_IMPLEMENTED)
    object Hodenschwellungen : MannSymptom(NOT_IMPLEMENTED)
    object UntenZiehendeHoden : MannSymptom(NOT_IMPLEMENTED)
    object Prostatitis : MannSymptom(NOT_IMPLEMENTED)
    object VerfruehteEjakulation : MannSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="MENS">
    // =================================================================================================================
    abstract class MensSymptom(source: SymptomSource) : Symptom(SymptomCategory.Mens, source)

    object MenstruationBeeinflusst : MensSymptom(NOT_IMPLEMENTED)
    object Zwischenblutung : MensSymptom(NOT_IMPLEMENTED)
    object Blutsturz : MensSymptom(NOT_IMPLEMENTED) // flutartige regelblutung, "metrorrhagien"
    object Hypermenorrhoe : MensSymptom(NOT_IMPLEMENTED)
    object Schmierblutung : MensSymptom(NOT_IMPLEMENTED)
    object AussetzerMenstruation : MensSymptom(NOT_IMPLEMENTED)
    object VerlaengerterZyklus : MensSymptom(NOT_IMPLEMENTED)
    object KurzeMensZyklen : MensSymptom(NOT_IMPLEMENTED)
    object BrustspannungPMS : MensSymptom(NOT_IMPLEMENTED)
    object Zyklusunregelmaessigkeiten : MensSymptom(NOT_IMPLEMENTED)
    object PMS : MensSymptom(NOT_IMPLEMENTED)
    object Regelkraempfe : MensSymptom(NOT_IMPLEMENTED)
    object UnterbauchziehenPMS : MensSymptom(NOT_IMPLEMENTED)
    object EmotionaleSchwankungenMens : MensSymptom(NOT_IMPLEMENTED)
    object DunklesMenstruationsblut : MensSymptom(NOT_IMPLEMENTED)
    object ZaehesMenstruationsblut : MensSymptom(NOT_IMPLEMENTED)
    object KlumpenInBlut : MensSymptom(NOT_IMPLEMENTED)
    object Menstruationskraempfe : MensSymptom(NOT_IMPLEMENTED)
    object HelleBlutung : MensSymptom(NOT_IMPLEMENTED)
    object ReichlichBlutung : MensSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="PSYCHO">
    // =================================================================================================================
    abstract class PsychoSymptom(source: SymptomSource) : Symptom(SymptomCategory.Psycho, source)

    object SchlechteMerkfaehigkeit : PsychoSymptom(NOT_IMPLEMENTED)
    object Konzentrationsstoerungen : PsychoSymptom(NOT_IMPLEMENTED)
    object Gewalttaetig : PsychoSymptom(NOT_IMPLEMENTED)
    object Verwirrung : PsychoSymptom(NOT_IMPLEMENTED)
    object Weinen : PsychoSymptom(NOT_IMPLEMENTED)
    object Extrovertiertheit : PsychoSymptom(NOT_IMPLEMENTED)
    object GetruebteBewusstsein : PsychoSymptom(NOT_IMPLEMENTED)
    object Lethargie : PsychoSymptom(NOT_IMPLEMENTED)
    object Depression : PsychoSymptom(NOT_IMPLEMENTED)
    object Bewusstseinsverlust : PsychoSymptom(NOT_IMPLEMENTED)
    object GedaechtnisStoerungen : PsychoSymptom(NOT_IMPLEMENTED)
    object Nervoesitaet : PsychoSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="SCHMERZEN">
    // =================================================================================================================
    abstract class SchmerzenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schmerzen, source)

    object LeichteKopfschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Kopfschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object HeftigeKopfschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object KopfschmerzenScheitel : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Muskelschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object StechenderSchmerz : SchmerzenSymptom(NOT_IMPLEMENTED)
    object FixierterSchmerz : SchmerzenSymptom(NOT_IMPLEMENTED)
    object ArmAusstrahlendeSchmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Magenschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object SchmerzenLumbalregion : SchmerzenSymptom(NOT_IMPLEMENTED)
    object KnieSchmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object FersenSchmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object KreuzSchmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object SchmerzZwerchfell : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Schulterschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Nackenschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object SchmerzNachtsSchlimmer : SchmerzenSymptom(NOT_IMPLEMENTED)
    object SchmerzBrustkorb : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Unterbauchschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)
    object LeichteSchmerzenOberbauch : SchmerzenSymptom(NOT_IMPLEMENTED)
    object Bauchschmerzen : SchmerzenSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="SCHLAF">
    // =================================================================================================================
    abstract class SchlafSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schlaf, source)

    object Traeumen : SchlafSymptom(NOT_IMPLEMENTED)
    object VieleTraeume : SchlafSymptom(NOT_IMPLEMENTED)
    object Schlafstoerungen : SchlafSymptom(NOT_IMPLEMENTED) // MINOR hat untertypen
    object StarkeSchlafstoerungen : SchlafSymptom(NOT_IMPLEMENTED)
    object EinschlafStoerungen : SchlafSymptom(NOT_IMPLEMENTED)
    object Schlaflosigkeit : SchlafSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="SCHLEIM">
    // =================================================================================================================
    abstract class SchleimSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schleim, source)

    // farbe
    object WeisserSchleim : SchleimSymptom(NOT_IMPLEMENTED)

    object GelberSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object KlarerSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object TrueberSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    // menge
    object VermehrterSchleim : SchleimSymptom(NOT_IMPLEMENTED)

    object ReichlichSchleim : SchleimSymptom(NOT_IMPLEMENTED) // MINOR synonym?!
    object WenigSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object KeinSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    // konsistenz
    object WaessrigerSchleim : SchleimSymptom(NOT_IMPLEMENTED)

    object KlebrigerSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object ZaeherSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    object DuennerSchleim : SchleimSymptom(NOT_IMPLEMENTED)
    // misc
    object BlutInSchleim : SchleimSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="SCHWEISS">
    // =================================================================================================================
    abstract class SchweissSymptom(source: SymptomSource) : Symptom(SymptomCategory.Schweiss, source)

    object StarkesSchwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object Schwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object LeichtesSchwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object KeinSchwitzen : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    object Nachtschweiss : SchlafSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.SweatEasily))
    //</editor-fold>
    //<editor-fold desc="SEHEN">
    // =================================================================================================================
    abstract class SehenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Sehen, source)

    object TrockeneAugen : SehenSymptom(NOT_IMPLEMENTED)
    object DahinStarren : SehenSymptom(NOT_IMPLEMENTED)
    object UnscharfesSehen : SehenSymptom(NOT_IMPLEMENTED)
    object VerschwommenesSehen : SehenSymptom(NOT_IMPLEMENTED)
    object Nachtblindheit : SehenSymptom(NOT_IMPLEMENTED)
    object MouchesVolantes : SehenSymptom(NOT_IMPLEMENTED)
    object RoteAugen : SehenSymptom(NOT_IMPLEMENTED)
    object RoteBindehaut : SehenSymptom(NOT_IMPLEMENTED)
    object RoteSkleren : SehenSymptom(NOT_IMPLEMENTED)
    object Augenzucken : SehenSymptom(NOT_IMPLEMENTED)
    object Sehstoerungen : SehenSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="SEX">
    // =================================================================================================================
    abstract class SexSymptom(source: SymptomSource) : Symptom(SymptomCategory.Sex, source)

    object VerminderteLibido : SexSymptom(NOT_IMPLEMENTED)
    object VermehrteLibido : SexSymptom(NOT_IMPLEMENTED)
    object SexuelleTraeme : SexSymptom(NOT_IMPLEMENTED)
    object VorzeitigerSamenerguss : SexSymptom(NOT_IMPLEMENTED)
    object NaechtlicheEjakulation : SexSymptom(NOT_IMPLEMENTED) // "Pollutionen"
    object Unfruchtbarkeit : SexSymptom(NOT_IMPLEMENTED) // "Infertilitaet"
    object Impotenz : SexSymptom(NOT_IMPLEMENTED) // "Errektionsprobleme"
    object Spermatorrhoe : SexSymptom(NOT_IMPLEMENTED) // Gefühl Samenflüssigkeit beim Urinieren zu verlieren
    object Ausfluss : SexSymptom(NOT_IMPLEMENTED)
    object NaechtlicherSamenverlust : SexSymptom(NOT_IMPLEMENTED)
    object SexuelleTraeume : SexSymptom(NOT_IMPLEMENTED)
    object Herpes : SexSymptom(NOT_IMPLEMENTED)
    object Infertilitaet : SexSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="SPRECHEN">
    // =================================================================================================================
    abstract class SprechenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Sprechen, source)

    object WenigLeiseSprechen : SprechenSymptom(NOT_IMPLEMENTED)
    object LeiseSprechen : SprechenSymptom(NOT_IMPLEMENTED)
    object SchwacheStimme : SprechenSymptom(NOT_IMPLEMENTED)
    object VerwirrtesSprechen : SprechenSymptom(NOT_IMPLEMENTED)
    object GrundlosesLachen : SprechenSymptom(NOT_IMPLEMENTED)
    object Schreien : SprechenSymptom(NOT_IMPLEMENTED)
    object Selbstgespraeche : SprechenSymptom(NOT_IMPLEMENTED)
    object NichtReden : SprechenSymptom(NOT_IMPLEMENTED)
    object Sprachstoerungen : SprechenSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="STUHL">
    // =================================================================================================================
    abstract class StuhlSymptom(source: SymptomSource) : Symptom(SymptomCategory.Stuhl, source)

    object Durchfall : StuhlSymptom(NOT_IMPLEMENTED) // "Obstipation"
    object BreiigerStuhl : StuhlSymptom(NOT_IMPLEMENTED)
    object HahnenschreiDiarrhoe : StuhlSymptom(NOT_IMPLEMENTED) // "Morgens gleich Durchfall"
    object Verstopfung : StuhlSymptom(NOT_IMPLEMENTED)
    object BrennenderDurchfall : StuhlSymptom(NOT_IMPLEMENTED)
    object UnregelmaessigerStuhl : StuhlSymptom(NOT_IMPLEMENTED)
    object WeicherStuhl : StuhlSymptom(NOT_IMPLEMENTED)
    object WaessrigerStuhl : StuhlSymptom(NOT_IMPLEMENTED)
    object UnverdauteNahrungInStuhl : StuhlSymptom(NOT_IMPLEMENTED)
    object BlutImStuhl : StuhlSymptom(NOT_IMPLEMENTED)  // "Melaena"
    object KlebrigerStuhl : StuhlSymptom(NOT_IMPLEMENTED)
    object StinkenderStuhl : StuhlSymptom(NOT_IMPLEMENTED)
    object AnalesBrennen : StuhlSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="TEMPERATUR">
    // =================================================================================================================
    abstract class TemperaturSymptom(source: SymptomSource) : Symptom(SymptomCategory.Temperatur, source)

    object AversionKaelte : TemperaturSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.AversionCold))
    object AversionWaerme : TemperaturSymptom(SymptomSource.XPropSource(XProps.Temperature, XProps.TemperatureOpts.AversionWarm))
    object AversionWind : TemperaturSymptom(NOT_IMPLEMENTED)
    object Erkaeltungen : TemperaturSymptom(NOT_IMPLEMENTED)
    object HitzeGefuehlAbends : TemperaturSymptom(NOT_IMPLEMENTED)
    object FroestelnMitEtwasFieber : TemperaturSymptom(NOT_IMPLEMENTED)
    object FroestelnStarkerAlsFieber : TemperaturSymptom(NOT_IMPLEMENTED)
    object FieberStaerkerAlsFroesteln : TemperaturSymptom(NOT_IMPLEMENTED)
    object KaelteGefuehl : TemperaturSymptom(NOT_IMPLEMENTED)
    object WaermeErleichtert : TemperaturSymptom(NOT_IMPLEMENTED)
    object Fieber : TemperaturSymptom(NOT_IMPLEMENTED)
    // TODO #115 dieses sollte dann auf viele andere symptoms matchen
    object HitzeZeichen : TemperaturSymptom(NOT_IMPLEMENTED)

    object KalteHaende : TemperaturSymptom(NOT_IMPLEMENTED)
    object KalteFuesse : TemperaturSymptom(NOT_IMPLEMENTED)
    object KalterBauch : TemperaturSymptom(NOT_IMPLEMENTED)
    object KalteExtremitaeten : TemperaturSymptom(NOT_IMPLEMENTED) // MINOR contains hand/fuss
    object FuenfZentrenHitze : TemperaturSymptom(NOT_IMPLEMENTED)
    object RoteWangenflecken : TemperaturSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="TRINKEN">
    // =================================================================================================================
    abstract class TrinkenSymptom(source: SymptomSource) : Symptom(SymptomCategory.Trinken, source)

    object Durst : TrinkenSymptom(NOT_IMPLEMENTED)
    object WenigDurst : TrinkenSymptom(NOT_IMPLEMENTED)
    object KeinDurst : TrinkenSymptom(NOT_IMPLEMENTED)
    object MehrDurst : TrinkenSymptom(NOT_IMPLEMENTED)
    object MehrDurstSpaeter : TrinkenSymptom(NOT_IMPLEMENTED) // MINOR untertyp von MehrDurst
    object MoechteKaltesTrinken : TrinkenSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="URIN">
    // =================================================================================================================
    abstract class UrinSymptom(source: SymptomSource) : Symptom(SymptomCategory.Urin, source)

    object KlarerUrin : UrinSymptom(NOT_IMPLEMENTED)
    object TrueberUrin : UrinSymptom(NOT_IMPLEMENTED)
    object HellerUrin : UrinSymptom(NOT_IMPLEMENTED)
    object DunklerUrin : UrinSymptom(NOT_IMPLEMENTED)
    object WenigUrin : UrinSymptom(NOT_IMPLEMENTED)
    object ReichlichUrin : UrinSymptom(NOT_IMPLEMENTED)
    object KonzentrierterUrin : UrinSymptom(NOT_IMPLEMENTED)
    object HaeufigesUrinieren : UrinSymptom(NOT_IMPLEMENTED)
    object UrinierenBrennen : UrinSymptom(NOT_IMPLEMENTED)
    object BlutInUrin : UrinSymptom(NOT_IMPLEMENTED) // "Haematurie"
    object Nachttroepfeln : UrinSymptom(NOT_IMPLEMENTED)
    object Harninkontinenz : UrinSymptom(NOT_IMPLEMENTED)

    //</editor-fold>
    //<editor-fold desc="VERDAUUNG">
    // =================================================================================================================
    abstract class VerdauungSymptom(source: SymptomSource) : Symptom(SymptomCategory.Verdauung, source)

    object Uebelkeit : VerdauungSymptom(NOT_IMPLEMENTED)
    object MorgendlicheUebelkeit : VerdauungSymptom(NOT_IMPLEMENTED)
    object Brechreiz : VerdauungSymptom(NOT_IMPLEMENTED)
    object Erbrechen : VerdauungSymptom(NOT_IMPLEMENTED)
    object BlutErbrechen : VerdauungSymptom(NOT_IMPLEMENTED)
    object Sodbrennen : VerdauungSymptom(NOT_IMPLEMENTED)
    object Aufstossen : VerdauungSymptom(NOT_IMPLEMENTED)
    object Blaehungen : VerdauungSymptom(NOT_IMPLEMENTED)
    object VerdauungsProbleme : VerdauungSymptom(NOT_IMPLEMENTED)
    object WechselhafteVerdauung : VerdauungSymptom(NOT_IMPLEMENTED)
    object Breichreiz : VerdauungSymptom(NOT_IMPLEMENTED)
    object VoelleGefuehl : VerdauungSymptom(NOT_IMPLEMENTED)
    object DurckgefuehlBauch : VerdauungSymptom(NOT_IMPLEMENTED)
    object Hernien : VerdauungSymptom(NOT_IMPLEMENTED) // durchtritt eingeweide bauchwand

    //</editor-fold>
    //<editor-fold desc="ZUNGE">
    // =================================================================================================================
    abstract class ZungeSymptom(source: SymptomSource) : Symptom(SymptomCategory.Zunge, source)

    object NormaleZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object GeschwolleneZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object VergroesserteZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object TrockeneZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object FeuchteZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object Zahneindruecke : ZungeSymptom(NOT_IMPLEMENTED)
    object LaengsrissInZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object Ulzerationen : ZungeSymptom(NOT_IMPLEMENTED) // rote dippeln (nicht rote puenktchen)
    object Zungenspalt : ZungeSymptom(NOT_IMPLEMENTED)
    object SteifeZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object NasseZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object Dornen : ZungeSymptom(NOT_IMPLEMENTED) // belag waechst in mittelriss rein
    object VioletteZungenflecken : ZungeSymptom(NOT_IMPLEMENTED)
    object GestauteUnterzungenvenen : ZungeSymptom(NOT_IMPLEMENTED)
    object BlauVioletteZungenpunkte : ZungeSymptom(NOT_IMPLEMENTED)
    // farbe
    object BlasseZunge : ZungeSymptom(NOT_IMPLEMENTED)

    object RoteZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object ScharlachRoteZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object LeichtBlaeulicheZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object LivideZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object VioletteZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object DunkleZunge : ZungeSymptom(NOT_IMPLEMENTED)
    object RoteZungenspitze : ZungeSymptom(NOT_IMPLEMENTED)
    object RoterZungenrand : ZungeSymptom(NOT_IMPLEMENTED)

    // belag - farbe
    object WeisserBelag : ZungeSymptom(NOT_IMPLEMENTED)

    object GelberBelag : ZungeSymptom(NOT_IMPLEMENTED)
    object RoterBelag : ZungeSymptom(NOT_IMPLEMENTED)
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

    //</editor-fold>
    //<editor-fold desc="PULS">
    // =================================================================================================================
    abstract class PulsSymptom(source: SymptomSource) : Symptom(SymptomCategory.Puls, source) {
        constructor(pulse: PulseProperty) : this(SymptomSource.PulseSource(pulse))
    }

    object BeschleunigterPuls : PulsSymptom(NOT_IMPLEMENTED)
    object DuennerPuls : PulsSymptom(NOT_IMPLEMENTED)
    object GespannterPuls : PulsSymptom(NOT_IMPLEMENTED)
    object HaftenderPuls : PulsSymptom(NOT_IMPLEMENTED)
    object HaengenderPuls : PulsSymptom(NOT_IMPLEMENTED)
    object JagenderPuls : PulsSymptom(NOT_IMPLEMENTED)
    object KraeftigerPuls : PulsSymptom(NOT_IMPLEMENTED)
    object LangsamerPuls : PulsSymptom(NOT_IMPLEMENTED)
    object LeererPuls : PulsSymptom(NOT_IMPLEMENTED)
    object OberflaechlicherPuls : PulsSymptom(NOT_IMPLEMENTED)
    object RauherPuls : PulsSymptom(NOT_IMPLEMENTED)
    object SchluepfrigerPuls : PulsSymptom(NOT_IMPLEMENTED)
    object SchwacherPuls : PulsSymptom(NOT_IMPLEMENTED)
    object SchnellerPuls : PulsSymptom(NOT_IMPLEMENTED)
    object SaitenfoermigerPuls : PulsSymptom(NOT_IMPLEMENTED) // = gespannt
    object TieferPuls : PulsSymptom(NOT_IMPLEMENTED)
    object UnregelmaessigerPuls : PulsSymptom(NOT_IMPLEMENTED) // = "intermittierend" // MINOR leitsymptom He
    object UeberflutenderPuls : PulsSymptom(NOT_IMPLEMENTED)
    object VerlangsamterPuls : PulsSymptom(NOT_IMPLEMENTED)
    object VollerPuls : PulsSymptom(NOT_IMPLEMENTED)
    object WeicherPuls : PulsSymptom(PulseProperty.Soft)
    //</editor-fold>
}

enum class SymptomCategory {
    Misc,
    Zunge,
    Puls,

    Trinken,
    Urin,
    Essen,
    Verdauung,
    Stuhl,
    Schleim,
    Schweiss,

    Hoeren,
    Sehen,
    Sprechen,
    Atmung,

    Mens,
    Sex,

    Emotion,
    Psycho,
    Energie,
    Schmerzen,
    Temperatur,

    Farbe,
    Gesicht,
    Haut,

    Herz,
    Schlaf,

    Frau,
    Mann
    ;
}

