package at.cpickl.gadsu.view.language

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.GadsuSystemProperty
import com.google.common.annotations.VisibleForTesting
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.util.Locale


object Languages {
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

        // or check system property: user.language = en
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
        val overrideLang = GadsuSystemProperty.overrideLanguage.getOrNull() ?: return null
        return Language.byId(overrideLang) ?: throw LanguageException("Invalid override language value '$overrideLang'!")
    }


}

enum class Language(val id: String) {
    DE("DE") {
        override fun toLocale() = Locale.GERMAN!!
    },
    // FTM disabling english
    //    EN("EN") {
    //        override fun toLocale() = Locale.ENGLISH
    //    },
    TEST_LANG("XXX") {
        override fun toLocale() = Locale.JAPANESE!!
    };

    companion object {
        val DEFAULT_LANGUAGE = DE

        fun byId(searchId: String) = values().toList().firstOrNull { it.id.equals(searchId) }
    }

    abstract fun toLocale(): Locale
}

object LabelsLanguageFinder {
    private val log = LoggerFactory.getLogger(javaClass)

    fun <T> find(requestType: Class<T>) = findForLanguage(requestType, Languages.language)

    @VisibleForTesting
    fun <T> findForLanguage(requestType: Class<T>, lang: Language): T {
        val found = findOrDefaultLang(requestType, lang) ?:
                throw LanguageException("Internal error: No label definition given for request type of '${requestType.name}' and language '$lang'!")
        found.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return found.get(null) as T
    }

    private fun <T> findOrDefaultLang(requestType: Class<T>, lang: Language): Field? {
        val fieldRef = _find(requestType, lang)
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
        log.debug("findForLanguage(requestType={}, language={}) ... searchName='{}'", requestType.simpleName, lang, searchName)
        val clazz = Labels::class.java
//        println(clazz.declaredFields.map { it.name })
        return clazz.declaredFields.firstOrNull { it.name.equals(searchName) }
    }
}

class LanguageException(message: String, cause: Throwable? = null) : GadsuException(message, cause)
