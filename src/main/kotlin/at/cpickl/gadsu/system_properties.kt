package at.cpickl.gadsu

object GadsuSystemPropertyKeys {
    val development = "gadsu.development"
    val overrideLanguage = "gadsu.overrideLanguage"
    val disableLog = "gadsu.disableLog"
    val isMacApp = "gadsu.isMacApp"
}


fun String.spReadBoolean(): Boolean {
    val value = spReadStringOrNull()?.toLowerCase() ?: throw GadsuException("System property '$this' not set!")
    if (value.equals("true") || value.equals("1")) {
        return true
    }
    if (value.equals("false") || value.equals("0")) {
        return false
    }
    throw GadsuException("Invalid system property '$this' boolean value: '$value'!")
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

fun String.spWriteFalse() {
    System.setProperty(this, "false")
}

fun String.spWriteString(value: String) {
    System.setProperty(this, value)
}

fun String.spClear() {
    System.clearProperty(this)
}
