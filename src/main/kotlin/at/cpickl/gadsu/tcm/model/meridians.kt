package at.cpickl.gadsu.tcm.model

import at.cpickl.gadsu.tcm.model.Element.*
import at.cpickl.gadsu.tcm.model.Extremity.Foot
import at.cpickl.gadsu.tcm.model.Extremity.Hand
import at.cpickl.gadsu.tcm.model.YinYang.Yang
import at.cpickl.gadsu.tcm.model.YinYang.Yin

// https://en.wikipedia.org/wiki/Meridian_%28Chinese_medicine%29#Twelve_standard_meridians

/**
 * One of the 12 standard meridians.
 */
enum class Meridian(
        val labelLong: String,
        val labelShort: String,
        val sqlCode: String, // of length 2
        val element: Element,
        val yinyang: YinYang,
        val extremity: Extremity,
        val organTime: Int // start hour time, for end time add 2 hours to it
        /// List of Acupunct via Acupuncts
) :
// this means, the meridians will be sorted the way they are DEFINED HERE! so watch out.
        Comparable<Meridian> {
    Lung("Lunge", "Lu", "LU", Metal, Yin, Hand, 3),
    LargeIntestine("Dickdarm", "Di", "DI", Metal, Yang, Hand, 5),
    Stomach("Magen", "Ma", "MA", Earth, Yang, Foot, 7),
    Spleen("Milz", "MP", "MP", Earth, Yin, Foot, 9),
    Heart("Herz", "He", "HE", Fire, Yin, Hand, 11),
    SmallIntestine("Dünndarm", "Dü", "DU", Fire, Yang, Hand, 13),
    UrinaryBladder("Blase", "Bl", "BL", Water, Yang, Foot, 15),
    Kidney("Niere", "Ni", "NI", Water, Yin, Foot, 17),
    Pericardium("Perikard", "Pk", "PK", Fire, Yin, Hand, 19),
    TripleBurner("3xErwärmer", "3E", "3E", Fire, Yang, Hand, 21),
    GallBladder("Gallenblase", "Gb", "GB", Wood, Yang, Foot, 23),
    Liver("Leber", "Le", "LE", Wood, Yin, Foot, 1);

    // right now, would only return the important ones
//    val pointsCount = lazy { Acupuncts.byMeridian(this).size }
}

object MeridianFactory {
    private val map: Map<String, Meridian>

    init {
        map = Meridian.values().associate { Pair(it.sqlCode, it) }
    }

    fun meridianBySqlCode(sqlCode: String): Meridian {
        return map.get(sqlCode) ?: throw IllegalArgumentException("Invalid meridian SQL code: '$sqlCode'")
    }
}

