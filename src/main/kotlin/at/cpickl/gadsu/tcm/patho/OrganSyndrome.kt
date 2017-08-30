package at.cpickl.gadsu.tcm.patho

private val LuWindKaelteZungeUndPuls = setOf(
        Symptom.NormaleZunge,
        Symptom.WeisserBelag,
        Symptom.VermehrterBelag,
        Symptom.DuennerBelag,
        Symptom.OberflaechlicherPuls,
        Symptom.GespannterPuls,
        Symptom.VerlangsamterPuls
)

enum class OrganSyndrome(
        val label: String,
        val description: String = "",
        val organ: ZangOrgan,
        val part: SyndromePart? = null,
        val tendency: MangelUeberfluss,

        val externalFactors: List<ExternalPathos> = emptyList(),
        val symptoms: Set<Symptom>
) {

    // LUNGE
    // =================================================================================================================
    LuQiMangel(
            label = "Lu-Qi-Mangel",
            description = "Hat etwas von einer Art Depression aber ohne der Trauer.",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Qi,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.Kurzatmigkeit,
                    Symptom.Husten,
                    Symptom.DuennerSchleim,
                    Symptom.VermehrterSchleim,
                    Symptom.Asthma,
                    Symptom.FlacheAtmung,

                    Symptom.WenigLeiseSprechen,
                    Symptom.EnergieMangel,
                    Symptom.Muedigkeit,
                    Symptom.Blaesse,
                    Symptom.TrauererloseDepression,

                    Symptom.LeichtesSchwitzen,
                    Symptom.Erkaeltungen,
                    Symptom.AversionKaelte,

                    Symptom.NormaleZunge,
                    Symptom.BlasseZunge,
                    Symptom.GeschwolleneZunge,

                    Symptom.SchwacherPuls,
                    Symptom.WeicherPuls,
                    Symptom.LeererPuls
            )),
    LuYinMangel(
            label = "Lu-Yin-Mangel",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Yin,
            tendency = MangelUeberfluss.Mangel,
            // TODO allg-YinMangel-symptoms + allg-QiMangel-symptoms
            symptoms = LuQiMangel.symptoms.plus(setOf(
                    Symptom.Heiserkeit,
                    Symptom.TrockenerHals,
                    Symptom.TrockenerHusten,
                    Symptom.HitzeGefuehlAbends,

                    Symptom.RoteZunge,
                    Symptom.TrockeneZunge,
                    Symptom.WenigBelag,

                    Symptom.KeinSchleim,
                    Symptom.WenigSchleim,
                    Symptom.KlebrigerSchleim,
                    Symptom.BlutInSchleim,

                    Symptom.BeschleunigterPuls,
                    Symptom.DuennerPuls,
                    Symptom.SchwacherPuls
            ))),
    LuWindKaelteWind(
            label = "Wind Wind Kälte attackiert Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.KratzenderHals,
                    Symptom.Kopfschmerzen,
                    Symptom.FroestelnMitEtwasFieber,
                    Symptom.Schwitzen,
                    Symptom.AversionWind
            ).plus(LuWindKaelteZungeUndPuls)
    ),
    LuWindKaelteKaelte(
            label = "Kälte Wind Kälte attackiert Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.FroestelnStarkerAlsFieber,
                    Symptom.AversionKaelte,
                    Symptom.KeinSchwitzen,
                    Symptom.WenigDurst,
                    Symptom.KeinDurst,
                    Symptom.Muskelschmerzen,
                    Symptom.Kopfschmerzen,
                    Symptom.Husten,
                    Symptom.Schnupfen,
                    Symptom.KlarerSchleim,
                    Symptom.WaessrigerSchleim,
                    Symptom.VerstopfteNase
            ).plus(LuWindKaelteZungeUndPuls)
    ),
    LuWindHitze(
            label = "Wind Hitze attackiert Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            // TODO plus symptoms of ExoWind; evtl plus HitzeSymptoms
            symptoms = setOf(
                    Symptom.FieberStaerkerAlsFroesteln,
                    Symptom.Schwitzen,
                    Symptom.Husten,
                    Symptom.GelberSchleim,
                    Symptom.VerstopfteNase,
                    Symptom.Schnupfen,
                    Symptom.Halsschmerzen,
                    Symptom.RoterHals,
                    Symptom.RoterRachen,
                    Symptom.MehrDurst,
                    Symptom.MoechteKaltesTrinken,

                    Symptom.RoteZungenspitze,
                    Symptom.DuennerBelag,
                    Symptom.GelberBelag,

                    Symptom.OberflaechlicherPuls,
                    Symptom.BeschleunigterPuls
            )
    ),
    LuTrocken(
            label = "Trockenheit attackiert Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.TrockenerMund,
                    Symptom.TrockeneNase,
                    Symptom.TrockenerRachen,
                    Symptom.TrockenerHals,
                    Symptom.Nasenbluten,
                    Symptom.Heiserkeit,
                    Symptom.TrockenerHusten,
                    Symptom.WenigSchleim,
                    Symptom.KeinSchleim,
                    Symptom.ZaeherSchleim,
                    Symptom.BlutInSchleim,
                    Symptom.LeichteKopfschmerzen,

                    Symptom.RoteZungenspitze,
                    Symptom.GelberBelag,
                    Symptom.DuennerBelag,
                    Symptom.TrockenerBelag,

                    Symptom.OberflaechlicherPuls,
                    Symptom.BeschleunigterPuls
            )
    ),
    // @LuSchleim generell:
    //     - MP produziert schleim, Lu lagert ihn
    //     - ursachen: MP-Qi/Yang-Mangel, Lu-Qi-Mangel
    LuSchleimKalt(
            label = "Kalte Schleimretention in Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = `Allgemeine Lunge Schleim Symptome`.plus(setOf(
                    Symptom.WeisserSchleim,
                    Symptom.TrueberSchleim,
                    Symptom.ReichlichSchleim,
                    Symptom.Blaesse,
                    Symptom.Muedigkeit,
                    Symptom.KaelteGefuehl,
                    Symptom.WeisserBelag,
                    Symptom.LangsamerPuls
            ))
    ),
    LuSchleimHeiss(
            label = "Heisse Schleimretention in Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = `Allgemeine Lunge Schleim Symptome`.plus(setOf(
                    Symptom.GelberSchleim,
                    Symptom.ReichlichSchleim,
                    Symptom.Fieber,
                    Symptom.HitzeZeichen,
                    Symptom.GelberBelag,
                    Symptom.BraunerBelag,
                    Symptom.BeschleunigterPuls
            ))
    ),

    // HERZ
    // =================================================================================================================

    HeQiXu(
            label = "He-Qi-Mangel",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.LeuchtendeBlaesse,
                    Symptom.Muedigkeit,
                    Symptom.Schwaeche,
                    Symptom.Palpitationen,
                    Symptom.Kurzatmigkeit,
                    Symptom.StarkesSchwitzen, // va am tag
                    Symptom.MentaleMuedigkeit,

                    Symptom.SchwacherPuls,
                    Symptom.LeererPuls,
                    Symptom.UnregelmaessigerPuls,

                    Symptom.BlasseZunge,
                    Symptom.GeschwolleneZunge,
                    Symptom.LaengsrissInZunge
            )
    ),
    HeYangXu(
            label = "He-Yang-Mangel",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = HeQiXu.symptoms.plus(setOf(
                    Symptom.BlassesGesicht,
                    Symptom.WeissesGesicht,
                    Symptom.ThorakalesEngegefuehl,
                    Symptom.KaelteGefuehl,
                    Symptom.KalteHaende,
                    Symptom.KalteFuesse,

                    Symptom.SchwacherPuls,
                    Symptom.LangsamerPuls,
                    Symptom.UnregelmaessigerPuls,

                    Symptom.BlasseZunge,
                    Symptom.FeuchteZunge,
                    Symptom.GeschwolleneZunge
            ))
    ),
    // He Yang Erschoepfung (ausgepraegter he yang xu) => hat noch 3 unterarten...
    HeBlutXu(
            label = "He-Blut-Mangel",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf( // plus(allgemeiner blut mangel symptoms)
                    Symptom.StumpfeBlaesse,
                    Symptom.Schwindel,
                    Symptom.Aengstlichkeit,
                    Symptom.Schreckhaftigkeit,
                    Symptom.Unruhe,
                    Symptom.Schlafstoerungen,
                    Symptom.VieleTraeume,
                    Symptom.Konzentrationsstoerungen,
                    Symptom.SchlechteMerkfaehigkeit,
                    Symptom.Palpitationen,

                    Symptom.BlasseZunge,
                    Symptom.DuennerPuls
            )
    ),
    HeYinXu(
            label = "He-Yin-Mangel",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf( // plus(allgemeiner yin mangel = palpitationen, unruhe, gereiztheit)
                    Symptom.FuenfZentrenHitze,
                    Symptom.Nachtschweiss,
                    Symptom.HitzeGefuehlAbends,
                    Symptom.RoteWangenflecken,
                    Symptom.TrockenerMund,
                    Symptom.MehrDurstSpaeter,
                    Symptom.Unruhe,
                    Symptom.Gereiztheit,
                    Symptom.Schlafstoerungen,
                    Symptom.VieleTraeume,
                    Symptom.RoteZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.DuennerBelag,
                    Symptom.FehlenderBelag,
                    Symptom.BeschleunigterPuls,
                    Symptom.DuennerPuls
            )
    ),
    HeFeuer(
            label = "Loderndes Herz Feuer",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.Blaesse
            )
    ),
    HeSchleimBeunruhigt(
            label = "Schleim-Feuer beunruhigt das Herz",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.Blaesse
            )
    ),
    HeSchleimVerstopft(
            label = "Schleim verstopft die Herzöffnungen",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.Blaesse
            )
    ),
    HeBlutStau(
            label = "Herz Blut Stauung",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.Blaesse
            )
    )

    // NIERE
    // =================================================================================================================


    // LEBER
    // =================================================================================================================


    // MILZ
    // =================================================================================================================



}

private val `Allgemeine Lunge Schleim Symptome` = setOf(
        Symptom.ThorakalesEngegefuehl,
        Symptom.VoelleGefuehl,
        Symptom.Atemnot,
        Symptom.Asthma,
        Symptom.Husten,
        Symptom.ReichlichSchleim,
        Symptom.RasselndeKehle,
        Symptom.WenigAppetit,
        Symptom.Breichreiz,
        Symptom.DickerBelag,
        Symptom.FeuchterBelag,
        Symptom.SchmierigerBelag,
        Symptom.SchluepfrigerPuls,
        Symptom.OberflaechlicherPuls
)
