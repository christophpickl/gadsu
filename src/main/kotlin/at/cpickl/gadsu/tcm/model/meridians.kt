package at.cpickl.gadsu.tcm.model

import at.cpickl.gadsu.tcm.model.Element.*
import at.cpickl.gadsu.tcm.model.Extremity.Foot
import at.cpickl.gadsu.tcm.model.Extremity.Hand
import at.cpickl.gadsu.tcm.model.YinYang.Yang
import at.cpickl.gadsu.tcm.model.YinYang.Yin

// https://en.wikipedia.org/wiki/Meridian_%28Chinese_medicine%29#Twelve_standard_meridians

enum class ZangFu(val yy: YinYang) {
    Zang(Yin),
    Fu(Yang)
}

/**
 * One of the 12 standard meridians.
 */
enum class Meridian(
        val labelLong: String,
        val labelShort: String,
        val labelChinese: String,
        val sqlCode: String, // of length 2
        val element: Element,
        val zangFu: ZangFu,
        val extremity: Extremity,
        val organTime: Int // start hour time, for end time add 2 hours to it

        /// List of Acupunct via Acupuncts
) :
// this means, the meridians will be sorted the way they are DEFINED HERE! so watch out.
        Comparable<Meridian> {
    Lung("Lunge", "Lu", "Fei", "LU", Metal, ZangFu.Zang, Hand, 3),
    LargeIntestine("Dickdarm", "Di", "Da Chang", "DI", Metal, ZangFu.Fu, Hand, 5),
    Stomach("Magen", "Ma", "Wei", "MA", Earth, ZangFu.Fu, Foot, 7),
    Spleen("Milz", "MP", "Pi", "MP", Earth, ZangFu.Zang, Foot, 9),
    Heart("Herz", "He", "Xin", "HE", Fire, ZangFu.Zang, Hand, 11),
    SmallIntestine("Dünndarm", "Dü", "Xio Chang", "DU", Fire, ZangFu.Fu, Hand, 13),
    UrinaryBladder("Blase", "Bl", "Pang Guang", "BL", Water, ZangFu.Fu, Foot, 15),
    Kidney("Niere", "Ni", "Shen", "NI", Water, ZangFu.Zang, Foot, 17),
    // MINOR Pk and 3E are actually no ZangFu, but do have a YinYang association
    Pericardium("Perikard", "Pk", "Xin Bao", "PK", Fire, ZangFu.Zang, Hand, 19),
    TripleBurner("3xErwärmer", "3E", "San Jiao", "3E", Fire, ZangFu.Fu, Hand, 21),
    GallBladder("Gallenblase", "Gb", "Dan", "GB", Wood, ZangFu.Fu, Foot, 23),
    Liver("Leber", "Le", "Gan", "LE", Wood, ZangFu.Zang, Foot, 1);

    // right now, would only return the important ones
//    val pointsCount = lazy { Acupuncts.byMeridian(this).size }

    companion object {
        private val map: Map<String, Meridian> by lazy {
            Meridian.values().associate { Pair(it.sqlCode, it) }
        }

        fun bySqlCode(sqlCode: String): Meridian {
            return map.get(sqlCode) ?: throw IllegalArgumentException("Invalid meridian SQL code: '$sqlCode'")
        }
    }
}
