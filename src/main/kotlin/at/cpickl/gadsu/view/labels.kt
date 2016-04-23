package at.cpickl.gadsu.view

import at.cpickl.gadsu.GadsuException
import com.google.common.annotations.VisibleForTesting
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.util.Locale

object Languages {
    private val SYSTEM_PROPERTY_NAME = "gadsu.overrideLanguage"
    private val log = LoggerFactory.getLogger(javaClass)

    val language: Language
    val locale: Locale

    init {
        language = _initLanguage()
        locale = language.toLocale()
    }

    @VisibleForTesting
    fun _initLanguage(): Language {
        val overridden = overrideLang()
        if (overridden != null) {
            log.info("Language has been overridden by system property to: {}", overridden)
            return overridden
        }

        val defaultLocale = Locale.getDefault()
        val supports = Language.byId(defaultLocale.language.toUpperCase())
        if (supports != null) {
            log.info("Your locale language '{}' is supported, congrats :)", supports)
            return supports
        }

        log.warn("Your locale language '{}' is NOT supported, switching to default language '{}' instead.",
                defaultLocale, Language.DEFAULT_LANGUAGE)
        return Language.DEFAULT_LANGUAGE
    }


    private fun overrideLang(): Language? {
        val overrideLang = System.getProperty(SYSTEM_PROPERTY_NAME, null) ?: return null
        return Language.byId(overrideLang) ?: throw LanguageException("Invalid override language value '$overrideLang'!")
    }


}

enum class Language(val id: String) {
    DE("DE") {
        override fun toLocale() = Locale.GERMAN
    },
    // FTM disabling english
//    EN("EN") {
//        override fun toLocale() = Locale.ENGLISH
//    },
    TEST_LANG("XXX") {
        override fun toLocale() = Locale.JAPANESE
    };

    companion object {
        val DEFAULT_LANGUAGE = Language.DE

        fun byId(searchId: String) = Language.values().toList().firstOrNull { it.id.equals(searchId) }
    }

    abstract fun toLocale(): Locale
}

object LabelsLanguageFinder {
    private val log = LoggerFactory.getLogger(javaClass)

    fun <T> find(requestType: Class<T>) = findForLanguage<T>(requestType, Languages.language)

    @VisibleForTesting
    fun <T> findForLanguage(requestType: Class<T>, lang: Language): T {
        val found = findOrDefaultLang(requestType, lang) ?:
                throw LanguageException("Internal error: No label definition given for request type of '${requestType.name}' and language '${lang}'!")
        found.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return found.get(null) as T
    }

    private fun <T> findOrDefaultLang(requestType: Class<T>, lang: Language): Field? {
        var fieldRef = _find(requestType, lang)
        if (fieldRef != null) {
            return fieldRef
        }
        log.warn("No labels found for {} in language {}.", requestType.simpleName, lang.name)
        if (lang == Language.DEFAULT_LANGUAGE) {
            return null
        }
        log.debug("Trying to find for default language {}.", Language.DEFAULT_LANGUAGE)
        return _find(requestType, Language.DEFAULT_LANGUAGE)
    }

    private fun <T> _find(requestType: Class<T>, lang: Language): Field? {
        val searchName = "${requestType.simpleName}_${lang.name}"
        log.debug("findForLanguage(requestType={}, lang={}) ... searchName='{}'", requestType.simpleName, lang, searchName)
        val clazz = Labels::class.java
        return clazz.declaredFields.firstOrNull { it.name.equals(searchName) }
    }
}

class LanguageException(message: String, cause: Throwable? = null) : GadsuException(message, cause)

object Labels {
    val Buttons: Buttons get() = LabelsLanguageFinder.find(Buttons::class.java)
    val Tabs: Tabs get() = LabelsLanguageFinder.find(Tabs::class.java)

    val Buttons_DE = object : Buttons {
        override val Insert = "Neu anlegen"
        override val Update = "Speichern"
        override val Back = "Zur\u00fcck"
    }
    val Buttons_EN = object : Buttons {
        override val Insert = "Insert"
        override val Update = "Update"
        override val Back = "Back"
    }

    val Tabs_DE = object : Tabs {
        override val ClientMain = "Allgemein"
        override val ClientTcm = "TCM"
    }

    val TestLabels_EN = object : TestLabels {}

    val Tabs_EN = object : Tabs {
        override val ClientMain = "Main"
        override val ClientTcm = "TCM"
    }

}

interface Buttons {
    val Insert: String
    val Update: String
    val Back: String
}


interface Tabs {
    val ClientMain: String
    val ClientTcm: String
}

interface TestLabels {

}
