package at.cpickl.gadsu.view

import at.cpickl.gadsu.GadsuException
import java.util.Locale

object Languages {
    private val SYSTEM_PROPERTY_NAME = "gadsu.overrideLanguage"
    private val DEFAULT_LANGUAGE = Language.DE

    val language: Language
    val locale: Locale

    init {
        // FIXME check default system property for language (luxury: configurable via preferences)
        language = overrideLang() ?: DEFAULT_LANGUAGE
        locale = language.toLocale()
    }

    private fun overrideLang(): Language? {
        val overrideLang = System.getProperty(SYSTEM_PROPERTY_NAME, null) ?: return null
        when (overrideLang) {
            "DE" -> return Language.DE
            "EN" -> return Language.EN
            else -> throw GadsuException("Invalid override language value '$overrideLang'!")
        }
    }


}

enum class Language {
    DE {
        override fun toLocale() = Locale.GERMAN
    },
    EN {
        override fun toLocale() = Locale.ENGLISH
    };

    abstract fun toLocale(): Locale
}

object Labels {
    // MINOR use reflection instead and cache result for future reoccuring use
    val Buttons: Buttons get() {
        when(Languages.language) {
            Language.DE -> return Buttons_DE
            else -> return Buttons_EN
        }
    }
    val Tabs: Tabs get() {
        when(Languages.language) {
            Language.DE -> return Tabs_DE
            else -> return Tabs_EN
        }
    }
}

object Buttons_DE : Buttons {
    override val Insert = "Neu anlegen"
    override val Update = "Speichern"
    override val Back = "Zur\u00fcck"
}

object Buttons_EN : Buttons {
    override val Insert = "Insert"
    override val Update = "Update"
    override val Back = "Back"
}


interface Buttons {
    val Insert: String
    val Update: String
    val Back: String
}

object Tabs_DE : Tabs {
    override val ClientMain = "Allgemein"
    override val ClientDetail = "Profil"
}

object Tabs_EN : Tabs {
    override val ClientMain = "Main"
    override val ClientDetail = "Detail"
}


interface Tabs {
    val ClientMain: String
    val ClientDetail: String
}