package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.view.detail.ClientDetailView
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.view.MainContent
import at.cpickl.gadsu.view.components.panels.GridPanel
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.Insets


interface ClientView : MainContent {
    val masterView: ClientMasterView
    val detailView: ClientDetailView
}

class SwingClientView @Inject constructor(
        override val masterView: ClientMasterView,
        override val detailView: ClientDetailView
) : GridPanel(), ClientView {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        debugColor = Color.YELLOW


        c.fill = GridBagConstraints.VERTICAL
        c.weightx = 0.0
        c.weighty = 1.0
        c.gridx = 0
        add(masterView.asComponent())

        c.gridx++
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.insets = Insets(0, 10, 0, 0)
        add(detailView.asComponent())
    }

    override fun closePreparations() {
        log.trace("closePreparations()")
        detailView.closePreparations()
    }

    override fun asComponent() = this
}
