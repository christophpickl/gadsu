@file:Suppress("unused")

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
    Lesser("Shao"),
    Greater("Tai"),
    Bright("Ming"),
    Terminal("Jue")
}

// https://en.wikipedia.org/wiki/Six_levels
enum class YinYangLevel(val labelChinese: String, val yy: YinYang, val size: YinYangSize) {
    GreaterYin("Tai Yin", YinYang.Yin, YinYangSize.Greater),
    LesserYin("Shao Yin", YinYang.Yin, YinYangSize.Lesser),
    TerminalYin("Jue Yin", YinYang.Yin, YinYangSize.Terminal),

    GreaterYang("Tai Yang", YinYang.Yang, YinYangSize.Greater),
    BrightYang("Yang Ming", YinYang.Yang, YinYangSize.Bright),
    LesserYang("Shao Yang", YinYang.Yang, YinYangSize.Lesser)
}

enum class YinYangDetailMiddle(val yy: YinYangLevel?) {
    YinBig(YinYangLevel.GreaterYin),
    YinSmall(YinYangLevel.LesserYin),
    YangBig(YinYangLevel.GreaterYang),
    YangSmall(YinYangLevel.LesserYang),
    Middle(null),
}
