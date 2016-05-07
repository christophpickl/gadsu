package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.transparent
import java.awt.Component

interface ClientTab {
    val title: String
    val scrolled: Boolean
    fun isModified(client: Client): Boolean
    fun updateFields(client: Client)
    fun asComponent(): Component
}

abstract class DefaultClientTab(override val title: String, override val scrolled: Boolean = false) : GridPanel(), ClientTab {

    init {
        transparent()
    }

    override final fun asComponent() = this

}
