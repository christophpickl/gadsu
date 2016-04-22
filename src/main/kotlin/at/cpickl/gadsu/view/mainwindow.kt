package at.cpickl.gadsu.view

import at.cpickl.gadsu.QuitUserEvent
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.preferences.WindowDescriptor
import at.cpickl.gadsu.view.components.MyFrame
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Point
import javax.inject.Inject
import javax.swing.JFrame
import javax.swing.JPanel



interface MainFrame {
    var descriptor: WindowDescriptor
    val dockPositionRight: Point

    fun start()
    fun close()
    fun changeContent(content: Component)
    fun asJFrame(): JFrame
    fun requestFocus()
}

class SwingMainFrame @Inject constructor(
        val bus: EventBus, // make it visible for directy UI test hack ;)
        private val gadsuMenuBar: GadsuMenuBar
        ) : MainFrame, MyFrame("Gadsu") {

    private val log = LoggerFactory.getLogger(javaClass)
    private val defaultSize = Dimension(600, 400)
    private val container = JPanel()
    private var _descriptor: WindowDescriptor? = null

    init {
        container.name = ViewNames.Main.ContainerPanel
        container.border = BORDER_GAP
        container.debugColor = Color.CYAN
        container.layout = BorderLayout()

        jMenuBar = gadsuMenuBar
        addCloseListener { bus.post(QuitUserEvent()) }

        contentPane.name = ViewNames.Main.ContentPanel
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
        log.debug("Going to show window with size: {}", size)
        isVisible = true
    }

    override fun close() {
        hideAndClose()
    }

    override fun changeContent(content: Component) {
        log.trace("changeContent(content={})", content)
        container.removeAll()
        container.add(content, BorderLayout.CENTER)
        container.revalidate()
        container.repaint()

    }

    override fun asJFrame() = this

}
