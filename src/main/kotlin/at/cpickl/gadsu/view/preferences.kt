package at.cpickl.gadsu.view

import at.cpickl.gadsu.QuitUserEvent
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.service.Prefs
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.MyWindow
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JLabel


object PreferencesViewNames {
    val Window = "Preferences.Window"
}

@Suppress("UNUSED")
val ViewNames.Preferences: PreferencesViewNames
    get() = PreferencesViewNames



class ShowPreferencesEvent : UserEvent()

class PreferencesController @Inject constructor(
        private val window: PreferencesWindow,
        private val prefs: Prefs
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onShowPreferencesEvent(@Suppress("UNUSED_PARAMETER") event: ShowPreferencesEvent) {
        log.debug("onShowPreferencesEvent(event)")
        window.start()
    }

    @Subscribe fun onQuitUserEvent(@Suppress("UNUSED_PARAMETER") event: QuitUserEvent) {
        log.debug("onQuitUserEvent(event)")
        // TODO get values and store in Prefs
        window.close()
    }
}

interface PreferencesWindow {
    fun start()
    fun close()
}

class SwingPreferencesWindow @Inject constructor(
        private val mainWindow: MainWindow
) : MyWindow("Einstellungen"), PreferencesWindow {

    private var yetCreated: Boolean = false

    init {
        name = ViewNames.Preferences.Window
        addCloseListener {
            isVisible = false
        }

        val panel = GridPanel()
        panel.add(JLabel("preferences"))
        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)

    }

    override fun start() {
        if (yetCreated == false) {
            yetCreated = true
            pack()
            setLocationRelativeTo(mainWindow.asJFrame())
            isVisible = true
        } else {
            isVisible = true
        }

    }

    override fun close() {
        hideAndClose()
    }

}