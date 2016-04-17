package at.cpickl.gadsu.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.Development
import at.cpickl.gadsu.QuitUserEvent
import at.cpickl.gadsu.client.view.ChangeBehaviour
import at.cpickl.gadsu.client.view.ClientViewController
import at.cpickl.gadsu.service.Prefs
import at.cpickl.gadsu.service.WindowDescriptor
import at.cpickl.gadsu.view.components.MyWindow
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.inject.Inject
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JPanel


class MainWindowController @Inject constructor(
        private val window: MainWindow,
        private val clientController: ClientViewController,
        private val prefs: Prefs
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onAppStartupEvent(@Suppress("UNUSED_PARAMETER") event: AppStartupEvent) {
        if (prefs.windowDescriptor != null) {
            window.descriptor = prefs.windowDescriptor!!
        }
    }

    @Subscribe fun onQuitUserEvent(@Suppress("UNUSED_PARAMETER") event: QuitUserEvent) {
        log.info("onQuitUserEvent(event)")

        if (clientController.checkChanges() === ChangeBehaviour.ABORT) {
            log.debug("Quit aborted by changes detected by the client controller.")
            return
        }

        prefs.windowDescriptor = window.descriptor
        window.close()
    }
}


interface MainWindow {
    var descriptor: WindowDescriptor

    fun start()
    fun close()
    fun changeContent(content: Component)
    fun asJFrame(): JFrame
}

class SwingMainWindow @Inject constructor(
        private val bus: EventBus,
        private val gadsuMenuBar: GadsuMenuBar
        ) : MainWindow, MyWindow("Gadsu") {

    private val log = LoggerFactory.getLogger(javaClass)
    private val defaultSize = Dimension(600, 400)
    private val container = JPanel()
    private var _descriptor: WindowDescriptor? = null

    init {
        if (Development.ENABLED) container.background = Color.CYAN

        jMenuBar = gadsuMenuBar
        addCloseListener { bus.post(QuitUserEvent()) }

        container.layout = BorderLayout()
        container.border = BorderFactory.createEmptyBorder(10, 15, 10, 15)

        contentPane.layout = BorderLayout()
        contentPane.add(container, BorderLayout.CENTER)
    }

    override var descriptor: WindowDescriptor
        get() = WindowDescriptor(location, size)
        set(value) {
            log.trace("set descriptor(value={})", value)
            _descriptor = value
            location = value.location
            size = if (value.isValidSize) value.size else defaultSize
        }


    override fun start() {
        if (_descriptor === null) {
            size = defaultSize
            setLocationRelativeTo(null)
        }
        setVisible(true)
    }

    override fun close() {
        hideAndClose()
    }

    override fun changeContent(content: Component) {
        log.trace("changeContent(content={})", content)
        container.removeAll()
        container.add(content, BorderLayout.CENTER)
    }

    override fun asJFrame() = this

}
