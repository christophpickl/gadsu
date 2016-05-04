package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.swing.addCloseListener
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Dimension
import javax.inject.Inject
import javax.swing.BorderFactory
import javax.swing.JTextField


interface PreferencesWindow {
    fun start()
    fun close()
    fun initData(preferencesData: PreferencesData)
    fun readData(): PreferencesData
}

class SwingPreferencesFrame @Inject constructor(
        private val mainFrame: MainFrame,
        private val bus: EventBus
) : MyFrame("Einstellungen"), PreferencesWindow {

    private val log = LoggerFactory.getLogger(javaClass)
    private var yetCreated: Boolean = false
    private val inpUsername = JTextField()

    init {
        name = ViewNames.Preferences.Window
        addCloseListener {
            isVisible = false
            bus.post(PreferencesWindowClosedEvent())
        }

        val panel = FormPanel()
        panel.border = BorderFactory.createEmptyBorder(10, 15, 10, 15)
        panel.addFormInput("Benutzername", inpUsername)

        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)

    }

    override fun initData(preferencesData: PreferencesData) {
        log.trace("initData(preferencesData={})", preferencesData)
        inpUsername.text = preferencesData.username
    }

    override fun readData() = PreferencesData(inpUsername.text)

    override fun start() {
        if (yetCreated === false) {
            yetCreated = true
            size = Dimension(500, 400)
            isResizable = false
            setLocationRelativeTo(mainFrame.asJFrame())
            isVisible = true
        } else {
            isVisible = true
        }

    }

    override fun close() {
        hideAndClose()
    }

}
