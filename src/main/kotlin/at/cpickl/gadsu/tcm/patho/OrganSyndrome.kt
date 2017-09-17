package at.cpickl.gadsu.tcm.patho


/*
TODOs:
- manche symtpoms haben Qi/Blut/Yin/Yang bezug, manche starke Zang, manche typisch fuer element
- CLI app schreiben, die auswertung printed; zb welche symptoms nur ein zang betreffen, ...
- 9er gruppe finden (auch zukuenftige beruecksichtigen)
- symptoms mit dynTreats matchen (zunge, puls)
- TCM props implementieren
 */
enum class OrganSyndrome(
        val label: String,
        // label LONG vs SHORT
        val sqlCode: String,
        val description: String = "",
        val organ: ZangOrgan,
        val part: SyndromePart? = null,
        val tendency: MangelUeberfluss,

        val externalFactors: List<ExternalPathos> = emptyList(),
        val symptoms: Set<Symptom>
) {


    // LEBER
    // =================================================================================================================

    // verdauung, emo, mens
    // sehnen, naegel, augen
    // genitalregion, rippenbogen, kopf, augen (meridianverlauf)

    LeBlutXu(
            label = "Leber Blut Mangel",
            sqlCode = "LeBlutXu",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    // allgemein
                    Symptom.Blaesse,
                    Symptom.Konzentrationsstoerungen,
                    Symptom.Schreckhaftigkeit,
                    Symptom.Palpitationen,
                    Symptom.Schlafstoerungen,
                    Symptom.TaubheitsgefuehlExtremitaeten,
                    // Le spezifisch
                    Symptom.UnscharfesSehen,
                    Symptom.VerschwommenesSehen,
                    Symptom.Nachtblindheit,
                    Symptom.MouchesVolantes,
                    Symptom.TrockeneAugen,
                    Symptom.SteifeSehnen,
                    Symptom.Zittern,
                    Symptom.AussetzerMenstruation, // Amenorrhoe
                    Symptom.VerlaengerterZyklus,

                    Symptom.BlasseZunge,
                    Symptom.DuennerPuls // fadenfoermig
            )
    ),
    LeYinXu(// wie Blutmangel, aber plus Mangel-Hitze
            label = "Leber Yin Mangel",
            sqlCode = "LeYinXu",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.TrockeneAugen,
                    Symptom.Nachtblindheit,
                    Symptom.Tinnitus,
                    Symptom.Schlafstoerungen,
                    // hitze
                    Symptom.TrockenerHals,
                    Symptom.Durst,
                    Symptom.Durchfall,
                    Symptom.Verstopfung,
                    Symptom.Unruhe,
                    Symptom.Nachtschweiss,
                    Symptom.FuenfZentrenHitze,
                    Symptom.HitzeGefuehlAbends,
                    // Le yang steigt auf
                    Symptom.Kopfschmerzen,
                    Symptom.Gereiztheit,
                    Symptom.Zornesanfaelle,
                    Symptom.Laehmung,
                    Symptom.HalbseitigeLaehmung,
                    Symptom.Schlaganfall,

                    Symptom.RoteZunge,
                    Symptom.WenigBelag,
                    Symptom.BeschleunigterPuls,
                    Symptom.DuennerPuls,
                    Symptom.SchwacherPuls
            )
    ),
    LeQiStau(
            label = "Leber Qi Stau",
            sqlCode = "xxx",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.VielAppetit,
                    Symptom.WenigAppetit,
                    Symptom.VoelleGefuehl,
                    Symptom.Blaehungen,
                    Symptom.Aufstossen,
                    Symptom.Sodbrennen,
                    Symptom.Brechreiz,
                    Symptom.Erbrechen,
                    Symptom.Magenschmerzen,
                    Symptom.Uebelkeit,
                    Symptom.MorgendlicheUebelkeit,
                    Symptom.UnregelmaessigerStuhl,
                    Symptom.WechselhafteVerdauung,

                    Symptom.SpannungZwerchfell,
                    Symptom.SchmerzZwerchfell,
                    Symptom.Seufzen,
                    Symptom.ThorakalesEngegefuehl,
                    Symptom.DruckgefuehlBrustbein,
                    Symptom.BrustspannungPMS,
                    Symptom.Schulterschmerzen,
                    Symptom.Nackenschmerzen,
                    Symptom.Kopfschmerzen,
                    Symptom.FroschImHals,

                    Symptom.Zyklusunregelmaessigkeiten,
                    Symptom.PMS,
                    Symptom.Regelkraempfe,
                    Symptom.UnterbauchziehenPMS,
                    Symptom.EmotionaleSchwankungenMens,

                    Symptom.Reizbarkeit,
                    Symptom.Aufbrausen,
                    Symptom.Zornesausbrueche,
                    Symptom.Launisch,
                    Symptom.Frustration,
                    Symptom.Depression,
                    Symptom.UnterdrueckteGefuehle,

                    Symptom.NormaleZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.RoterZungenrand,
                    Symptom.SaitenfoermigerPuls
            )
    ),
    LeBlutStau(
            label = "Leber Blut Stau",
            sqlCode = "LeBlutStau",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.StechenderSchmerz,
                    Symptom.FixierterSchmerz,
                    Symptom.SchmerzNachtsSchlimmer,
                    Symptom.DunklesMenstruationsblut,
                    Symptom.ZaehesMenstruationsblut,
                    Symptom.KlumpenInBlut,
                    Symptom.Knoten,
                    Symptom.Tumore,
                    Symptom.LeberVergroesserung,
                    Symptom.MilzVergroesserung,
                    Symptom.DunkleGesichtsfarbe,
                    Symptom.StumpfeGesichtsfarbe,
                    Symptom.GestauteVenen,

                    Symptom.VioletteZunge,
                    Symptom.DunkleZunge,
                    Symptom.BlauVioletteZungenpunkte,
                    Symptom.GestauteUnterzungenvenen,
                    Symptom.RauherPuls
            )
    ),
    LeFeuer(
            label = "Leber Feuer",
            sqlCode = "LeFeuer",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.HeftigeKopfschmerzen,
                    Symptom.KopfschmerzenScheitel,
                    Symptom.Tinnitus,
                    Symptom.Hoersturz,
                    Symptom.Schwindel,
                    Symptom.RoteAugen,
                    Symptom.RoteBindehaut,
                    Symptom.RoteSkleren,
                    Symptom.TrockenerMund,
                    Symptom.BittererMundgeschmack,
                    Symptom.Nasenbluten,
                    Symptom.BlutHusten,
                    Symptom.BlutErbrechen,

                    Symptom.Zornesausbrueche,
                    Symptom.Unruhe,
                    Symptom.Gereiztheit,
                    Symptom.Gewalttaetig,

                    Symptom.Magenschmerzen,
                    Symptom.Sodbrennen,
                    Symptom.Brechreiz,
                    Symptom.Erbrechen,
                    Symptom.Verstopfung,
                    Symptom.BrennenderDurchfall,

                    // allg. zeichen ueberfluss-hitze (He-Feuer, holz das feuer uebernaehrt)
                    Symptom.Unruhe,
                    Symptom.Schlaflosigkeit,
                    Symptom.Durst,
                    Symptom.Durchfall,
                    Symptom.WenigUrin,
                    Symptom.DunklerUrin,
                    Symptom.RotesGesicht,
                    Symptom.RoteAugen,
                    Symptom.Palpitationen,

                    Symptom.RoteZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.RoterZungenrand,
                    Symptom.GelberBelag,
                    Symptom.SaitenfoermigerPuls,
                    Symptom.BeschleunigterPuls,
                    Symptom.KraeftigerPuls
            )
    ),
    LeWindHitze(
            label = "Leber Wind (Extreme Hitze)",
            sqlCode = "LeWindHitze",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = `Symptoms of general Le Wind` + setOf(// TODO + ueberfluss-hitze
                    Symptom.Kraempfe,
                    Symptom.Koma,
                    Symptom.Delirium,

                    Symptom.RoteZunge,
                    Symptom.ScharlachRoteZunge,
                    Symptom.TrockenerBelag,
                    Symptom.GelberBelag,
                    Symptom.SteifeZunge,
                    Symptom.SchnellerPuls,
                    Symptom.VollerPuls,
                    Symptom.SaitenfoermigerPuls
            )
    ),
    LeWindYangAuf(
            label = "Leber Wind (Aufsteigendes Leber Yang)",
            sqlCode = "LeWindLeYang",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = LeYinXu.symptoms + LeBlutXu.symptoms + setOf(
                    Symptom.Schwindel,
                    Symptom.Schwanken,
                    Symptom.KopfUnwillkuerlichBewegt,
                    Symptom.Laehmung,
                    Symptom.HalbseitigeLaehmung,
                    Symptom.Desorientiertheit,
                    Symptom.Bewusstseinsverlust,
                    Symptom.Sprachstoerungen,

                    Symptom.RoteZunge,
                    Symptom.WenigBelag,
                    Symptom.FehlenderBelag,
                    Symptom.BeschleunigterPuls
            )
    ),
    LeWindBlutXu(
            label = "Leber Wind (Blut Mangel)",
            sqlCode = "LeWindBlutXu",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = LeBlutXu.symptoms + setOf(
                    Symptom.Zittern,
                    Symptom.Tics,
                    Symptom.Augenzucken,
                    Symptom.KopfUnwillkuerlichBewegt,
                    Symptom.Sehstoerungen,
                    Symptom.Schwindel,
                    Symptom.Benommenheit,

                    Symptom.BlasseZunge,
                    Symptom.DuennerBelag,
                    Symptom.WeisserBelag,
                    Symptom.DuennerPuls
            )
    ),
    LeFeuchteHitze(
            label = "Feuchtigkeit und Hitze in Le und Gb",
            sqlCode = "LeFeuchteHitze",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.SchmerzBrustkorb,
                    Symptom.SchmerzZwerchfell,
                    Symptom.WenigAppetit,
                    Symptom.Brechreiz,
                    Symptom.Erbrechen,
                    Symptom.DurckgefuehlBauch,
                    Symptom.Blaehungen,
                    Symptom.BittererMundgeschmack,
                    Symptom.Durst,
                    Symptom.Unruhe,
                    Symptom.Gereiztheit,
                    Symptom.KonzentrierterUrin,
                    Symptom.DunklerUrin,
                    Symptom.Fieber, // niedrig, anhaltend
                    Symptom.Gelbsucht,

                    // frauen
                    Symptom.GelberAusfluss,
                    Symptom.RiechenderAusfluss,
                    Symptom.JuckreizScheide,
                    Symptom.Entzuendungsherde, // bereich Le-/Gb-meridian
                    Symptom.Hautpilz,
                    Symptom.Herpes,

                    // maenner
                    Symptom.Hodenschmerzen,
                    Symptom.Hodenschwellungen,
                    Symptom.Ausfluss,
                    Symptom.Prostatitis,
                    Symptom.Hautausschlag, // bereich Le-/Gb-meridian

                    Symptom.RoteZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.RoterZungenrand,
                    Symptom.DickerBelag,
                    Symptom.GelberBelag,
                    Symptom.SchmierigerBelag,
                    Symptom.BeschleunigterPuls,
                    Symptom.SaitenfoermigerPuls,
                    Symptom.SchluepfrigerPuls
            )
    ),
    LeKaelteJingLuo(
            label = "Kälte im Lebermeridian",
            sqlCode = "LeKaltMerid",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.Unterbauchschmerzen,
                    Symptom.Kraempfe,
                    Symptom.Kontraktionen,
                    Symptom.WaermeErleichtert,
                    Symptom.Ausfluss,
                    Symptom.Unfruchtbarkeit,
                    Symptom.UntenZiehendeHoden,
                    Symptom.Impotenz,
                    Symptom.Menstruationskraempfe,

                    Symptom.BlasseZunge,
                    Symptom.WeisserBelag,
                    Symptom.FeuchterBelag,
                    Symptom.VerlangsamterPuls,
                    Symptom.TieferPuls,
                    Symptom.SaitenfoermigerPuls
            )
    ),


    // HERZ
    // =================================================================================================================

    HeQiXu(
            label = "He-Qi-Mangel",
            sqlCode = "HeQiXu",
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
            sqlCode = "HeYangXu",
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
            sqlCode = "HeBlutXu",
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
            sqlCode = "HeYinXu",
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
            sqlCode = "HeFeuer",
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
    HeSchleimFeuerBeunruhigt(// psychisches bild
            label = "Schleim-Feuer beunruhigt das Herz",
            sqlCode = "HeSchleimFeuer",
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
    HeSchleimKaelteVerstopft(
            label = "Kalter Schleim verstopft die Herzöffnungen",
            sqlCode = "HeSchleimKaelte",
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
            sqlCode = "HeBlutStau",
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

                    Symptom.VioletteZunge,
                    Symptom.VioletteZungenflecken,
                    Symptom.DuennerBelag,

                    Symptom.RauherPuls,
                    Symptom.HaengenderPuls,
                    Symptom.SchwacherPuls,
                    Symptom.UnregelmaessigerPuls,
                    Symptom.SaitenfoermigerPuls
            )
    ),


    // MILZ
    // =================================================================================================================

    MPQiXu(
            label = "Milz Qi Mangel",
            sqlCode = "MPQiXu",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.WenigAppetit,
                    Symptom.VoelleGefuehl,
                    Symptom.Blaehungen,
                    Symptom.Aufstossen,
                    Symptom.LeichteSchmerzenOberbauch,
                    Symptom.BreiigerStuhl,

                    Symptom.Muedigkeit,
                    Symptom.EnergieMangel,
                    Symptom.Ausgezehrt,
                    Symptom.AnstrengungSchnellErschoepft,
                    Symptom.KraftloseMuskeln,
                    Symptom.Oedeme,
                    Symptom.OedemeBauch,
                    Symptom.LeuchtendeBlaesse,
                    Symptom.Zwischenblutung,
                    Symptom.KurzeMensZyklen,
                    Symptom.HelleBlutung,
                    Symptom.ReichlichBlutung,

                    Symptom.BlasseZunge,
                    Symptom.GeschwolleneZunge,
                    Symptom.Zahneindruecke,
                    Symptom.DuennerBelag,
                    Symptom.WeisserBelag,
                    Symptom.SchwacherPuls,
                    Symptom.WeicherPuls,
                    Symptom.LeererPuls
            )
    ),
    MPYangXu(
            label = "Milz Yang Mangel",
            sqlCode = "MPYangXu",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Mangel,
            symptoms = MPQiXu.symptoms + setOf(
                    Symptom.AversionKaelte,
                    Symptom.KalteHaende,
                    Symptom.KalteFuesse,
                    Symptom.KalterBauch,
                    Symptom.Unterbauchschmerzen,
                    Symptom.Kraempfe, // durch waerme gelindert
                    Symptom.WeicherStuhl,
                    Symptom.WaessrigerStuhl,
                    Symptom.UnverdauteNahrungInStuhl,
                    Symptom.Blaesse,
                    Symptom.LeuchtendWeissesGesicht,
                    Symptom.Oedeme,
                    Symptom.HahnenschreiDiarrhoe,

                    Symptom.BlasseZunge,
                    Symptom.LeichtBlaeulicheZunge,
                    Symptom.GeschwolleneZunge,
                    Symptom.Zahneindruecke,
                    Symptom.NasseZunge,
                    Symptom.WeisserBelag,
                    Symptom.DuennerBelag,
                    Symptom.TieferPuls,
                    Symptom.SchwacherPuls,
                    Symptom.LangsamerPuls
            )
    ),
    MPYangXuAbsinkenQi(
            label = "Absinken des Milz Qi",
            sqlCode = "MPQiAbsinken",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Mangel,
            symptoms = MPYangXu.symptoms + setOf(
                    Symptom.UntenZiehendeBauchorgane,
                    Symptom.Schweregefuehl,
                    Symptom.Hernien,
                    Symptom.Haemorrhoiden
            )
    ),
    MPYangXuBlutUnkontrolle(
            label = "Milz kann das Blut nicht kontrollieren",
            sqlCode = "MPBlutUnkontrolle",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Mangel,
            symptoms = MPYangXu.symptoms + setOf(
                    Symptom.Petechien,
                    Symptom.Purpura,
                    Symptom.BlaueFlecken,
                    Symptom.Blutsturz,
                    Symptom.Hypermenorrhoe,
                    Symptom.Schmierblutung,
                    Symptom.BlutInUrin,
                    Symptom.BlutImStuhl
            )
    ),
    MPFeuchtKalt(
            label = "Kälte und Feuchtigkeit in Milz",
            sqlCode = "MPFeuchtKalt",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.Muedigkeit,
                    Symptom.Schlappheit,
                    Symptom.SchwererKopf,
                    Symptom.SchwereGliedmassen,
                    Symptom.VoelleGefuehl,
                    Symptom.ThorakalesEngegefuehl,
                    Symptom.DurckgefuehlBauch,
                    Symptom.KeinAppetit,
                    Symptom.Uebelkeit,
                    Symptom.Brechreiz,
                    Symptom.Erbrechen,
                    Symptom.WeicherStuhl,
                    Symptom.KlebrigerStuhl,
                    Symptom.Bauchschmerzen, // besser waerme
                    Symptom.VerminderterGeschmackssinn,
                    Symptom.FaderMundgeschmack,
                    Symptom.BlanderMundgeschmack,
                    Symptom.KaelteGefuehl,
                    Symptom.KeinDurst,
                    Symptom.WenigDurst,
                    Symptom.Ausfluss,
                    Symptom.TrueberUrin,
                    Symptom.Gelbsucht,

                    Symptom.BlasseZunge,
                    Symptom.GeschwolleneZunge,
                    Symptom.DickerBelag,
                    Symptom.WeisserBelag,
                    Symptom.SchluepfrigerPuls,
                    Symptom.VerlangsamterPuls
            )
    ),
    MPFeuchtHitze(
            label = "Hitze und Feuchtigkeit in Milz",
            sqlCode = "MPFeuchtHitze",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = setOf(
                    Symptom.ThorakalesEngegefuehl,
                    Symptom.DurckgefuehlBauch,
                    Symptom.Brechreiz,
                    Symptom.Uebelkeit,
                    Symptom.Erbrechen,
                    Symptom.Schlappheit,
                    Symptom.Schweregefuehl,
                    Symptom.WenigUrin,
                    Symptom.KonzentrierterUrin,
                    Symptom.TrueberUrin,
                    Symptom.Durst,
                    Symptom.Bauchschmerzen,
                    Symptom.AversionWaerme,
                    Symptom.WeicherStuhl,
                    Symptom.StinkenderStuhl,
                    Symptom.Durchfall,
                    Symptom.AnalesBrennen,
                    Symptom.Fieber,
                    Symptom.Gelbsucht,

                    Symptom.RoteZunge,
                    Symptom.GelberBelag,
                    Symptom.DickerBelag,
                    Symptom.SchmierigerBelag,
                    Symptom.BeschleunigterPuls,
                    Symptom.SchluepfrigerPuls
            )
    ),

    // LUNGE
    // =================================================================================================================
    LuQiMangel(
            label = "Lu-Qi-Mangel",
            sqlCode = "LuQiXu",
            description = "Hat etwas von einer Art Depression aber ohne der Trauer.",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Qi,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.Kurzatmigkeit,
                    Symptom.Husten,
                    Symptom.DuennerSchleim,
                    Symptom.VielSchleim,
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
            sqlCode = "LuYinXu",
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
            label = "Wind-Kälte attackiert Lu (mehr Wind)",
            sqlCode = "LuWind",
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
            label = "Wind-Kälte attackiert Lu (mehr Kälte)",
            sqlCode = "LuKalt",
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
            label = "Wind-Hitze attackiert Lu",
            sqlCode = "LuHitze",
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
            sqlCode = "LuTrocken",
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
            sqlCode = "LuSchleimKalt",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = `Symptoms of general LuSchleim` + setOf(
                    Symptom.WeisserSchleim,
                    Symptom.TrueberSchleim,
                    Symptom.VielSchleim,
                    Symptom.Blaesse,
                    Symptom.Muedigkeit,
                    Symptom.KaelteGefuehl,
                    Symptom.WeisserBelag,
                    Symptom.LangsamerPuls
            )
    ),
    LuSchleimHeiss(
            label = "Heisse Schleimretention in Lu",
            sqlCode = "LuSchleimHeiss",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = `Symptoms of general LuSchleim` + setOf(
                    Symptom.GelberSchleim,
                    Symptom.VielSchleim,
                    Symptom.Fieber,
                    Symptom.HitzeZeichen,
                    Symptom.GelberBelag,
                    Symptom.BraunerBelag,
                    Symptom.BeschleunigterPuls
            )
    ),

    // NIERE
    // =================================================================================================================

    NiYinXu(
            label = "Nieren Yin Mangel",
            sqlCode = "NiYinXu",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = `Symptoms of general Yin Xu` + setOf(
                    // schmerzen unterer bereich
                    Symptom.SchmerzenLumbalregion,
                    Symptom.KnieSchmerzen,
                    Symptom.FersenSchmerzen,
                    Symptom.VermehrteLibido,
                    Symptom.SexuelleTraeme,
                    Symptom.VerfruehteEjakulation,
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
            sqlCode = "NiYangXu",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = `Symptoms of general Yang Xu` + setOf(
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
            sqlCode = "NiYangXuUeber",
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
            sqlCode = "NiYangXuFest",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = setOf(
                    Symptom.Spermatorrhoe,
                    Symptom.NaechtlicheEjakulation,
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
            sqlCode = "NiYangXuFangen",
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
            sqlCode = "NiJingXuPre",
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
            sqlCode = "NiJingXuPost",
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
}

private val `Symptoms of general Le Wind` = setOf(
        Symptom.PloetzlicheBewegungen,
        Symptom.UnkoordinierteBewegungen,
        Symptom.Zuckungen,
        Symptom.Tics,
        Symptom.Augenzucken,
        Symptom.AllgemeineUnregelmaessigkeiten,
        Symptom.OberkoerperStaerkerBetroffen,
        Symptom.KopfStaerkerBetroffen,
        Symptom.YangLokalisationenStaerkerBetroffen
)

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
        Symptom.VielSchleim,
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
