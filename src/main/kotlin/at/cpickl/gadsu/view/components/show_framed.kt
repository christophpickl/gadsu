package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.RealClock
import com.google.common.eventbus.EventBus
import java.awt.Component
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.WindowConstants

/**
 * For internal use, when starting up part of the UI in its own main method.
 */
class Framed {
    companion object {

        fun showWithContext(function: ((context: FramedContext) -> Component)) {
            val _context = FramedContext()
            val component = function.invoke(_context)
            Framed()._show(component)
        }


        fun show(vararg components: Component) {
            Framed()._show(*components)
        }
    }

    private fun _show(vararg components: Component) {
        val frame = JFrame()

        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.contentPane.layout = BoxLayout(frame.contentPane, BoxLayout.X_AXIS)

        components.forEach { frame.contentPane.add(it) }

        frame.packCenterAndShow()
    }
}
class FramedContext(
        val bus: EventBus = EventBus(),
        val clock: Clock = RealClock(),
        val swing: SwingFactory = SwingFactory(bus, clock)
)
