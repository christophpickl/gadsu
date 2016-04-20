package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.view.Labels
import at.cpickl.gadsu.view.components.GridPanel
import java.awt.Component
import javax.swing.JLabel

interface ClientTab {
    val title: String

    fun asComponent(): Component
}

abstract class DefaultClientTab(override val title: String) : GridPanel(), ClientTab {

    init {
        isOpaque = false
    }

    override final fun asComponent() = this

}

class ClientTabMain : DefaultClientTab(Labels.Tabs.ClientMain) {
    init {
        add(JLabel("main"))
    }


}

class ClientTabDetail : DefaultClientTab(Labels.Tabs.ClientDetail) {
    init {
        add(JLabel("detail"))
    }
}
