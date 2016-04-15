package at.cpickl.gadsu.view

import at.cpickl.gadsu.QuitUserEvent
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.MetaInfLoader
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JFrame
import javax.swing.JLabel

class ShowAboutDialogEvent : UserEvent() {}

class AboutModule : AbstractModule() {
    override fun configure() {
        bind(AboutController::class.java).asEagerSingleton()
        bind(AboutWindow::class.java).`in`(Scopes.SINGLETON)
    }
}

class AboutController @Inject constructor(
        private val window: AboutWindow
) {

    @Subscribe fun onAbout(@Suppress("UNUSED_PARAMETER") event: ShowAboutDialogEvent) {
        window.setVisible(true)
    }

    @Suppress("unused")
    @Subscribe fun onQuit(@Suppress("UNUSED_PARAMETER") event: QuitUserEvent) {
        window.setVisible(false)
        window.dispose()
    }
}

class AboutWindow @Inject constructor(
        metaInfLoader: MetaInfLoader
) : JFrame() {
    init {
        val metaInf = metaInfLoader.load()
        title = "Gadsu abooouuut"
        contentPane.layout = BorderLayout()
        contentPane.add(JLabel("<html>Version: ${metaInf.applicationVersion}<br>" +
                "Datum: ${DateFormats.DATE_TIME.print(metaInf.built)}</html>"), BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(null)
    }
}
