package at.cpickl.gadsu

object GadsuSystemProperty {
    val development = BooleanSystemProperty("gadsu.development")
    val testRun = BooleanSystemProperty("gadsu.testRun")
    val overrideLanguage = StringSystemProperty("gadsu.overrideLanguage")
    val disableLog = BooleanSystemProperty("gadsu.disableLog")
    val isMacApp = BooleanSystemProperty("gadsu.isMacApp")
    val disableAutoUpdate = BooleanSystemProperty("gadsu.disableAutoUpdate")
    val disableAutoBackup = BooleanSystemProperty("gadsu.disableAutoBackup")
}

abstract class AbstractSystemProperty(val key: String) {
    fun clear() {
        System.clearProperty(key)
    }
}

class StringSystemProperty(key: String) : AbstractSystemProperty(key) {
    fun getOrNull(): String? {
        return System.getProperty(key, null)
    }
    fun getOrDefault(default: String) : String {
        return System.getProperty(key, null) ?: default
    }

    fun set(value: String) {
        System.setProperty(key, value)
    }
}

class BooleanSystemProperty(key: String) : AbstractSystemProperty(key) {
    fun isEnabledOrNull(): Boolean? {
        val raw = System.getProperty(key, null)?.toLowerCase() ?: return null
        if (raw.equals("true") || raw.equals("1")) {
            return true
        }
        if (raw.equals("false") || raw.equals("0")) {
            return false
        }
        throw GadsuException("Invalid system property '$this' boolean value: '$raw'!")
    }

    fun isEnabledOrFalse(): Boolean {
        return isEnabledOrNull() ?: false
    }

    fun enable() {
        System.setProperty(key, "true")
    }

    fun disable() {
        System.setProperty(key, "false")
    }
}
