package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.KTab
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.swing.transparent

enum class ClientTabType(val label: String) {
    MAIN(Labels.Tabs.ClientMain),
    TEXTS(Labels.Tabs.ClientTexts),
    TCM(Labels.Tabs.ClientTcm),
    ASSIST(Labels.Tabs.ClientAssist);
}

interface ClientTab : KTab {
    val type: ClientTabType

    fun isModified(client: Client): Boolean
    fun updateFields(client: Client)
}

abstract class DefaultClientTab(
        override val tabTitle: String,
        override val type: ClientTabType,
        override val scrolled: Boolean = false
) :
        GridPanel(), ClientTab {

    init {
        transparent()
    }

    override final fun asComponent() = this

}
