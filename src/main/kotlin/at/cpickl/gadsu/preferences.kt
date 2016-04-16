package at.cpickl.gadsu

import at.cpickl.gadsu.service.Prefs
import at.cpickl.gadsu.view.MainWindow
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.MyWindow
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Dimension
import javax.inject.Inject
import javax.swing.BorderFactory
import javax.swing.JTextField


object PreferencesViewNames {
    val Window = "Preferences.Window"
}

@Suppress("UNUSED")
val ViewNames.Preferences: PreferencesViewNames
    get() = PreferencesViewNames



class ShowPreferencesEvent : UserEvent()
class PreferencesWindowClosedEvent : UserEvent()

class PreferencesController @Inject constructor(
        private val window: PreferencesWindow,
        private val prefs: Prefs
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onShowPreferencesEvent(@Suppress("UNUSED_PARAMETER") event: ShowPreferencesEvent) {
        log.debug("onShowPreferencesEvent(event)")
        window.initData(prefs.preferencesData ?: PreferencesData.DEFAULT)
        window.start()
    }

    @Subscribe fun onPreferencesWindowClosedEvent(@Suppress("UNUSED_PARAMETER") event: PreferencesWindowClosedEvent) {
        log.debug("onPreferencesWindowClosedEvent(event)")
        prefs.preferencesData = window.readData()
    }

    @Subscribe fun onQuitUserEvent(@Suppress("UNUSED_PARAMETER") event: QuitUserEvent) {
        log.debug("onQuitUserEvent(event)")
        window.close()
    }
}

interface PreferencesWindow {
    fun start()
    fun close()
    fun initData(preferencesData: PreferencesData)
    fun readData(): PreferencesData
}

class SwingPreferencesWindow @Inject constructor(
        private val mainWindow: MainWindow,
        private val bus: EventBus
) : MyWindow("Einstellungen"), PreferencesWindow {

    private val log = LoggerFactory.getLogger(javaClass)
    private var yetCreated: Boolean = false
    private val inpDummy = JTextField()

    init {
        name = ViewNames.Preferences.Window
        addCloseListener {
            isVisible = false
            bus.post(PreferencesWindowClosedEvent())
        }

        val panel = FormPanel()
        panel.border = BorderFactory.createEmptyBorder(10, 15, 10, 15)
        panel.addFormInput("Dummy", inpDummy)

        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)

    }

    override fun initData(preferencesData: PreferencesData) {
        log.trace("initData(preferencesData={})", preferencesData)
        inpDummy.text = preferencesData.dummy
    }

    override fun readData() = PreferencesData(inpDummy.text)

    override fun start() {
        if (yetCreated == false) {
            yetCreated = true
            size = Dimension(500, 400)
            isResizable = false
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

data class PreferencesData(val dummy: String) {
    companion object {
        val DEFAULT = PreferencesData("")
    }
}
