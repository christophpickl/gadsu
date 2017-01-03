package at.cpickl.gadsu.tcm.model

import at.cpickl.gadsu.acupuncture.Acupunct
import at.cpickl.gadsu.tcm.model.Element.*
import at.cpickl.gadsu.tcm.model.Extremity.Foot
import at.cpickl.gadsu.tcm.model.Extremity.Hand
import at.cpickl.gadsu.tcm.model.Family.*
import at.cpickl.gadsu.tcm.model.YinYang.Yang
import at.cpickl.gadsu.tcm.model.YinYang.Yin
import at.cpickl.gadsu.tcm.model.ZangFu.Fu
import at.cpickl.gadsu.tcm.model.ZangFu.Zang

// https://en.wikipedia.org/wiki/Meridian_%28Chinese_medicine%29#Twelve_standard_meridians

enum class ZangFu(val yy: YinYang) {
    Zang(Yin),
    Fu(Yang)
}

// 12 hauptleitbahnen, 12 leitbahnzweige, 8 unpaarige, 15 netzleitbahne, netzbahnzweige, netzbahnen 3rd generation, 12 muskelleitbahne, hauptregionen

enum class UnpairedMeridian(val label: String) {
    RenMai("Ren Mai"),
    DuMai("Du Mai"),
    ChongMai("Chong Mai")
    // ...
    ;
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
        val organTime: Int, // start hour time, for end time add 2 hours to it
        val family: Family
) : Comparable<Meridian> { // this means, the meridians will be sorted the way they are DEFINED HERE! so watch out.

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

    companion object {
        private val mapSqlCode: Map<String, Meridian> by lazy { Meridian.values().associate { Pair(it.sqlCode, it) } }
        private val mapLabelShort: Map<String, Meridian> by lazy { Meridian.values().associate { Pair(it.labelShort.toLowerCase(), it) } }

        fun bySqlCode(search: String): Meridian {
            return mapSqlCode[search] ?: throw IllegalArgumentException("Invalid meridian SQL code: '$search'")
        }

        fun byLabelShort(search: String): Meridian {
            return mapLabelShort[search.toLowerCase()] ?: throw IllegalArgumentException("Invalid meridian short label: '$search'")
        }
    }

    val acupuncts: List<Acupunct> by lazy { Acupunct.allForMeridian(this) }

    init {
        if (sqlCode.length != 2) throw IllegalArgumentException("sqlCode expected to be of length 2! Was: [$sqlCode]")
    }
}

enum class Family {
    First, // front
    Second, // back
    Third // side/middle
}
