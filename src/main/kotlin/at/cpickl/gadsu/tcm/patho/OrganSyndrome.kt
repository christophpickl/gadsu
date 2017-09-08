package at.cpickl.gadsu.tcm.patho


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
            symptoms = LuQiMangel.symptoms + setOf(
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
            )),
    LuWindKaelteWind(
            label = "Wind Wind Kälte attackiert Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = `Symptoms of LuWindKaelte` + setOf(
                    Symptom.KratzenderHals,
                    Symptom.Kopfschmerzen,
                    Symptom.FroestelnMitEtwasFieber,
                    Symptom.Schwitzen,
                    Symptom.AversionWind
            )
    ),
    LuWindKaelteKaelte(
            label = "Kälte Wind Kälte attackiert Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = `Symptoms of LuWindKaelte` + setOf(
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
            )
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
            symptoms = `Symptoms of general LuSchleim` + setOf(
                    Symptom.WeisserSchleim,
                    Symptom.TrueberSchleim,
                    Symptom.ReichlichSchleim,
                    Symptom.Blaesse,
                    Symptom.Muedigkeit,
                    Symptom.KaelteGefuehl,
                    Symptom.WeisserBelag,
                    Symptom.LangsamerPuls
            )
    ),
    LuSchleimHeiss(
            label = "Heisse Schleimretention in Lu",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = `Symptoms of general LuSchleim` + setOf(
                    Symptom.GelberSchleim,
                    Symptom.ReichlichSchleim,
                    Symptom.Fieber,
                    Symptom.HitzeZeichen,
                    Symptom.GelberBelag,
                    Symptom.BraunerBelag,
                    Symptom.BeschleunigterPuls
            )
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
            symptoms = HeQiXu.symptoms + setOf(
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
            )
    ),
    // He Yang Erschoepfung (ausgepraegter he yang xu) => hat noch 3 unterarten...
    HeBlutXu(
            label = "He-Blut-Mangel",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(// plus(allgemeiner blut mangel symptoms)
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
            symptoms = setOf(// plus(allgemeiner yin mangel = palpitationen, unruhe, gereiztheit)
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
    HeFeuer(// staerkere variante von He-Yin-Xu
            label = "Loderndes Herz Feuer",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.RotesGesicht,
                    Symptom.Erosionen,
                    Symptom.Ulzerationen,
                    Symptom.MehrDurst,
                    Symptom.BittererMundgeschmack,
                    Symptom.UrinierenBrennen,
                    Symptom.BlutInUrin,
                    Symptom.Unruhe,
                    Symptom.Gereiztheit,
                    Symptom.Gewalttaetig,
                    Symptom.StarkeSchlafstoerungen,
                    Symptom.Palpitationen,

                    Symptom.RoteZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.DuennerBelag,
                    Symptom.GelberBelag,
                    Symptom.Zungenspalt,

                    Symptom.BeschleunigterPuls,
                    Symptom.KraeftigerPuls,
                    Symptom.UeberflutenderPuls,
                    Symptom.JagenderPuls
            )
    ),
    HeSchleimBeunruhigt(// psychisches bild
            label = "Schleim-Feuer beunruhigt das Herz",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.Unruhe,
                    Symptom.Gereiztheit,
                    Symptom.Verwirrung,
                    Symptom.VerwirrtesSprechen,
                    Symptom.GrundlosesLachen,
                    Symptom.Weinen,
                    Symptom.Schreien,
                    Symptom.Gewalttaetig,
                    Symptom.Extrovertiertheit,
                    Symptom.Schlafstoerungen,
                    Symptom.ThorakalesEngegefuehl,
                    Symptom.BittererMundgeschmack,
                    Symptom.Palpitationen,

                    Symptom.RoteZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.GelberBelag,
                    Symptom.SchmierigerBelag,
                    Symptom.Dornen,

                    Symptom.BeschleunigterPuls,
                    Symptom.SchluepfrigerPuls,
                    Symptom.SaitenfoermigerPuls // Le einfluss
            )
    ),
    HeSchleimVerstopft(
            label = "Kalter Schleim verstopft die Herzöffnungen",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.Verwirrung,
                    Symptom.GetruebteBewusstsein,
                    Symptom.Lethargie,
                    Symptom.Depression,
                    Symptom.Selbstgespraeche,
                    Symptom.DahinStarren,
                    Symptom.NichtReden,
                    Symptom.Bewusstseinsverlust,
                    Symptom.RasselndeKehle, // beim einatmen

                    Symptom.BlasseZunge,
                    Symptom.DickerBelag,
                    Symptom.WeisserBelag,
                    Symptom.SchmierigerBelag,

                    Symptom.VerlangsamterPuls,
                    Symptom.SchluepfrigerPuls
            )
    ),
    HeBlutStau(
            label = "Herz Blut Stauung",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.StechenderSchmerz,
                    Symptom.FixierterSchmerz,
                    Symptom.ArmAusstrahlendeSchmerzen,
                    Symptom.Magenschmerzen,
                    Symptom.ThorakalesEngegefuehl,
                    Symptom.Druckgefuehl,
                    Symptom.KalteExtremitaeten,
                    Symptom.Kurzatmigkeit,
                    Symptom.Palpitationen,
                    Symptom.BlaufaerbungGesicht,
                    Symptom.BlaufaerbungNaegeln,

                    Symptom.LivideZunge,
                    Symptom.VioletteZungenflecken,
                    Symptom.DuennerBelag,

                    Symptom.RauherPuls,
                    Symptom.HaengenderPuls,
                    Symptom.SchwacherPuls,
                    Symptom.UnregelmaessigerPuls,
                    Symptom.SaitenfoermigerPuls
            )
    ),

    // NIERE
    // =================================================================================================================

    NiYinXu(
            label = "Nieren Yin Mangel",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = `Symptoms of general Yin Xu` + setOf(
                    // schmerzen unterer bereich
                    Symptom.SchmerzenLumbalregion,
                    Symptom.KnieSchmerzen,
                    Symptom.FersenSchmerzen,
                    Symptom.VermehrteLibido,
                    Symptom.SexuelleTraeme,
                    Symptom.VorzeitigerSamenerguss,
                    Symptom.NaechtlicheEjakulation,
                    Symptom.MenstruationBeeinflusst,
                    Symptom.GedaechtnisStoerungen,
                    Symptom.Schwindel,
                    Symptom.Tinnitus,
                    Symptom.Hoerverlust,

                    Symptom.RoterBelag,
                    Symptom.DuennerBelag,
                    Symptom.FehlenderBelag,

                    Symptom.DuennerPuls,
                    Symptom.BeschleunigterPuls
            )
    ),
    NiYangXu(
            label = "Nieren Yang Mangel",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = `Symptoms of general Yang Xu` +  setOf(
                    Symptom.KreuzSchmerzen,
                    Symptom.KnieSchmerzen,
                    Symptom.FersenSchmerzen,
                    Symptom.VerminderteLibido,
                    Symptom.Unfruchtbarkeit,
                    Symptom.Impotenz,
                    Symptom.Spermatorrhoe,
                    Symptom.Ausfluss,
                    Symptom.GedaechtnisStoerungen,
                    Symptom.Zahnausfall,
                    Symptom.HoervermoegenVermindert,
                    Symptom.Tinnitus,
                    Symptom.KlarerUrin,
                    Symptom.HellerUrin,
                    Symptom.ReichlichUrin,
                    Symptom.WenigUrin,
                    Symptom.Oedeme,
                    // MP auch beeinflusst
                    Symptom.VerdauungsProbleme,
                    Symptom.BreiigerStuhl,
                    Symptom.HahnenschreiDiarrhoe,

                    Symptom.BlasseZunge,
                    Symptom.VergroesserteZunge,
                    Symptom.DuennerBelag,
                    Symptom.WeisserBelag,
                    Symptom.TieferPuls,
                    Symptom.SchwacherPuls,
                    Symptom.VerlangsamterPuls
            )
    ),
    NiYangUeberfliessenXu(
            label = "Nieren Yang Mangel mit Überfließen des Wassers",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = NiYangXu.symptoms + setOf(
                    Symptom.StarkeOedemeBeine,
                    Symptom.Aszites,
                    Symptom.LungenOedem,
                    Symptom.Husten,
                    Symptom.Atemnot,
                    Symptom.Palpitationen,

                    Symptom.BlasseZunge,
                    Symptom.GeschwolleneZunge,
                    Symptom.DuennerBelag,
                    Symptom.DickerBelag,
                    Symptom.WeisserBelag,

                    Symptom.TieferPuls,
                    Symptom.SchwacherPuls,
                    Symptom.HaftenderPuls
            )
    ),
    NiYangFestigkeitXu(
            label = "Mangelnde Festigkeit des Nieren Qi",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.Spermatorrhoe,
                    Symptom.NaechtlicherSamenverlust,
                    Symptom.SexuelleTraeume,
                    Symptom.VerfruehteEjakulation,
                    Symptom.Ausfluss,
                    Symptom.Unfruchtbarkeit,

                    Symptom.HaeufigesUrinieren,
                    Symptom.ReichlichUrin,
                    Symptom.KlarerUrin,
                    Symptom.Nachttroepfeln,
                    Symptom.Harninkontinenz,

                    Symptom.BlasseZunge,
                    Symptom.GeschwolleneZunge,
                    Symptom.TieferPuls,
                    Symptom.SchwacherPuls
            )
    ),
    NiYangEinfangenXu(
            label = "Unfähigkeit der Nieren das Qi einzufangen",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.Atemnot,
                    Symptom.Kurzatmigkeit,
                    Symptom.Asthma,
                    Symptom.Husten,
                    Symptom.LeiseSprechen,
                    Symptom.SchwacheStimme,
                    Symptom.Infektanfaelligkeit,
                    Symptom.ReichlichUrin,
                    Symptom.HellerUrin,
                    Symptom.KreuzSchmerzen,

                    Symptom.BlasseZunge,
                    Symptom.GeschwolleneZunge,
                    Symptom.DuennerBelag,
                    Symptom.WeisserBelag,
                    Symptom.TieferPuls,
                    Symptom.DuennerPuls,
                    Symptom.SchwacherPuls,
                    Symptom.OberflaechlicherPuls,
                    Symptom.LeererPuls
            )
    ),
    NiJingPraenatalXu(
            label = "Nieren Jing Mangel (Pränatal)",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.VerzoegerteGeistigeEntwicklung,
                    Symptom.VerzoegerteKoerperlicheEntwicklung,
                    Symptom.VerzoegerteKnochenEntwicklung,
                    Symptom.VeroegertesWachstum,
                    Symptom.VerzoegerteReifung,
                    Symptom.GestoerteZahnentwicklung,
                    Symptom.Hoerstoerung
            )
    ),
    NiJingPostnatalXu(
            label = "Nieren Jing Mangel (Postnatal)",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.VerfruehtesSenium,
                    Symptom.ProblemeUntererRuecken,
                    Symptom.ProblemeKnie,
                    Symptom.ProblemeFussknoechel,
                    Symptom.ProblemeKnochen,
                    Symptom.ProblemeZaehne,
                    Symptom.ProblemeGedaechtnis,
                    Symptom.ProblemeGehirn,
                    Symptom.ProblemeOhren,
                    Symptom.ProblemeHaare
            )
    )

    // LEBER
    // =================================================================================================================

//    XXX(
//            label = "XXX",
//            organ = ZangOrgan.XXX,
//            tendency = MangelUeberfluss.XXX,
//            symptoms = setOf(
//                    Symptom.,
//                    Symptom.
//            )
//    ),

    // MILZ
    // =================================================================================================================

//    XXX(
//            label = "XXX",
//            organ = ZangOrgan.XXX,
//            tendency = MangelUeberfluss.XXX,
//            symptoms = setOf(
//                    Symptom.,
//                    Symptom.
//            )
//    ),

}

private val `Symptoms of general Yin Xu` = setOf(
        Symptom.RoteWangenflecken,
        Symptom.MehrDurst,
        Symptom.FuenfZentrenHitze,
        Symptom.Nachtschweiss,
        Symptom.Durchfall,
        Symptom.KonzentrierterUrin,
        Symptom.DunklerUrin,
        Symptom.HitzeGefuehlAbends,
        Symptom.TrockenerMund,
        Symptom.TrockenerHals,
        Symptom.Halsschmerzen, // haeufig, leichte
        Symptom.Schlafstoerungen,
        Symptom.Unruhe,
        Symptom.Nervoesitaet
)

private val `Symptoms of general Yang Xu` = setOf(
        Symptom.BlassesGesicht,
        Symptom.LeuchtendWeissesGesicht,
        Symptom.AversionKaelte,
        Symptom.KalteHaende,
        Symptom.KalteFuesse,
        Symptom.Lethargie,
        Symptom.Schwaeche,
        Symptom.Antriebslosigkeit
)
private val `Symptoms of general LuSchleim` = setOf(
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

private val `Symptoms of LuWindKaelte` = setOf(
        Symptom.NormaleZunge,
        Symptom.WeisserBelag,
        Symptom.VermehrterBelag,
        Symptom.DuennerBelag,

        Symptom.OberflaechlicherPuls,
        Symptom.GespannterPuls,
        Symptom.VerlangsamterPuls
)
