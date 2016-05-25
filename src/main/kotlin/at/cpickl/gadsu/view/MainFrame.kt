package at.cpickl.gadsu.view

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.preferences.WindowDescriptor
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.isTransparent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.*
import javax.inject.Inject
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JPanel

class ChangeMainContentEvent(val newContent: MainContent) : AppEvent()
class MainContentChangedEvent(val oldContent: MainContent?, val newContent: MainContent) : AppEvent()


interface MainContent {
    /**
     * To close datepicker popup.
     */
    fun closePreparations()
    fun asComponent(): Component

}

interface MainFrame {
    var descriptor: WindowDescriptor
    val dockPositionRight: Point

    fun start()
    fun close()
    fun asJFrame(): JFrame
    fun requestFocus()
}

@Logged
open class SwingMainFrame @Inject constructor(
        val bus: EventBus, // make it visible for directy UI test hack ;)
        private val gadsuMenuBar: GadsuMenuBar
        ) : MainFrame, MyFrame("Gadsu") {

    private val log = LoggerFactory.getLogger(javaClass)
    private val defaultSize = Dimension(600, 400)
    private val container = JPanel()
    private var _descriptor: WindowDescriptor? = null

    init {
        iconImage = ImageIcon(javaClass.getResource("/gadsu/logo100.png")).image
        container.name = ViewNames.Main.ContainerPanel
        container.border = BORDER_GAP
        container.debugColor = Color.CYAN
        container.isTransparent = true
        container.layout = BorderLayout()

        jMenuBar = gadsuMenuBar
        addCloseListener { bus.post(QuitEvent()) }

        contentPane.name = ViewNames.Main.ContentPanel
//        contentPane.background = Color.RED
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

    @Subscribe open fun onChangeMainContentEvent(event: ChangeMainContentEvent) {
        val newContent = event.newContent
        val oldContent = if (container.componentCount == 0) null else container.getComponent(0) as MainContent
        container.removeAll() // actually deletes only the single one :)
        container.add(newContent.asComponent(), BorderLayout.CENTER)
        container.revalidate()
        container.repaint()

        bus.post(MainContentChangedEvent(oldContent, newContent))
    }

    override fun asJFrame() = this

}

