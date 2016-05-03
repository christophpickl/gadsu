package at.cpickl.gadsu.tcm.model

import java.awt.Color

enum class YinYang {
    Yin,
    Yang
}

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
