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

    // IMPRESSION
    // ============================================================

    private val eager_Impression = ImpressionOpts.SkinBright

    enum class ImpressionOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        SkinBright(opt("Impression_SkinBright", "Haut hell")),
        SkinDark(opt("Impression_SkinDark", "Haut dunkel")),
        SkinHealty(opt("Impression_SkinHealty", "Haut vital")),

        VoiceQuiet(opt("Impression_VoiceQuiet", "Sprache leise")),
        VoiceLoud(opt("Impression_VoiceLoud", "Sprache laut")),
        VoiceSlow(opt("Impression_VoiceSlow", "Sprache langsam")),
        VoiceQuick(opt("Impression_VoiceQuick", "Sprache schnell")),

        MovementSlow(opt("Impression_MovementSlow", "Bewegung langsam")),
        MovementFast(opt("Impression_MovementFast", "Bewegung schnell")),

        BehaveCalm(opt("Impression_BehaveCalm", "Verhalten ruhig")),
        BehaveNervous(opt("Impression_BehaveNervous", "Verhalten nervös")),

        RedSpots(opt("Impression_RedSpots", "Rote Flecken")),
        EyesClear(opt("Impression_EyesClear", "Klare Augen"))
    }

    val Impression = enum("Impression", "Ersteindruck")

    // KONSTITUTION
    // ============================================================

    private val eager_BodyConception = BodyConceptionOpts.TissueTight

    enum class BodyConceptionOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        TissueSoft(opt("BodyConception_TissueSoft", "Gewebe weich")),
        TissueTight(opt("BodyConception_TissueTight", "Gewebe fest")),

        HeightSmall(opt("BodyConception_Small", "klein")),
        HeightTall(opt("BodyConception_Tall", "groß")),

        Slim(opt("BodyConception_Slim", "schlank")),
        Gentle(opt("BodyConception_Gentle", "zart")),
        Muscular(opt("BodyConception_Muscular", "muskulös")),
        Fat(opt("BodyConception_Fat", "massig")),

        JointsFlexible(opt("BodyConception_JointsFlexible", "Gelenke flexibel")),
        JointsStiff(opt("BodyConception_JointsStiff", "Gelenke steif"))
    }

    val BodyConception = enum("BodyConception", "Körperbild")

    // CHI STATUS
    // ============================================================

    private val eager_ChiStatus = ChiStatusOpts.Top

    enum class ChiStatusOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        Less(opt("ChiLocation_less", "wenig")),
        Much(opt("ChiLocation_much", "viel")),

        Bottom(opt("ChiLocation_Bottom", "unten")),
        Top(opt("ChiLocation_Top", "oben")),

        Weak(opt("ChiLocation_Weak", "leicht")),
        Strong(opt("ChiLocation_Strong", "schwer")),

        Inside(opt("ChiLocation_Inside", "innen")),
        Outside(opt("ChiLocation_Outside", "aussen"))
    }

    val ChiStatus = enum("ChiStatus", "Qi Status") // Energiekonzentration

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

    // TEMPERATUR
    // ============================================================

    private val eager_Temperature = TemperatureOpts.FeelWarm

    enum class TemperatureOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        FeelCold(opt("Temperature_FeelCold", "Meist kalt")),
        FeelWarm(opt("Temperature_FeelWarm", "Meist warm")),
        FeelColdHands(opt("Temperature_FeelColdHands", "kalte Hände")),
        FeelColdFeet(opt("Temperature_FeelColdFeet", "kalte Füße")),
        FeelWarmHead(opt("Temperature_FeelWarmHead", "warmer Kopf")),
        FeelWarmChest(opt("Temperature_FeelWarmChest", "warmer Brustkorb")),

        AversionCold(opt("Temperature_AversionCold", "Aversion Kälte")),
        AversionWarm(opt("Temperature_AversionWarm", "Aversion Wärme")),

        SweatRarely(opt("Temperature_SweatRarely", "Schwitze selten")),
        SweatEasily(opt("Temperature_SweatEasily", "Schwitze leicht")),
        SweatLittle(opt("Temperature_SweatLittle", "Schwitze wenig")),
        SweatMuch(opt("Temperature_SweatMuch", "Schwitze viel")),
        SweatyHands(opt("Temperature_SweatyHands", "schwitzige Hände")),
        DrySkin(opt("Temperature_DrySkin", "trockene Haut")),

        SweatDuringNight(opt("Temperature_SweatDuringNight", "Nachtschweiß")),
        SweatWhenStressed(opt("Temperature_SweatWhenStressed", "Stressschweiß"))
    }

    val Temperature = enum("Temperature", "Temperatur") // verhalten

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
        DrinkCoffee(opt("Liquid_DrinkCoffee", "Trinkt Kaffee")),
        DrinkSoftdrink(opt("Liquid_DrinkSoftdrink", "Trinkt Softdrinks")),
        DrinkTeaWith(opt("Liquid_DrinkTeaWith", "Trinkt Tee mit Honig")),
        DrinkTeaWithout(opt("Liquid_DrinkTeaWithout", "Trinkt Tee ohne Honig")),

        UrinColorBright(opt("Liquid_UrinColorBright", "Urin hell")),
        UrinColorDark(opt("Liquid_UrinColorDark", "Urin dunkel")),
        UrinatePain(opt("Liquid_UrinatePain", "Urinieren schmerzt"))
    }

    val Liquid = enum("Liquid", "Flüssiges")

    // HUNGRY
    // ============================================================

    private val eager_Hungry = HungryOpts.BigHunger

    enum class HungryOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        DietVeggy(opt("Hungry_DietVeggy", "Vegetarier")),
        DietVegan(opt("Hungry_DietVegan", "Vegan")),

        LittleHunger(opt("Hungry_LittleHunger", "Hunger klein")),
        BigHunger(opt("Hungry_BigHunger", "Hunger gro\u00df")),

        // FIXME 5 tastes here!!!, digestion stuff down

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

    // DIGESTION
    // ============================================================

    private val eager_Digestion = TasteOpts.Sweet

    enum class TasteOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        // FIXME move digestion stuff here
        Sweet(opt("Taste_Sweet", "süß")),
        Salty(opt("Taste_Salty", "salzig")),
        Sour(opt("Taste_Sour", "sauer")),
        Hot(opt("Taste_Hot", "scharf")),
        Bitter(opt("Taste_Bitter", "bitter"))
    }

    val Digestion = enum("Taste", "Geschmack")

    // MENSTRUATION
    // ============================================================

    private val eager_Menstruation = MenstruationOpts.Strong

    enum class MenstruationOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        Little(opt("Menstruation_Little", "schwach")),
        Strong(opt("Menstruation_Strong", "kräftig")),

        BloodBright(opt("Menstruation_BloodBright", "Blut hell")),
        BloodDark(opt("Menstruation_BloodDark", "Blut dunkel")),
        Chunks(opt("Menstruation_Chunks", "Klumpen")),

        PMS(opt("Menstruation_PMS", "PMS")),
        Dysmenorrhoea(opt("Menstruation_Dysmenorrhoea", "Regelbeschwerden")),

        Pill(opt("Menstruation_Pill", "hormonelle Verhütung")),
        Menopause(opt("Menstruation_Menopause", "Wechseljahre")),
        Operations(opt("Menstruation_Operations", "Operationen"))
    }

    val Menstruation = enum("Menstruation", "Zyklus")

}

interface IsEnumOption {
    val opt: XPropEnumOpt
}
