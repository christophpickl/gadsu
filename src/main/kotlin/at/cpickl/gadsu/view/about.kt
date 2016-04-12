package at.cpickl.gadsu.view

import at.cpickl.gadsu.QuitUserEvent
import at.cpickl.gadsu.UserEvent
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JFrame
import javax.swing.JLabel

class ShowAboutDialogEvent : UserEvent() {}

class AboutModule : AbstractModule() {
    override fun configure() {
        bind(AboutController::class.java).asEagerSingleton()
    }
}

class AboutController @Inject constructor(
        private val window: AboutWindow
) {

    @Subscribe fun onAbout(event: ShowAboutDialogEvent) {
        window.setVisible(true)
    }

    @Subscribe fun onQuit(event: QuitUserEvent) {
        window.setVisible(false)
        window.dispose()
    }
}

class AboutWindow : JFrame() {
    init {
        title = "Gadsu abooouuut"
        contentPane.layout = BorderLayout()
        contentPane.add(JLabel("Abouuut."), BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(null)
    }
}
