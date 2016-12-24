package at.cpickl.gadsu.tcm.model

import at.cpickl.gadsu.client.xprops.model.XProp
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.client.xprops.model.XPropsFactory
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

@Suppress("UNUSED")
object XProps : XPropsFinder by XPropsFactory {

    // SLEEP
    // ============================================================

    private val eager_Sleep = SleepOpts.TiredInMorning

    enum class SleepOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        NeedLess(opt("Sleep_NeedLess", "wenig Schlaf")),
        NeedMuch(opt("Sleep_NeedMuch", "viel Schlaf")),
        ProblemsFallAsleep(opt("Sleep_ProblemsFallAsleep", "Einschlafprobleme")),
        ProblemsSleepThrough(opt("Sleep_ProblemsSleepThrough", "Durchschlafprobleme")),
        TiredInMorning(opt("Sleep_TiredInMorning", "Morgensmüdigkeit")),
        TiredDuringDay(opt("Sleep_TiredDuringDay", "Tagesmüdigkeit")),
        Dreams(opt("Sleep_Dreams", "Träume"))
    }

    val Sleep = enum("Sleep", "Schlaf")

    // HUNGRY
    // ============================================================

    private val eager_Hungry = HungryOpts.BigHunger

    enum class HungryOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        BigHunger(opt("Hungry_BigHunger", "Hunger gro\u00df")),
        LittleHunger(opt("Hungry_LittleHunger", "Hunger klein")),
        DigestionSlow(opt("Hungry_DigestionSlow", "Verdauung träge")),
        DigestionFast(opt("Hungry_DigestionFast", "Verdauung schnell")),
        Blockage(opt("Hungry_Blockage", "Verstopfungen")),
        Diarrhea(opt("Hungry_Diarrhea", "Durchfall")),
        UndigestedParts(opt("Hungry_UndigestedParts", "Unverdaute Teile")),
        StoolHard(opt("Hungry_StoolHard", "Stuhl geformt, hart")),
        StoolSoft(opt("Hungry_StoolSoft", "Stuhl ungeformt, weich")),
        WindBelly(opt("Hungry_WindBelly", "Blähbauch")),
        Farts(opt("Hungry_Farts", "Blähungen")) // wind
    }

    val Hungry = enum("Hungry", "Essen") // Verdauung

    // TASTE
    // ============================================================

    private val eager_Taste = TasteOpts.Sweet

    enum class TasteOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        Sweet(opt("Taste_Sweet", "süß")),
        Salty(opt("Taste_Salty", "salzig")),
        Sour(opt("Taste_Sour", "sauer")),
        Hot(opt("Taste_Hot", "scharf")),
        Bitter(opt("Taste_Bitter", "bitter"))
    }

    val Taste = enum("Taste", "Geschmack")

    // TEMPERATUR
    // ============================================================

    private val eager_Temperature = TemperatureOpts.FeelWarm

    enum class TemperatureOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        FeelWarm(opt("Temperature_FeelWarm", "Meist warm")),
        FeelCold(opt("Temperature_FeelCold", "Meist kalt")), // got additional textfield describing where if cold: "TemperatureColdLocation"
        SweatEasily(opt("Temperature_SweatEasily", "Schwitze leicht")),
        SweatRarely(opt("Temperature_SweatRarely", "Schwitze selten")),
        SweatDuringNight(opt("Temperature_SweatDuringNight", "Nachtschweiß")),
        SweatWhenStressed(opt("Temperature_SweatWhenStressed", "Stressschweiß"))
    }

    val Temperature = enum("Temperature", "Temperatur") // verhalten

    // MENSTRUATION
    // ============================================================

    private val eager_Menstruation = MenstruationOpts.Strong

    enum class MenstruationOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        // see text field: MenstruationTimes
        Little(opt("Menstruation_Little", "schwach")),
        Strong(opt("Menstruation_Strong", "kräftig")),
        BloodDark(opt("Menstruation_BloodDark", "Blut dunkel")),
        BloodBright(opt("Menstruation_BloodBright", "Blut hell, dünn")),
        Chunks(opt("Menstruation_Chunks", "Klumpen")),
        PMS(opt("Menstruation_PMS", "PMS")),
        Dysmenorrhoea(opt("Menstruation_Dysmenorrhoea", "Regelbeschwerden")),
        Pill(opt("Menstruation_Pill", "Pille, hormonell")),
        Menopause(opt("Menstruation_Menopause", "Wechseljahre")),
        Operations(opt("Menstruation_Operations", "Operationen"))
    }

    val Menstruation = enum("Menstruation", "Zyklus")

    // IMPRESSION
    // ============================================================

    private val eager_Impression = ImpressionOpts.SkinBright

    enum class ImpressionOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        SkinBright(opt("Impression_SkinBright", "Haut hell")),
        SkinDark(opt("Impression_SkinDark", "Haut dunkel")),
        SkinHealty(opt("Impression_SkinHealty", "Haut gesund")),
        VoiceQuiet(opt("Impression_VoiceQuiet", "Sprache leise")),
        VoiceLoud(opt("Impression_VoiceLoud", "Sprache laut")),
        VoiceSlow(opt("Impression_VoiceSlow", "Sprache langsam")),
        VoiceQuick(opt("Impression_VoiceQuick", "Sprache schnell")),
        MovementSlow(opt("Impression_MovementSlow", "Bewegung langsam")),
        MovementFast(opt("Impression_MovementFast", "Bewegung schnell")),
        RedSpots(opt("Impression_RedSpots", "Rote Flecken")),
        BehaveCalm(opt("Impression_BehaveCalm", "Verhalten ruhig")),
        BehaveNervous(opt("Impression_BehaveNervous", "Verhalten nervös")),
        EyesClear(opt("Impression_EyesClear", "Klare Augen"))
    }

    val Impression = enum("Impression", "Ersteindruck")

    // LIQUID
    // ============================================================

    private val eager_Liquid = LiquidOpts.DrinkMuch

    enum class LiquidOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        ThirstLess(opt("Liquid_ThirstLess", "Durst klein")),
        ThirstMuch(opt("Liquid_ThirstMuch", "Durst gross")),
        DrinkLess(opt("Liquid_DrinkLess", "Menge wenig")),
        DrinkMuch(opt("Liquid_DrinkMuch", "Menge viel")),
        DrinkCold(opt("Liquid_DrinkCold", "Trinkt kaltes")),
        DrinkWarm(opt("Liquid_DrinkWarm", "Trinkt warmes")),
        DrinkSoftdrink(opt("Liquid_DrinkSoftdrink", "Trinkt Softdrinks")),
        UrinColorBright(opt("Liquid_UrinColorBright", "Urin hell")),
        UrinColorDark(opt("Liquid_UrinColorDark", "Urin dunkel")),
        UrinatePain(opt("Liquid_UrinatePain", "Urinieren schmerzt"))
    }

    val Liquid = enum("Liquid", "Flüssiges")

    // CHI LOCATION
    // ============================================================

    private val eager_ChiLocation = ChiLocationOpts.Top

    enum class ChiLocationOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        Less(opt("ChiLocation_less", "wenig")),
        Much(opt("ChiLocation_much", "viel")),
        Top(opt("ChiLocation_Top", "oben")),
        Bottom(opt("ChiLocation_Bottom", "unten")),
        Weak(opt("ChiLocation_Weak", "leicht")),
        Strong(opt("ChiLocation_Strong", "schwer")),
        Outside(opt("ChiLocation_Outside", "aussen")),
        Inside(opt("ChiLocation_Inside", "innen"))
    }

    val ChiLocation = enum("ChiLocation", "Qi Status") // Energiekonzentration

    // KONSTITUTION
    // ============================================================

    private val eager_BodyConception = BodyConceptionOpts.TissueTight

    enum class BodyConceptionOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        TissueTight(opt("BodyConception_TissueTight", "Gewebe fest")),
        TissueSoft(opt("BodyConception_TissueSoft", "Gewebe weich")),
        Muscular(opt("BodyConception_Muscular", "muskulös")),
        Gentle(opt("BodyConception_Gentle", "zart")),
        Fat(opt("BodyConception_Fat", "massig")),
        Slim(opt("BodyConception_Slim", "schlank")),
        JointsStiff(opt("BodyConception_JointsStiff", "Gelenke steif")),
        JointsFlexible(opt("BodyConception_JointsFlexible", "Gelenke flexibel"))
    }

    val BodyConception = enum("BodyConception", "Körperbild")

}

interface IsEnumOption {
    val opt: XPropEnumOpt
}
