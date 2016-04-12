package at.cpickl.gadsu.view

import at.cpickl.gadsu.QuitUserEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.inject.Inject
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.WindowConstants


class MainWindowController @Inject constructor(
        private val window: MainWindow
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onQuit(event: QuitUserEvent) {
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
) : MainWindow, JFrame() {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        title = "Gadsu"
        jMenuBar = gadsuMenuBar
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(event: WindowEvent) {
                log.trace("windowClosing() captured, dispatching QuitUserEvent")
                bus.post(QuitUserEvent())
            }
        })

    }

    override fun start() {
        contentPane.layout = BorderLayout()
        contentPane.add(JLabel("Gadsu is greeting."), BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(null)

        setVisible(true)
    }

    override fun close() {
        setVisible(false)
        dispose()
    }

}