package at.cpickl.gadsu.view

import at.cpickl.gadsu.QuitUserEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.inject.Inject
import javax.swing.JLabel


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
}

class SwingMainWindow @Inject constructor(
        private val bus: EventBus,
        private val gadsuMenuBar: GadsuMenuBar
) : MainWindow, MyWindow("Gadsu") {

    init {
        jMenuBar = gadsuMenuBar
        addCloseListener { bus.post(QuitUserEvent()) }
    }

    override fun start() {
        contentPane.layout = BorderLayout()
        contentPane.add(JLabel("Gadsu is greeting."), BorderLayout.CENTER)
        packAndShow()
    }

    override fun close() {
        hideAndClose()
    }

}
