package at.cpickl.gadsu.view

import at.cpickl.gadsu.GadsuException

enum class Lang {
    DE, EN
}

object Labels {

    private val lang: Lang

    init {
        // FIXME check system property for language (luxury: configurable via preferences)
        lang = overrideLang() ?: Lang.DE
    }

    private fun overrideLang(): Lang? {
        val overrideLang = System.getProperty("gadsu.overrideLanguage", null) ?: return null
        when (overrideLang) {
            "DE" -> return Lang.DE
            "EN" -> return Lang.EN
            else -> throw GadsuException("Invalid override language value '$overrideLang'!")
        }
    }

    val Buttons: Buttons get() {
        when(lang) {
            Lang.DE -> return Buttons_DE
            else -> return Buttons_EN
        }
    }
}

object Buttons_DE : Buttons {
    override val Insert = "Neu anlegen"
    override val Update = "Speichern"
    override val Back = "Zur\u00fcck"
    override val OpenFile = "Durchsuchen"
}

object Buttons_EN : Buttons {
    override val Insert = "Insert"
    override val Update = "Update"
    override val Back = "Back"
    override val OpenFile = "Open File"
}


interface Buttons {
    val Insert: String
    val Update: String
    val Back: String
    val OpenFile: String
}