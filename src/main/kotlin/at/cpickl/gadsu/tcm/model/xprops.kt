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


    // TODO OrganSyndrome' symptoms still work in progress
    enum class NOT(override val opt: XPropEnumOpt) : IsEnumOption {
        IMPLEMENTED(opt("NOT_IMPLEMENTED", "NOT_IMPLEMENTED"))
    }

    // IMPRESSION
    // ============================================================

    private val eager_Impression = ImpressionOpts.SkinBright

    enum class ImpressionOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        SkinBright(opt("Impression_SkinBright", "helle Haut")),
        SkinDark(opt("Impression_SkinDark", "dunkle Haut")),
        SkinHealty(opt("Impression_SkinHealty", "vitale Haut")),

        VoiceQuiet(opt("Impression_VoiceQuiet", "spricht leise")),
        VoiceLoud(opt("Impression_VoiceLoud", "spricht laut")),
        VoiceSlow(opt("Impression_VoiceSlow", "spricht langsam")),
        VoiceQuick(opt("Impression_VoiceQuick", "spricht schnell")),
        SpeaksLess(opt("Impression_SpeaksLess", "spricht wenig")),
        SpeaksMuch(opt("Impression_SpeaksMuch", "spricht viel")),

        MovementSlow(opt("Impression_MovementSlow", "langsame Bewegung")),
        MovementFast(opt("Impression_MovementFast", "schnelle Bewegung")),

        BehaveCalm(opt("Impression_BehaveCalm", "ruhiges Verhalten")),
        BehaveNervous(opt("Impression_BehaveNervous", "nervöses Verhalten")),

        RedSpots(opt("Impression_RedSpots", "rote Flecken")),
        EyesClear(opt("Impression_EyesClear", "klare Augen"))
    }

    val Impression = enum("Impression", "Eindruck")

    // KONSTITUTION
    // ============================================================

    private val eager_BodyConception = BodyConceptionOpts.TissueTight

    enum class BodyConceptionOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        TissueSoft(opt("BodyConception_TissueSoft", "weiches Gewebe")),
        TissueTight(opt("BodyConception_TissueTight", "festes Gewebe")),

        HeightSmall(opt("BodyConception_Small", "klein")),
        HeightTall(opt("BodyConception_Tall", "groß")),

        Slim(opt("BodyConception_Slim", "schlank")),
        Gentle(opt("BodyConception_Gentle", "zart")),
        Muscular(opt("BodyConception_Muscular", "muskulös")),
        Fat(opt("BodyConception_Fat", "massig")),

        JointsFlexible(opt("BodyConception_JointsFlexible", "flexible Gelenke")),
        JointsStiff(opt("BodyConception_JointsStiff", "steife Gelenke")),

        Shutter(opt("BodyConception_Shutter", "Zucken")) // it's not tremor... only from time to time nervous zucken
    }

    val BodyConception = enum("BodyConception", "Körper")

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

    val ChiStatus = enum("ChiLocation", "Qi Status") // Energiekonzentration

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

        Dreams(opt("Sleep_Dreams", "Träume")),
        CrunchTeeth(opt("Sleep_CrunchTeeth", "Zähne knirschen"))
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

        SweatRarely(opt("Temperature_SweatRarely", "schwitzt selten")),
        SweatEasily(opt("Temperature_SweatEasily", "schwitzt leicht")),
        SweatLittle(opt("Temperature_SweatLittle", "schwitzt wenig")),
        SweatMuch(opt("Temperature_SweatMuch", "schwitzt viel")),

        SweatyHands(opt("Temperature_SweatyHands", "schwitzige Hände")),
        DrySkin(opt("Temperature_DrySkin", "trockene Haut")),

        SweatDuringNight(opt("Temperature_SweatDuringNight", "Nachtschweiß")),
        SweatWhenStressed(opt("Temperature_SweatWhenStressed", "Stressschweiß"))
    }

    val Temperature = enum("Temperature", "Wärme") // verhalten

    // LIQUID
    // ============================================================

    private val eager_Liquid = LiquidOpts.DrinkMuch

    enum class LiquidOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        ThirstLess(opt("Liquid_ThirstLess", "kleiner Durst")),
        ThirstMuch(opt("Liquid_ThirstMuch", "grosser Durst")),

        DrinkLess(opt("Liquid_DrinkLess", "trinkt wenig")),
        DrinkMuch(opt("Liquid_DrinkMuch", "trinkt viel")),

        DrinkCold(opt("Liquid_DrinkCold", "trinkt Kaltes")),
        DrinkWarm(opt("Liquid_DrinkWarm", "trinkt Warmes")),
        DrinkCoffee(opt("Liquid_DrinkCoffee", "trinkt Kaffee")),
        DrinkSoftdrink(opt("Liquid_DrinkSoftdrink", "trinkt Softdrinks")),
        DrinkTeaWith(opt("Liquid_DrinkTeaWith", "trinkt Tee mit Honig")),
        DrinkTeaWithout(opt("Liquid_DrinkTeaWithout", "trinkt Tee ohne Honig")),

        UrinColorBright(opt("Liquid_UrinColorBright", "heller Urin")),
        UrinColorDark(opt("Liquid_UrinColorDark", "dunkler Urin")),
        UrinatePain(opt("Liquid_UrinatePain", "Urinieren schmerzt"))
    }

    val Liquid = enum("Liquid", "Flüssig")

    // HUNGRY
    // ============================================================

    private val eager_Hungry = HungryOpts.BigHunger

    enum class HungryOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        DietVeggy(opt("Hungry_DietVeggy", "Vegetarier")),
        DietVegan(opt("Hungry_DietVegan", "Vegan")),

        LittleHunger(opt("Hungry_LittleHunger", "kleiner Appetit")),
        BigHunger(opt("Hungry_BigHunger", "gro\u00dfen Appetit")),

        EatCold(opt("Hungry_EatCold", "isst Kalt")),
        EatWarm(opt("Hungry_EatWarm", "isst Warm")),

        ChewMuch(opt("Hungry_ChewMuch", "kauen")),
        ChewLess(opt("Hungry_ChewLess", "schlingen")),

        TasteSweet(opt("Hungry_TasteSweet", "mag süß")),
        TasteSalty(opt("Hungry_TasteSalty", "mag salzig")),
        TasteSour(opt("Hungry_TasteSour", "mag sauer")),
        TasteHot(opt("Hungry_TasteHot", "mag scharf")),
        TasteBitter(opt("Hungry_TasteBitter", "mag bitter")),

        TasteNotSweet(opt("Hungry_TasteNotSweet", "mag nicht süß")),
        TasteNotSalty(opt("Hungry_TasteNotSalty", "mag nicht salzig")),
        TasteNotSour(opt("Hungry_TasteNotSour", "mag nicht sauer")),
        TasteNotHot(opt("Hungry_TasteNotHot", "mag nicht scharf")),
        TasteNotBitter(opt("Hungry_TasteNotBitter", "mag nicht bitter")),
    }

    val Hungry = enum("Hungry", "Essen")

    // DIGESTION
    // ============================================================

    private val eager_Digestion = DigestionOpts.DigestionSlow

    enum class DigestionOpts(override val opt: XPropEnumOpt) : IsEnumOption {

        DigestionSlow(opt("Digestion_DigestionSlow", "träge Verdauung")),
        DigestionFast(opt("Digestion_DigestionFast", "schnelle Verdauung")),

        StoolSoft(opt("Digestion_StoolSoft", "weicher Stuhl")),
        StoolHard(opt("Digestion_StoolHard", "harter Stuhl")),

        UndigestedParts(opt("Digestion_UndigestedParts", "Unverdaute Teile")),
        Farts(opt("Digestion_Farts", "Blähungen")), // wind
        WindBelly(opt("Digestion_WindBelly", "Blähbauch")),

        Blockage(opt("Digestion_Blockage", "Verstopfungen")),
        Diarrhea(opt("Digestion_Diarrhea", "Durchfall")),
        Pyrosis(opt("Digestion_Pyrosis", "Sodbrennen")),
        Eructation(opt("Digestion_Eructation", "Aufstoßen"))

    }

    val Digestion = enum("Digestion", "Verdauen")

    // MENSTRUATION
    // ============================================================

    private val eager_Menstruation = MenstruationOpts.Strong

    enum class MenstruationOpts(override val opt: XPropEnumOpt) : IsEnumOption {
        Little(opt("Menstruation_Little", "schwach")),
        Strong(opt("Menstruation_Strong", "kräftig")),

        BloodBright(opt("Menstruation_BloodBright", "helles Blut")),
        BloodDark(opt("Menstruation_BloodDark", "dunkles Blut")),
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
