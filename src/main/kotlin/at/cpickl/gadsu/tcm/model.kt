package at.cpickl.gadsu.tcm

import java.awt.Color


// https://en.wikipedia.org/wiki/Meridian_%28Chinese_medicine%29#Twelve_standard_meridians

enum class Element(val color: Color) {
    Wood(Color.GREEN),
    Fire(Color.RED),
    Earth(Color.YELLOW),
    Metal(Color.LIGHT_GRAY),
    Water(Color.BLUE)
}

enum class Extremity {
    Hand,
    Foot
}

/**
 * One of the 12 standard meridians.
 */
enum class Meridian(
        val element: Element,
        val extremity: Extremity
        // chinesischer name
) :
        // this means, the meridians will be sorted the way they are DEFINED HERE! so watch out.
        Comparable<Meridian>
{
    Lung(Element.Metal, Extremity.Hand),
    LargeIntestine(Element.Metal, Extremity.Hand),
    Stomach(Element.Earth, Extremity.Foot),
    Spleen(Element.Earth, Extremity.Foot), // milz
    Heart(Element.Fire, Extremity.Hand),
    SmallIntestine(Element.Fire, Extremity.Hand),
    UrinaryBladder(Element.Water, Extremity.Foot),
    Kidney(Element.Water, Extremity.Foot), // niere
    Pericardium(Element.Fire, Extremity.Hand),
    TripleBurner(Element.Fire, Extremity.Hand), // san jiao
    GallBladder(Element.Wood, Extremity.Foot),
    Liver(Element.Wood, Extremity.Foot)

}
