package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.Development
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.GridPanel
import com.google.inject.Inject
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.Insets


object ClientViewNames {
    // all via extensions
}

@Suppress("UNUSED")
val ViewNames.Client: ClientViewNames
    get() = ClientViewNames


interface ClientView {
    val masterView: ClientMasterView
    val detailView: ClientDetailView

    fun asComponent(): Component
}

class SwingClientView @Inject constructor(
        override val masterView: ClientMasterView,
        override val detailView: ClientDetailView
) : GridPanel(), ClientView {

    init {
        if (Development.ENABLED) background = Color.YELLOW

        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0

        c.weightx = 0.3
        c.gridx = 0
        add(masterView.asComponent())

        c.weightx = 0.7
        c.gridx++
        c.insets = Insets(0, 10, 0, 0)
        add(detailView.asComponent())
    }

    override fun asComponent() = this
}
