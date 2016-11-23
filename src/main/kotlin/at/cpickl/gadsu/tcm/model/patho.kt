package at.cpickl.gadsu.tcm.model

import at.cpickl.gadsu.tcm.model.Element.*

enum class ExternalPathos(val label: String, val element: Element?) {
    Heat("Hitze", null), // MINOR heat got no element relation?!
    Cold("Kälte", Water),
    Dry("Feuchtigkeit", Earth),
    Wet("Trockenheit", Metal),
    Wind("Wind", Wood),
    Sommerheat("Sommerhitze", Fire)
}
