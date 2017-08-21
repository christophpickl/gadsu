package at.cpickl.gadsu.tcm.model

import at.cpickl.gadsu.tcm.model.YinYangDetailMiddle.*
import java.awt.Color

enum class ZangFu(val yy: YinYang) {
    Zang(YinYang.Yin),
    Fu(YinYang.Yang)
}

enum class ZangOrgan(
        val meridian: Meridian
) {
    Lung(Meridian.Lung),
    Spleen(Meridian.Spleen),
    Heart(Meridian.Heart),
    Liver(Meridian.Liver),
    Kidney(Meridian.Kidney)
    ;
}

enum class Element(
        val label: String,
        val labelChinese: String,
        val color: Color,
        val yy: YinYangDetailMiddle
) {
    Wood("Holz", "Mu", Color.decode("#6CDE1F"), YangSmall),
    Fire("Feuer", "Huo", Color.decode("#E80018"), YangBig),
    Earth("Erde", "Tu", Color.decode("#C8B622"), Middle),
    Metal("Metall", "Jin", Color.decode("#ABADAC"), YinSmall),
    Water("Wasser", "Shui", Color.decode("#77AFFD"), YinBig)
}

enum class Extremity(val label: String) {
    Hand("Hand"),
    Foot("Fu\u00df")
}

enum class Substances(
        val label: String,
        val yy: YinYang,
        val meridian: Meridian // MINOR should actually be a ZangFu, not a Meridian
) {
    Qi("Vitalenergie", YinYang.Yang, Meridian.Lung),
    Xue("Blut", YinYang.Yin, Meridian.Liver),
    Jing("Essenz", YinYang.Yang, Meridian.Kidney),
    Shen("Geist", YinYang.Yang, Meridian.Heart),
    JinYe("Körperflüssigkeiten", YinYang.Yin, Meridian.Spleen)
}
