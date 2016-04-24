package at.cpickl.gadsu.tcm

import at.cpickl.gadsu.tcm.Element.*
import at.cpickl.gadsu.tcm.Extremity.Foot
import at.cpickl.gadsu.tcm.Extremity.Hand
import java.awt.Color


// https://en.wikipedia.org/wiki/Meridian_%28Chinese_medicine%29#Twelve_standard_meridians

enum class Element(
        val label: String,
        val color: Color
) {
    Wood("Holz", Color.decode("#6CDE1F")),
    Fire("Feuer", Color.decode("#E80018")),
    Earth("Erde", Color.decode("#C8B622")),
    Metal("Metall", Color.decode("#ABADAC")),
    Water("Wasser", Color.decode("#77AFFD"))
}

enum class Extremity(val label: String) {
    Hand("Hand"),
    Foot("Fu\u00df")
}

/**
 * One of the 12 standard meridians.
 */
enum class Meridian(
        val labelLong: String,
        val labelShort: String,
        val element: Element,
        val extremity: Extremity
) :
        // this means, the meridians will be sorted the way they are DEFINED HERE! so watch out.
        Comparable<Meridian>
{
    Lung(          "Lunge",           "Lu", Metal, Hand),
    LargeIntestine("Dickdarm",        "Di", Metal, Hand),
    Stomach(       "Magen",           "Ma", Earth, Foot),
    Spleen(        "Milz",            "Mi", Earth, Foot),
    Heart(         "Herz",            "He", Fire, Hand),
    SmallIntestine("D\u00fcnndarm",   "D\u00fc", Fire, Hand),
    UrinaryBladder("Blase",           "Bl", Water, Foot),
    Kidney(        "Niere",           "Ni", Water, Foot),
    Pericardium(   "Perikard",        "Pk", Fire, Hand),
    TripleBurner(  "3xErw\u00e4rmer", "3E", Fire, Hand),
    GallBladder(   "Gallenblase",     "Gb", Wood, Foot),
    Liver(         "Leber",           "Le", Wood, Foot)
}
