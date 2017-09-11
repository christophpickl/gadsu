package at.cpickl.gadsu.view.language

object Labels {

    object ClientSaveDialog {
        val title = "Speichern abgebrochen"
        val button = "Abbrechen"
    }

    // resulted in a compile error with kotlin 1.0.5 :(
    val Buttons: Buttons get() = Buttons_DE //LabelsLanguageFinder.find(Buttons::class.java)
    val Tabs: Tabs get() = Tabs_DE //LabelsLanguageFinder.find(Tabs::class.java)
    val XProps: XPropsLabels get() = XPropsLabels_DE //LabelsLanguageFinder.find(XPropsLabels::class.java)

    val Buttons_DE = object : Buttons {
        override val Insert = "Anlegen"
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
        override val ClientTexts = "Texte"
        override val ClientTcm = "TCM"
        override val ClientAssist = "Assistent"
    }

    val TestLabels_EN = object : TestLabels {}

    val Tabs_EN = object : Tabs {
        override val ClientMain = "Main"
        override val ClientTexts = "Texts"
        override val ClientTcm = "TCM"
        override val ClientAssist = "Assist"
    }


    val XPropsLabels_DE = object : XPropsLabels {
        private val map: Map<String, String> = mapOf(
                // not used yet
        )

        override fun labelFor(key: String): String {
            return map[key] ?: throw LanguageException("No label found for key '$key'!")
        }
    }

}

interface XPropsLabels {
    fun labelFor(key: String): String
}

interface Buttons {
    val Insert: String
    val Update: String
    val Back: String
}


interface Tabs {
    val ClientMain: String
    val ClientTexts: String
    val ClientTcm: String
    val ClientAssist: String
}

interface TestLabels {

}
