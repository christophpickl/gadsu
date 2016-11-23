package at.cpickl.gadsu.tcm.model


enum class YinYang(val label: String) {
    Yin("Yin"),
    Yang("Yang")
}

enum class YinYangMiddle(val yy: YinYang?) {
    Yin(YinYang.Yin),
    Yang(YinYang.Yang),
    Middle(null)
}

enum class YinYangSize(val labelChinese: String) {
    Small("Tai"),
    Big("Shao")
}

enum class YinYangDetail(val labelChinese: String, val yy: YinYang, size: YinYangSize) {
    YinBig("Tai Yin", YinYang.Yin, YinYangSize.Big),
    YinSmall("Shao Yin", YinYang.Yin, YinYangSize.Small),
    YangBig("Tai Yang", YinYang.Yang, YinYangSize.Big),
    YangSmall("Shao Yang", YinYang.Yang, YinYangSize.Small)
}

enum class YinYangDetailMiddle(val yy: YinYangDetail?) {
    YinBig(YinYangDetail.YinBig),
    YinSmall(YinYangDetail.YinSmall),
    YangBig(YinYangDetail.YangBig),
    YangSmall(YinYangDetail.YangSmall),
    Middle(null),
}
