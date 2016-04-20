package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.view.detail.ClientDetailView
import at.cpickl.gadsu.debugColor
import at.cpickl.gadsu.view.components.GridPanel
import com.google.inject.Inject
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.Insets


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
        debugColor = Color.YELLOW

        c.weighty = 1.0

        c.fill = GridBagConstraints.VERTICAL
        c.weightx = 0.0
        c.gridx = 0
        add(masterView.asComponent())

        c.gridx++
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.insets = Insets(0, 10, 0, 0)
        add(detailView.asComponent())
    }

    override fun asComponent() = this
}
