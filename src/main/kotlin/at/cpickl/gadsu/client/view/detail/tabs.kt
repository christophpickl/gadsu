package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.components.panels.GridPanel
import java.awt.Component

interface ClientTab {
    val title: String
    fun isModified(client: Client): Boolean
    fun updateFields(client: Client)
    fun asComponent(): Component
}

abstract class DefaultClientTab(override val title: String) : GridPanel(), ClientTab {

    init {
        isOpaque = false
    }

    override final fun asComponent() = this

}
