package at.cpickl.gadsu

object GadsuSystemPropertyKeys {
    val development = "gadsu.development"
    val overrideLanguage = "gadsu.overrideLanguage"
    public val disableLog = "gadsu.disableLog"
    val isMacApp = "gadsu.isMacApp"
}


fun String.spReadBoolean(): Boolean {
    val value = spReadString("").toLowerCase()
    return value.equals("true") || value.equals("1")
}

fun String.spReadString(orDefault: String): String {
    return System.getProperty(this, orDefault)
}

fun String.spReadStringOrNull(): String? {
    return System.getProperty(this, null)
}

fun String.spWriteTrue() {
    System.setProperty(this, "true")
}

fun String.spWriteString(value: String) {
    System.setProperty(this, value)
}

fun String.spClear() {
    System.clearProperty(this)
}
