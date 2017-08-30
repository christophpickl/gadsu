package at.cpickl.gadsu.tcm.model

import at.cpickl.gadsu.tcm.model.YinYangDetailMiddle.*
import at.cpickl.gadsu.tcm.patho.ZangOrgan
import java.awt.Color

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
        val zangFu: ZangOrgan
) {
    Qi("Qi", YinYang.Yang, ZangOrgan.Lung),
    Xue("Blut", YinYang.Yin, ZangOrgan.Liver),
    Jing("Jing", YinYang.Yang, ZangOrgan.Kidney),
    Shen("Shen", YinYang.Yang, ZangOrgan.Heart),
    JinYe("JinYe", YinYang.Yin, ZangOrgan.Spleen)
}
