package at.cpickl.gadsu.view.language

object Labels {

    val Buttons: Buttons get() = LabelsLanguageFinder.find(Buttons::class.java)
    val Tabs: Tabs get() = LabelsLanguageFinder.find(Tabs::class.java)
    val XProps: XPropsLabels get() = LabelsLanguageFinder.find(XPropsLabels::class.java)

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
        override val ClientTexts = "Texte"
        override val ClientTcm = "TCM"
    }

    val TestLabels_EN = object : TestLabels {}

    val Tabs_EN = object : Tabs {
        override val ClientMain = "Main"
        override val ClientTexts = "Texts"
        override val ClientTcm = "TCM"
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
}

interface TestLabels {

}
