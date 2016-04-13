package at.cpickl.gadsu.view

import at.cpickl.gadsu.QuitUserEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Dimension
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JPanel


class MainWindowController @Inject constructor(
        private val window: MainWindow
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onQuit(@Suppress("UNUSED_PARAMETER") event: QuitUserEvent) {
        log.info("onQuit(event)")
        window.close()
    }
}


interface MainWindow {
    fun start()
    fun close()

    fun changeContent(content: JComponent)
}

class SwingMainWindow @Inject constructor(
        private val bus: EventBus,
        private val gadsuMenuBar: GadsuMenuBar
) : MainWindow, MyWindow("Gadsu") {

    private val log = LoggerFactory.getLogger(javaClass)
    private val container: JPanel = JPanel()

    init {
        jMenuBar = gadsuMenuBar
        addCloseListener { bus.post(QuitUserEvent()) }

        contentPane.layout = BorderLayout()
        contentPane.add(container, BorderLayout.CENTER)
    }

    override fun start() {
        size = Dimension(600, 400)
        setLocationRelativeTo(null)
        setVisible(true)
    }

    override fun close() {
        hideAndClose()
    }

    override fun changeContent(content: JComponent) {
        log.trace("changeContent(content={})", content)
        container.removeAll()
        container.add(content)
    }

}
