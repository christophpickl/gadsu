package non_test

import at.cpickl.gadsu.preferences.WindowDescriptor
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.LogConfigurator
import at.cpickl.gadsu.service.RealClock
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import com.google.common.eventbus.EventBus
import java.awt.Component
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.Point
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.UIManager
import javax.swing.WindowConstants

object DummyMainFrame : MainFrame {
    override var descriptor = WindowDescriptor(Point(0, 0), Dimension(0, 0))
    override val dockPositionRight: Point = Point(0, 0)

    override fun start() {}
    override fun close() {}
    override fun asJFrame() = JFrame()
    override fun requestFocus() {}
}

val DEFAULT_FRAMED_DIMENSION = Dimension(600, 600)

/**
 * For internal use, when starting up part of the UI in its own main method.
 */
class Framed {
    companion object {
        init {
            LogConfigurator(true).configureLog()
            initUi()
        }

        fun initUi() {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            JFrame.setDefaultLookAndFeelDecorated(true)
        }

        fun showWithContextDefaultSize(function: ((context: FramedContext) -> Component)) {
            showWithContext(function, DEFAULT_FRAMED_DIMENSION)
        }

        fun showWithContext(function: ((context: FramedContext) -> Component), size: Dimension? = null) {
            val _context = FramedContext()
            val component = function.invoke(_context)
            Framed()._show(arrayOf(component), size)
        }

        fun showFrameWithContext(function: ((context: FramedContext) -> JFrame), size: Dimension? = null) {
            val _context = FramedContext()
            val frame = function.invoke(_context)
            Framed()._showFrame(frame, size)
        }


        fun show(component: Component, size: Dimension? = null) {
            Framed()._show(arrayOf(component), size)
        }

        fun show(components: Array<Component>, size: Dimension? = null) {
            Framed()._show(components, size)
        }
    }

    private fun _show(components: Array<Component>, size: Dimension? = null) {
        val frame = JFrame()

        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.contentPane.layout = BoxLayout(frame.contentPane, BoxLayout.X_AXIS)

        components.forEach { frame.contentPane.add(it) }

        _showFrame(frame, size)
    }

    private fun _showFrame(frame: JFrame, size: Dimension? = null) {
        if (size != null) {
            frame.size = size
        } else {
            frame.pack()
        }
        frame.setLocationRelativeTo(null)
        EventQueue.invokeLater { frame.isVisible = true }
//        SwingUtilities.invokeLater { frame.isVisible = true }
    }
}

class FramedContext(
        val bus: EventBus = EventBus(),
        val clock: Clock = RealClock(),
        val swing: SwingFactory = SwingFactory(bus, clock)
)
