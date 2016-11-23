package at.cpickl.gadsu.tcm.model

import at.cpickl.gadsu.tcm.model.YinYangDetailMiddle.*
import java.awt.Color

enum class YinYang {
    Yin,
    Yang
}

enum class YinYangMiddle(val yy: YinYang?) {
    Yin(YinYang.Yin),
    Yang(YinYang.Yang),
    Middle(null)
}

enum class YinYangSize {
    Small,
    Big
}

enum class YinYangDetail(val yy: YinYang, size: YinYangSize) {
    YinBig(YinYang.Yin, YinYangSize.Big),
    YinSmall(YinYang.Yin, YinYangSize.Small),
    YangBig(YinYang.Yang, YinYangSize.Big),
    YangSmall(YinYang.Yang, YinYangSize.Small)
}

enum class YinYangDetailMiddle(val yy: YinYangDetail?) {
    YinBig(YinYangDetail.YinBig),
    YinSmall(YinYangDetail.YinSmall),
    YangBig(YinYangDetail.YangBig),
    YangSmall(YinYangDetail.YangSmall),
    Middle(null),
}

enum class Element(
        val label: String,
        val color: Color,
        val yy: YinYangDetailMiddle
) {
    Wood("Holz", Color.decode("#6CDE1F"), YangSmall),
    Fire("Feuer", Color.decode("#E80018"), YangBig),
    Earth("Erde", Color.decode("#C8B622"), Middle),
    Metal("Metall", Color.decode("#ABADAC"), YinSmall),
    Water("Wasser", Color.decode("#77AFFD"), YinBig)
}

enum class Extremity(val label: String) {
    Hand("Hand"),
    Foot("Fu\u00df")
}
