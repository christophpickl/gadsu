package at.cpickl.gadsu.tcm.patho

private val LuWindKaelteZungeUndPuls = setOf(
        Symptom.ZungeNormal,
        Symptom.WeisserBelag,
        Symptom.VermehrterBelag,
        Symptom.DuennerBelag,
        Symptom.PulsOberflaechlich,
        Symptom.PulsGespannt,
        Symptom.PulsVerlangsamt
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

    LuQiMangel(
            label = "Lu-Qi-Mangel",
            description = "Hat etwas von einer Art Depression aber ohne der Trauer.",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Qi,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.Kurzatmigkeit,
                    Symptom.Husten,
                    Symptom.VermehrtDuennesSputum,
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

                    Symptom.ZungeNormal,
                    Symptom.ZungeBlass,
                    Symptom.ZungeGeschwollen,

                    Symptom.PulsSchwach,
                    Symptom.PulsWeich,
                    Symptom.PulsLeer
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

                    Symptom.ZungeRot,
                    Symptom.ZungeTrocken,
                    Symptom.WenigBelag,

                    Symptom.SchleimFehlend,
                    Symptom.SchleimWenig,
                    Symptom.SchleimKlebrig,
                    Symptom.SchleimBissiBlut,

                    Symptom.PulsBeschleunigt,
                    Symptom.PulsDuenn,
                    Symptom.PulsSchwach
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
                    Symptom.KlarerSputum,
                    Symptom.WaessrigerSputum,
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
                    Symptom.SchleimGelb,
                    Symptom.VerstopfteNase,
                    Symptom.Schnupfen,
                    Symptom.Halsschmerzen,
                    Symptom.RoterHals,
                    Symptom.RoterRachen,
                    Symptom.MehrDurst,
                    Symptom.MoechteKaltesTrinken,

                    Symptom.ZungeRoteSpitze,
                    Symptom.DuennerBelag,
                    Symptom.GelberBelag,

                    Symptom.PulsOberflaechlich,
                    Symptom.PulsBeschleunigt
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
                    Symptom.SchleimWenig,
                    Symptom.SchleimFehlend,
                    Symptom.ZaeherSchleim,
                    Symptom.SchleimBissiBlut,
                    Symptom.LeichteKopfschmerzen,

                    Symptom.ZungeRoteSpitze,
                    Symptom.GelberBelag,
                    Symptom.DuennerBelag,
                    Symptom.TrockenerBelag,

                    Symptom.PulsOberflaechlich,
                    Symptom.PulsBeschleunigt
            )
    ),
    LuSchleim(
            label = "Schleimretention in Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.Heiserkeit // TODO
            )
    )
    //
    //
    //

}
