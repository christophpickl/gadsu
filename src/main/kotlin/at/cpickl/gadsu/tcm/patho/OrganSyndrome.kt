package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.tcm.model.Substances
import at.cpickl.gadsu.tcm.model.YinYang

// FIXME unbedingt die zunge+puls in eigenen datacontainer, damit diese nicht included werden koennen!
/*
TODOs:
- manche symtpoms haben Qi/Blut/Yin/Yang bezug, manche starke Zang, manche typisch fuer element
- CLI app schreiben, die auswertung printed; zb welche symptoms nur ein zang betreffen, ...
- 9er gruppe finden (auch zukuenftige beruecksichtigen)
- symptoms mit dynTreats matchen (zunge, puls)
- TCM props implementieren
 */
enum class OrganSyndrome(
        // MINOR label LONG vs SHORT
        val label: String,
        val sqlCode: String,
        val description: String = "",
        val organ: ZangOrgan,
        val part: SyndromePart? = null,
        val tendency: MangelUeberfluss,

        val externalFactors: List<ExternalPathos> = emptyList(),
        val symptoms: List<Symptom>
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
            symptoms = GeneralSymptoms.BlutXu.symptoms + listOf(
                    // augen
                    Symptom.UnscharfesSehen,
                    Symptom.VerschwommenesSehen,
                    Symptom.Nachtblindheit,
                    Symptom.MouchesVolantes,
                    Symptom.TrockeneAugen,
                    // sehnen
                    Symptom.SteifeSehnen,
                    Symptom.Zittern,
                    // mens
                    Symptom.AussetzerMenstruation,
                    Symptom.VerlaengerterZyklus,
                    // zunge
                    Symptom.BlasseZunge,
                    // puls
                    Symptom.DuennerPuls // fadenfoermig
            )
    ),
    LeYinXu(// wie Blutmangel, aber plus Mangel-Hitze
            label = "Leber Yin Mangel",
            sqlCode = "LeYinXu",
            description = "Symptome ähnlich von Leber Feuer, da das Yin das Yang nicht halten kann und aufsteigt, aber da es ein Mangel ist schwächer ausgeprägt.",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Mangel,
            symptoms = GeneralSymptoms.YinXu.symptoms + listOf(
                    // augen
                    Symptom.TrockeneAugen,
                    Symptom.Nachtblindheit,
                    // ohren
                    Symptom.Tinnitus,
                    // Le yang steigt auf
                    Symptom.Kopfschmerzen,
                    Symptom.Gereiztheit,
                    Symptom.Zornesanfaelle,
                    Symptom.Laehmung,
                    Symptom.HalbseitigeLaehmung,
                    Symptom.Schlaganfall,
                    // zunge
                    Symptom.RoteZunge, // TODO should not contain BlasseZunge from LeBlutXu! maybe outsource tongue+pulse symptoms in own variable...
                    Symptom.WenigBelag,
                    // puls
                    Symptom.BeschleunigterPuls,
                    Symptom.DuennerPuls,
                    Symptom.SchwacherPuls
            )
    ),
    LeBlutStau(
            label = "Leber Blut Stau",
            sqlCode = "LeBlutStau",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
                    // misc
                    Symptom.Knoten,
                    Symptom.Tumore,
                    Symptom.LeberVergroesserung,
                    Symptom.MilzVergroesserung,
                    Symptom.DunkleGesichtsfarbe,
                    Symptom.StumpfeGesichtsfarbe,
                    // schmerz
                    Symptom.StechenderSchmerz,
                    Symptom.FixierterSchmerz,
                    Symptom.SchmerzNachtsSchlimmer,
                    // mens
                    Symptom.DunklesMenstruationsblut,
                    Symptom.ZaehesMenstruationsblut,
                    Symptom.KlumpenInBlut,
                    // zunge
                    Symptom.VioletteZunge,
                    Symptom.DunkleZunge,
                    Symptom.BlauVioletteZungenpunkte,
                    Symptom.GestauteVenen, // MINOR gestaute == unterzungenvenen??
                    Symptom.GestauteUnterzungenvenen,
                    // puls
                    Symptom.RauherPuls
            )
    ),
    LeQiStau(
            label = "Leber Qi Stau",
            description = "Greift MP an.",
            sqlCode = "LeQiStau",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
                    // verdauung
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
                    // meridian
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
                    // mens
                    Symptom.Zyklusunregelmaessigkeiten,
                    Symptom.PMS,
                    Symptom.Regelkraempfe,
                    Symptom.UnterbauchziehenPMS,
                    Symptom.EmotionaleSchwankungenMens,
                    // emotionen
                    Symptom.Reizbarkeit,
                    Symptom.Aufbrausen,
                    Symptom.Zornesausbrueche,
                    Symptom.Launisch,
                    Symptom.Frustration,
                    Symptom.Depression,
                    Symptom.UnterdrueckteGefuehle,
                    // zunge
                    Symptom.NormaleZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.RoterZungenrand,
                    // puls
                    Symptom.SaitenfoermigerPuls
            )
    ),
    LeFeuer(
            label = "Leber Feuer (lodert nach oben)",
            sqlCode = "LeFeuer",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
                    Symptom.HeftigeKopfschmerzen,
                    Symptom.KopfschmerzenScheitel,
                    Symptom.Tinnitus,
                    Symptom.Hoersturz,
                    Symptom.Schwindel,
                    Symptom.RoteBindehaut,
                    Symptom.RoteSkleren,
                    Symptom.TrockenerMund,
                    Symptom.BittererMundgeschmack,
                    Symptom.Nasenbluten,
                    Symptom.BlutHusten,
                    Symptom.BlutErbrechen,
                    // psycho
                    Symptom.Zornesausbrueche,
                    Symptom.Gereiztheit,
                    Symptom.Gewalttaetig,
                    // verdauung
                    Symptom.Magenschmerzen,
                    Symptom.Sodbrennen,
                    Symptom.Brechreiz,
                    Symptom.Erbrechen,
                    Symptom.Verstopfung,
                    Symptom.BrennenderDurchfall,

                    // allg. zeichen ueberfluss-hitze (He-Feuer, holz das feuer uebernaehrt); aehnlich dem LeBlutXu
                    Symptom.Unruhe,
                    Symptom.Schlaflosigkeit,
                    Symptom.Durst,
                    Symptom.Durchfall,
                    Symptom.WenigUrin,
                    Symptom.DunklerUrin,
                    Symptom.RotesGesicht,
                    Symptom.RoteAugen,
                    Symptom.Palpitationen,
                    // zunge
                    Symptom.RoteZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.RoterZungenrand,
                    Symptom.GelberBelag,
                    // puls
                    Symptom.SaitenfoermigerPuls,
                    Symptom.BeschleunigterPuls,
                    Symptom.KraeftigerPuls
            )
    ),
    LeWindHitze(
            label = "Leber Wind (Extreme Hitze)",
            sqlCode = "LeWindHitze",
            description = "Von sehr hohem Fieber. Fieberkrämpfe, Verkrampfungen, Augen nach oben rollen.",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = GeneralSymptoms.LeWind + listOf(// MINOR + ueberfluss-hitze
                    Symptom.Kraempfe,
                    Symptom.Koma,
                    Symptom.Delirium,
                    // zunge
                    Symptom.RoteZunge,
                    Symptom.ScharlachRoteZunge,
                    Symptom.TrockenerBelag,
                    Symptom.GelberBelag,
                    Symptom.SteifeZunge,
                    // puls
                    Symptom.SchnellerPuls,
                    Symptom.VollerPuls,
                    Symptom.SaitenfoermigerPuls
            )
    ),
    LeWindYangAuf(
            label = "Leber Wind (Aufsteigendes Leber Yang)",
            description = "Ursache ist Le/Ni Yin-Mangel",
            sqlCode = "LeWindLeYang",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
                    Symptom.Schwindel,
                    Symptom.Schwanken,
                    Symptom.KopfUnwillkuerlichBewegt,
                    Symptom.Laehmung,
                    Symptom.HalbseitigeLaehmung,
                    // psycho
                    Symptom.Desorientiertheit,
                    Symptom.Bewusstseinsverlust,
                    Symptom.Sprachstoerungen,
                    // zunge
                    Symptom.RoteZunge,
                    Symptom.WenigBelag,
                    Symptom.FehlenderBelag,
                    // puls
                    Symptom.BeschleunigterPuls
            )
    ),
    LeWindBlutXu(
            label = "Leber Wind (Blut Mangel)",
            description = "Ursache ist Le/allgemeiner Blut-Mangel",
            sqlCode = "LeWindBlutXu",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = LeBlutXu.symptoms + listOf(
                    Symptom.Tics,
                    Symptom.Augenzucken,
                    Symptom.KopfUnwillkuerlichBewegt,
                    Symptom.Sehstoerungen,
                    Symptom.Schwindel,
                    Symptom.Benommenheit,

                    Symptom.DuennerBelag,
                    Symptom.WeisserBelag
            )
    ),
    LeFeuchteHitze(
            label = "Feuchtigkeit und Hitze in Le und Gb",
            description = "Ursache ist Le-Qi-Stau.",
            sqlCode = "LeFeuchteHitze",
            organ = ZangOrgan.Liver,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
                    Symptom.SchmerzBrustkorb,
                    Symptom.SchmerzZwerchfell,
                    Symptom.Durst,
                    Symptom.Unruhe,
                    Symptom.Gereiztheit,
                    Symptom.KonzentrierterUrin,
                    Symptom.DunklerUrin,
                    Symptom.Fieber, // niedrig, anhaltend
                    Symptom.Gelbsucht,
                    // milz
                    Symptom.WenigAppetit,
                    Symptom.Brechreiz,
                    Symptom.Erbrechen,
                    Symptom.DurckgefuehlBauch,
                    Symptom.Blaehungen,
                    Symptom.BittererMundgeschmack,
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
                    // zunge
                    Symptom.RoteZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.RoterZungenrand,
                    Symptom.DickerBelag,
                    Symptom.GelberBelag,
                    Symptom.SchmierigerBelag,
                    // puls
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
            symptoms = listOf(
                    Symptom.Unterbauchschmerzen,
                    Symptom.Kraempfe,
                    Symptom.Kontraktionen,
                    Symptom.WaermeErleichtert,
                    Symptom.Unfruchtbarkeit,
                    // frau
                    Symptom.Menstruationskraempfe,
                    Symptom.Ausfluss,
                    // mann
                    Symptom.Impotenz,
                    Symptom.UntenZiehendeHoden,
                    // zunge
                    Symptom.BlasseZunge,
                    Symptom.WeisserBelag,
                    Symptom.FeuchterBelag,
                    // puls
                    Symptom.VerlangsamterPuls,
                    Symptom.TieferPuls,
                    Symptom.SaitenfoermigerPuls
            )
    ),


    // HERZ
    // =================================================================================================================

    HeBlutXu(
            label = "He-Blut-Mangel",
            sqlCode = "HeBlutXu",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = GeneralSymptoms.BlutXu.symptoms + SpecificSymptoms.HeBlutXu
    ),
    HeYinXu(
            label = "He-Yin-Mangel",
            sqlCode = "HeYinXu",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = SpecificSymptoms.HeBlutXu + GeneralSymptoms.YinXu.symptoms + listOf(
                    Symptom.MehrDurstSpaeter,
                    Symptom.Gereiztheit,

                    Symptom.RoteZunge,
                    Symptom.RoteZungenspitze,
                    Symptom.DuennerBelag,
                    Symptom.FehlenderBelag,

                    Symptom.BeschleunigterPuls
            )
    ),
    HeQiXu(
            label = "He-Qi-Mangel",
            sqlCode = "HeQiXu",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = GeneralSymptoms.QiXu.symptoms + SpecificSymptoms.HeQiXu
    ),
    HeYangXu(
            label = "He-Yang-Mangel",
            sqlCode = "HeYangXu",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = SpecificSymptoms.HeQiXu + GeneralSymptoms.YangXu.symptoms + listOf(
                    Symptom.BlassesGesicht,
                    Symptom.WeissesGesicht,
                    Symptom.ThorakalesEngegefuehl,
                    // zunge
                    Symptom.FeuchteZunge,
                    // puls
                    Symptom.LangsamerPuls
            )
    ),
    // MINOR 3 yang xu subtypes got symptoms of HeYangXu as well??
    HeYangXuBlutBewegen(
            label = "He-Yang-Mangel (Yang kann das Blut nicht ausreichend bewegen)",
            sqlCode = "HeYangXuBlutBewegen",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = GeneralSymptoms.HeYangXuPulsTongue + listOf(
                    Symptom.Zyanose,
                    Symptom.ThorakalesEngegefuehl,
                    Symptom.MaessigeSchmerzen,
                    Symptom.Atemnot // belastungs- / ruhedyspnoe
            )
    ),
    HeYangXuUndNiYangErschopeft(
            label = "He-Yang-Mangel (He/Ni Yang erschöpft)",
            sqlCode = "HeYangXuUndNiYangErschopeft",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = GeneralSymptoms.HeYangXuPulsTongue + listOf(
                    Symptom.UrinierenSchwierigkeiten,
                    Symptom.Oedeme, // oft oben
                    Symptom.Husten,
                    Symptom.Wasserretention // ansammlung (in der lunge)
            )
    ),
    HeYangXuKollaps(
            label = "He-Yang-Mangel (Kollaps, Yang bricht zusammen)",
            sqlCode = "HeYangXuKollaps",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Mangel,
            symptoms = GeneralSymptoms.HeYangXuPulsTongue + listOf(
                    Symptom.KaelteGefuehl, // extremes
                    Symptom.Zittern,
                    Symptom.Zyanose,
                    Symptom.Bewusstseinsverlust,
                    Symptom.SchweissAusbruch
            )
    ),
    HeBlutStau(
            label = "Herz Blut Stauung",
            sqlCode = "HeBlutStau",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
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
    HeFeuer(
            label = "Loderndes Herz Feuer",
            sqlCode = "HeFeuer",
            description = "Stärkere variante von He-Yin-Mangel",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
                    Symptom.RotesGesicht,
                    Symptom.Erosionen,
                    Symptom.Ulzerationen,
                    Symptom.BittererMundgeschmack,
                    Symptom.UrinierenBrennen,
                    Symptom.BlutInUrin,
                    Symptom.Gewalttaetig,
                    Symptom.StarkeSchlafstoerungen,

                    Symptom.MehrDurst,
                    Symptom.Unruhe,
                    Symptom.Gereiztheit,
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
    HeSchleimFeuerVerstoert(
            label = "Schleim-Feuer verstört das Herz",
            description = "Psychisches Bild, extrovertiert",
            sqlCode = "HeSchleimFeuer",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
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
            description = "Psychisches Bild, extrovertiert",
            sqlCode = "HeSchleimKaelte",
            organ = ZangOrgan.Heart,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
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


    // MILZ
    // =================================================================================================================

    MPQiXu(
            label = "Milz Qi Mangel",
            sqlCode = "MPQiXu",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Mangel,
            symptoms = GeneralSymptoms.QiXu.symptoms + SpecificSymptoms.MPQiXu
    ),
    MPYangXu(
            label = "Milz Yang Mangel",
            sqlCode = "MPYangXu",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Mangel,
            symptoms = SpecificSymptoms.MPQiXu + GeneralSymptoms.YangXu.symptoms + listOf(
                    Symptom.KalterBauch,
                    Symptom.Unterbauchschmerzen,
                    Symptom.Kraempfe, // durch waerme gelindert
                    Symptom.WeicherStuhl,
                    Symptom.WaessrigerStuhl,
                    Symptom.UnverdauteNahrungInStuhl,
                    Symptom.Blaesse,

                    Symptom.Oedeme,
                    Symptom.HahnenschreiDiarrhoe,
                    // zunge
                    Symptom.LeichtBlaeulicheZunge,
                    Symptom.NasseZunge,
                    // puls
                    Symptom.TieferPuls,
                    Symptom.LangsamerPuls
            )
    ),
    MPYangXuAbsinkenQi(
            label = "Absinkendes Milz-Qi",
            sqlCode = "MPQiAbsinken",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Mangel,
            symptoms = MPYangXu.symptoms + listOf(
                    Symptom.UntenZiehendeBauchorgane,
                    Symptom.Prolaps, // "Organsenkung" // von bauch- und unterleibsorgane: magen, darm, nieren, blase, uterus, scheide
                    Symptom.Schweregefuehl,
                    Symptom.Hernien,
                    Symptom.Krampfadern,
                    Symptom.Haemorrhoiden
            )
    ),
    MPYangXuBlutUnkontrolle(
            label = "Milz kann das Blut nicht kontrollieren",
            sqlCode = "MPBlutUnkontrolle",
            organ = ZangOrgan.Spleen,
            tendency = MangelUeberfluss.Mangel,
            symptoms = MPYangXu.symptoms + listOf(
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
            symptoms = listOf(
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
            symptoms = listOf(
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

    LuQiXu(
            label = "Lu-Qi-Mangel",
            sqlCode = "LuQiXu",
            description = "Hat etwas von einer Art Depression aber ohne der Trauer.",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Qi,
            tendency = MangelUeberfluss.Mangel,
            symptoms = GeneralSymptoms.QiXu.symptoms + listOf(
                    Symptom.Kurzatmigkeit,
                    Symptom.Husten,
                    Symptom.DuennerSchleim,
                    Symptom.VielSchleim,
                    Symptom.Asthma,
                    Symptom.FlacheAtmung,

                    Symptom.WenigLeiseSprechen,
                    Symptom.EnergieMangel,
                    Symptom.TrauererloseDepression,

                    Symptom.LeichtesSchwitzen,
                    Symptom.Erkaeltungen,
                    Symptom.AversionKaelte, // MINOR really? eigentlich erst bei YangXu...

                    Symptom.NormaleZunge,
                    Symptom.BlasseZunge,
                    Symptom.GeschwolleneZunge,

                    Symptom.SchwacherPuls,
                    Symptom.WeicherPuls,
                    Symptom.LeererPuls
            )),
    LuYinXu(
            label = "Lu-Yin-Mangel",
            sqlCode = "LuYinXu",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Yin,
            tendency = MangelUeberfluss.Mangel,
            // TODO allg-YinMangel-symptoms + allg-QiMangel-symptoms
            symptoms = LuQiXu.symptoms + listOf(
                    Symptom.Heiserkeit,
                    Symptom.TrockenerHals,
                    Symptom.TrockenerHusten,
                    Symptom.HitzeGefuehlAbends,

                    Symptom.KeinSchleim,
                    Symptom.WenigSchleim,
                    Symptom.KlebrigerSchleim,
                    Symptom.BlutInSchleim,

                    Symptom.RoteZunge,
                    Symptom.TrockeneZunge,
                    Symptom.WenigBelag,

                    Symptom.BeschleunigterPuls,
                    Symptom.DuennerPuls
            )),
    LuWindKaelteWind(
            label = "Wind-Kälte attackiert Lu (Invasion äußerer Wind)",
            sqlCode = "LuWind",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = GeneralSymptoms.LuWindKaelte + listOf(
                    Symptom.KratzenderHals,
                    Symptom.Kopfschmerzen,
                    Symptom.FroestelnMitEtwasFieber,
                    Symptom.Schwitzen,
                    Symptom.AversionWind
            )
    ),
    LuWindKaelteKaelte(
            label = "Wind-Kälte attackiert Lu (Invasion äußere Kälte)",
            sqlCode = "LuKalt",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = GeneralSymptoms.LuWindKaelte + listOf(
                    Symptom.FroestelnStarkerAlsFieber,
                    Symptom.AversionKaelte,
                    Symptom.KeinSchwitzen,
                    Symptom.WenigDurst,
                    Symptom.KeinDurst,

                    Symptom.Husten,
                    Symptom.Schnupfen,
                    Symptom.KlarerSchleim,
                    Symptom.WaessrigerSchleim,
                    Symptom.VerstopfteNase,

                    Symptom.Muskelschmerzen,
                    Symptom.Kopfschmerzen
            )
    ),
    LuWindHitze(
            label = "Wind-Hitze attackiert Lu",
            sqlCode = "LuHitze",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            // TODO plus symptoms of ExoWind; evtl plus HitzeSymptoms
            symptoms = listOf(
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
            description = "Keine wirklichen Hitze Symptome.",
            organ = ZangOrgan.Lung,
            tendency = MangelUeberfluss.Ueberfluss,
            symptoms = listOf(
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
            symptoms = GeneralSymptoms.LuSchleim + listOf(
                    Symptom.WeisserSchleim,
                    Symptom.TrueberSchleim,

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
            symptoms = GeneralSymptoms.LuSchleim + listOf(
                    Symptom.GelberSchleim,

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
            symptoms = GeneralSymptoms.YinXu.symptoms + listOf(
                    // schmerzen unten
                    Symptom.SchmerzenLumbalregion,
                    Symptom.KnieSchmerzen,
                    Symptom.FersenSchmerzen,
                    // sex
                    Symptom.VermehrteLibido,
                    Symptom.SexuelleTraeme,
                    Symptom.VerfruehteEjakulation,
                    Symptom.NaechtlicheEjakulation,
                    Symptom.MenstruationBeeinflusst,
                    // ohren
                    Symptom.Tinnitus,
                    Symptom.Hoerverlust,
                    //misc
                    Symptom.GedaechtnisStoerungen,
                    Symptom.Schwindel,
                    // zunge
                    Symptom.RoterBelag,
                    Symptom.DuennerBelag,
                    Symptom.FehlenderBelag,
                    // puls
                    Symptom.DuennerPuls,
                    Symptom.BeschleunigterPuls
            )
    ),
    NiYangXu(
            label = "Nieren Yang Mangel",
            sqlCode = "NiYangXu",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = GeneralSymptoms.YangXu.symptoms + listOf(
                    Symptom.GedaechtnisStoerungen,
                    Symptom.Zahnausfall,
                    Symptom.Oedeme,
                    // schmerzen unten
                    Symptom.SchmerzenLumbalregion,
                    Symptom.KnieSchmerzen,
                    Symptom.FersenSchmerzen,
                    // sex
                    Symptom.VerminderteLibido,
                    Symptom.Unfruchtbarkeit,
                    Symptom.Impotenz,
                    Symptom.Spermatorrhoe,
                    Symptom.Ausfluss,
                    // ohren
                    Symptom.HoervermoegenVermindert,
                    Symptom.Tinnitus,
                    // urin
                    Symptom.KlarerUrin,
                    Symptom.HellerUrin,
                    Symptom.ReichlichUrin,
                    Symptom.WenigUrin,
                    // MP auch beeinflusst
                    Symptom.VerdauungAllgemein,
                    Symptom.BreiigerStuhl,
                    Symptom.HahnenschreiDiarrhoe,
                    // zunge
                    Symptom.BlasseZunge,
                    Symptom.VergroesserteZunge,
                    Symptom.DuennerBelag,
                    Symptom.WeisserBelag,
                    // puls
                    Symptom.TieferPuls,
                    Symptom.SchwacherPuls,
                    Symptom.VerlangsamterPuls
            )
    ),
    NiYangXuUeberfliessenWasser(
            label = "Nieren Yang Mangel mit Überfließen des Wassers",
            sqlCode = "NiYangXuUeber",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = NiYangXu.symptoms + listOf(
                    Symptom.Palpitationen, // MINOR hat palpitationen, obwohl das eher bei blut-xu vorkommt?!
                    // Lu beeinflusst
                    Symptom.Husten,
                    Symptom.Atemnot,
                    // wasser
                    Symptom.StarkeOedemeBeine,
                    Symptom.LungenOedem,
                    Symptom.Wasserbauch,
                    // zunge
                    Symptom.GeschwolleneZunge,
                    Symptom.DickerBelag,
                    // puls
                    Symptom.HaftenderPuls
            )
    ),
    NiYangXuFestigkeitXu(
            label = "Mangelnde Festigkeit des Nieren Qi",
            sqlCode = "NiYangXuFest",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = listOf(// MINOR why not include NiYang symptoms?
                    // sex
                    Symptom.Spermatorrhoe,
                    Symptom.NaechtlicheEjakulation,
                    Symptom.SexuelleTraeume,
                    Symptom.VerfruehteEjakulation,
                    Symptom.Ausfluss,
                    Symptom.Unfruchtbarkeit,
                    // urin
                    Symptom.HaeufigesUrinieren,
                    Symptom.ReichlichUrin,
                    Symptom.KlarerUrin,
                    Symptom.Nachttroepfeln,
                    Symptom.Harninkontinenz,
                    // zunge
                    Symptom.BlasseZunge,
                    Symptom.GeschwolleneZunge,
                    // puls
                    Symptom.TieferPuls,
                    Symptom.SchwacherPuls
            )
    ),
    NiYangXuQiEinfangen(
            label = "Unfähigkeit der Nieren das Qi einzufangen",
            sqlCode = "NiYangXuFangen",
            organ = ZangOrgan.Kidney,
            tendency = MangelUeberfluss.Mangel,
            symptoms = listOf(// MINOR why not include NiYang symptoms?
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
            symptoms = listOf(
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
            symptoms = listOf(
                    Symptom.VerfruehtesSenium,
                    // MINOR rename Probleme to Allgemein
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


data class GeneralSymptom(
        val yy: YinYang,
        val substance: Substances? = null,
        val symptoms: List<Symptom>
        // val mangel = MangelUeberfluss.Mangel
)

object GeneralSymptoms {

    val BlutXu = GeneralSymptom(
            yy = YinYang.Yin,
            substance = Substances.Xue,
            symptoms = listOf(
                    Symptom.FahleBlaesse,
                    Symptom.Palpitationen,
                    Symptom.TaubheitsgefuehlExtremitaeten,
                    // psycho
                    Symptom.Konzentrationsstoerungen,
                    Symptom.Schreckhaftigkeit,
                    Symptom.Schlafstoerungen

            )
    )
    val YinXu = GeneralSymptom(// hat HITZE
            yy = YinYang.Yin,
            symptoms = BlutXu.symptoms + listOf(
                    // psycho
                    Symptom.Unruhe,
                    Symptom.Nervoesitaet,
                    // hitze
                    Symptom.RoteWangenflecken,
                    Symptom.FuenfZentrenHitze,
                    Symptom.Nachtschweiss,
                    Symptom.HitzeGefuehlAbends,
                    Symptom.TrockenerMund,
                    Symptom.TrockenerHals,
                    Symptom.Halsschmerzen, // haeufig, leichte
                    Symptom.Durst, // mehr? MehrDurstSpaeter?
                    // verdauung
                    Symptom.DunklerUrin,
                    Symptom.KonzentrierterUrin,
                    Symptom.Verstopfung,
                    Symptom.Durchfall
            ))

    val QiXu = GeneralSymptom(
            yy = YinYang.Yin,
            substance = Substances.Qi,
            symptoms = listOf(
                    Symptom.LeuchtendeBlaesse,
                    Symptom.Muedigkeit,
                    Symptom.Schwaeche,
                    Symptom.AnstrengungSchnellErschoepft
            ))
    val YangXu = GeneralSymptom(
            // looks cold, feels cold
            yy = YinYang.Yin,
            symptoms = QiXu.symptoms + listOf(
                    Symptom.KalteHaende,
                    Symptom.KalteFuesse,
                    Symptom.KaelteGefuehl,
                    Symptom.AversionKaelte,
                    // MINOR engegefuehl (auch bei NiYangXu?)
                    // MINOR schmerzen
                    Symptom.Kontraktionen,
                    Symptom.Lethargie,
                    Symptom.Antriebslosigkeit
            ))


    val LeWind = listOf(
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

    val LuSchleim = listOf(
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

    val LuWindKaelte = listOf(
            Symptom.NormaleZunge,
            Symptom.WeisserBelag,
            Symptom.VermehrterBelag,
            Symptom.DuennerBelag,

            Symptom.OberflaechlicherPuls,
            Symptom.GespannterPuls,
            Symptom.VerlangsamterPuls
    )

    val HeYangXuPulsTongue = listOf(
            Symptom.VersteckterPuls,
            Symptom.SchwacherPuls,
            Symptom.UnregelmaessigerPuls,
            Symptom.RauherPuls,

            Symptom.BlaeulicheZunge,
            Symptom.GeschwolleneZunge,
            Symptom.GestauteUnterzungenvenen // stark geschwollen
    )

}

object SpecificSymptoms {

    val HeBlutXu = listOf(
            Symptom.Schwindel,
            Symptom.Aengstlichkeit,
            Symptom.VieleTraeume,
            Symptom.SchlechteMerkfaehigkeit,

            Symptom.BlasseZunge,

            Symptom.DuennerPuls
    )

    val HeQiXu = listOf(
            Symptom.Palpitationen,
            Symptom.Kurzatmigkeit,
            Symptom.StarkesSchwitzen, // va am tag
            Symptom.MentaleMuedigkeit,

            Symptom.BlasseZunge,
            Symptom.GeschwolleneZunge,
            Symptom.LaengsrissInZunge,

            Symptom.SchwacherPuls,
            Symptom.LeererPuls,
            Symptom.UnregelmaessigerPuls
    )

    val MPQiXu = listOf(
            Symptom.WenigAppetit,
            Symptom.VoelleGefuehl,
            Symptom.Blaehungen,
            Symptom.Aufstossen,
            Symptom.LeichteSchmerzenOberbauch,
            Symptom.BreiigerStuhl,

            Symptom.EnergieMangel,
            Symptom.Ausgezehrt,
            Symptom.KraftloseMuskeln,
            Symptom.OedemeBauch,

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
}

