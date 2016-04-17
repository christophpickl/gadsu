package at.cpickl.gadsu.view

enum class Lang {
    DE, EN
}

object Labels {

    private val lang: Lang

    init {
        // FIXME check system property for language (luxury: configurable via preferences)
        lang = Lang.EN
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