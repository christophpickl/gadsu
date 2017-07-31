package at.cpickl.gadsu.tcm.model

import at.cpickl.gadsu.tcm.model.Element.*
import at.cpickl.gadsu.tcm.model.Symptom.*

fun main(args: Array<String>) {
    println(SyndromeDetector().detect(listOf(
            Symptom.Kurzatmigkeit
    )))
}

data class PossibleSyndrom(
        val syndrome: OrganSyndrome,
        /** from 0.0 to 1.0 */
        val matchRation: Double
)

data class SyndromeReport(
        val possibleSyndromes: List<PossibleSyndrom>
)

class SyndromeDetector {
    // TODO change to symptoms: List<Symptom>
    fun detect(symptoms: List<Symptom>): SyndromeReport {
        val foundSyndromes = mutableListOf<PossibleSyndrom>()

        OrganSyndrome.values().forEach { syndrome ->
//            if (syndrome.symptoms.contains(symptom)) {
//                foundSyndromes += PossibleSyndrom(syndrome, 1.0)
//            }
            val match = calculateMatch(syndrome, symptoms)
            if (match != 0.0) {
                foundSyndromes += PossibleSyndrom(syndrome, match)
            }
        }
        return SyndromeReport(foundSyndromes)
    }

    private fun calculateMatch(syndrome: OrganSyndrome, symptoms: List<Symptom>): Double {
        symptoms.forEach { symptom ->
            if (syndrome.symptoms.contains(symptom)) {
                return 1.0
            }
        }
        return 0.0
    }
}

enum class ExternalPathos(val label: String, val element: Element?) {
    Heat("Hitze", null), // MINOR @TCM model - heat got no element relation?!
    Cold("Kälte", Water),
    Dry("Feuchtigkeit", Earth),
    Wet("Trockenheit", Metal),
    Wind("Wind", Wood),
    Sommerheat("Sommerhitze", Fire)
}

interface IPulseSymptom

sealed class Symptom {
    abstract class GenericSymptom : Symptom()
    object Kurzatmigkeit : GenericSymptom()
    object Heiserkeit : GenericSymptom()
    object SomeOther : GenericSymptom()

    abstract class PulseSymptom : Symptom()
    object SchwacherPuls : PulseSymptom(), IPulseSymptom // TODO do it this or that
    object WeicherPuls : PulseSymptom(), IPulseSymptom
    object LeererPuls : PulseSymptom(), IPulseSymptom
}

enum class SyndromeTendenz(val yy: YinYang) {
    Mangel(YinYang.Yin),
    Ueberfluss(YinYang.Yang)
}

enum class SyndromeAspect(
        val part: SyndromePart,
        val tendenz: SyndromeTendenz
) {
    QiMangel(SyndromePart.Qi, SyndromeTendenz.Mangel),
    YinMangel(SyndromePart.Yin, SyndromeTendenz.Mangel)
}

enum class SyndromePart {
    Qi,
    Xue, // Blut
    Jing, // essenz
    Yin,
    Yang
    ;
    // Blut (stau), nahrungstau
    // E.P.F. (wind, kaelte, hitze, trockenheit, feuchtigkeit)
}

enum class OrganSyndrome(
        val organ: ZangFuOrgan,
        val aspect: SyndromeAspect,
        val symptoms: List<Symptom>
) {
    LuQiMangel(ZangFuOrgan.Lung, SyndromeAspect.QiMangel, listOf(
            Kurzatmigkeit, SchwacherPuls, WeicherPuls, LeererPuls
            // !Kurzatmigkeit, Husten, Asthma, flache Atmung
            // wenig und leise Sprechen, Energiemangel, Müdigkeit, Blässe, trauerlose Depression
            // leichtes Schwitzen, Erkältungen, Aversion Kälte
            // Puls: !schwach, !weich, !leer
    )),

    LuYinMangel(ZangFuOrgan.Lung, SyndromeAspect.YinMangel, LuQiMangel.symptoms.plus(listOf(
            Heiserkeit
            // wie Qi Mangel aber noch dazu: !Heiserkeit, trockener Hals/Husten, subjektives Hitzegefühl Abend
            // Zunge: gerötet, trocken, wenig Belag
            // Puls: beschleunigt, dünn, schwach
    )))
}

enum class ZangFuOrgan(
        val meridian: Meridian
) {
    Lung(Meridian.Lung);

    val zangFu = meridian.zangFu
    /*
    Lung("Lunge", "Lu", "Fei", "LU", Metal, Zang, Hand, 3, First),
    LargeIntestine("Dickdarm", "Di", "Da Chang", "DI", Metal, Fu, Hand, 5, First),
    Stomach("Magen", "Ma", "Wei", "MA", Earth, Fu, Foot, 7, First),
    Spleen("Milz", "MP", "Pi", "MP", Earth, Zang, Foot, 9, First),
    Heart("Herz", "He", "Xin", "HE", Fire, Zang, Hand, 11, Second),
    SmallIntestine("Dünndarm", "Due", "Xio Chang", "DU", Fire, Fu, Hand, 13, Second),
    UrinaryBladder("Blase", "Bl", "Pang Guang", "BL", Water, Fu, Foot, 15, Second),
    Kidney("Niere", "Ni", "Shen", "NI", Water, Zang, Foot, 17, Second),
    // MINOR @TCM model - Pk and 3E are actually no ZangFu, but do have a YinYang association
    Pericardium("Perikard", "Pk", "Xin Bao", "PK", Fire, Zang, Hand, 19, Third),
    TripleBurner("3xErwärmer", "3E", "San Jiao", "3E", Fire, Fu, Hand, 21, Third),
    GallBladder("Gallenblase", "Gb", "Dan", "GB", Wood, Fu, Foot, 23, Third),
    Liver("Leber", "Le", "Gan", "LE", Wood, Zang, Foot, 1, Third);
     */
}
