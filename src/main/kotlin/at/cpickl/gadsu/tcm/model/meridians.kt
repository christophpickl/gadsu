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
        val element: Element,
        val yinyang: YinYang,
        val extremity: Extremity,
        val organTime: Int // start hour time, for end time add 2 hours to it
        /// List of Acupunct via Acupuncts
) :
// this means, the meridians will be sorted the way they are DEFINED HERE! so watch out.
        Comparable<Meridian>
{
    Lung(          "Lunge",       "Lu", Metal, Yin,  Hand,  3),
    LargeIntestine("Dickdarm",    "Di", Metal, Yang, Hand,  5),
    Stomach(       "Magen",       "Ma", Earth, Yang, Foot,  7),
    Spleen(        "Milz",        "Mi", Earth, Yin,  Foot,  9),
    Heart(         "Herz",        "He", Fire,  Yin,  Hand, 11),
    SmallIntestine("Dünndarm",    "Dü", Fire,  Yang, Hand, 13),
    UrinaryBladder("Blase",       "Bl", Water, Yang, Foot, 15),
    Kidney(        "Niere",       "Ni", Water, Yin,  Foot, 17),
    Pericardium(   "Perikard",    "Pk", Fire,  Yin,  Hand, 19),
    TripleBurner(  "3xErwärmer",  "3E", Fire,  Yang, Hand, 21),
    GallBladder(   "Gallenblase", "Gb", Wood,  Yang, Foot, 23),
    Liver(         "Leber",       "Le", Wood,  Yin,  Foot,  1);

    // right now, would only return the important ones
//    val pointsCount = lazy { Acupuncts.byMeridian(this).size }

}

