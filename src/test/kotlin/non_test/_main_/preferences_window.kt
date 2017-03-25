package non_test._main_

import at.cpickl.gadsu.preferences.PreferencesSwingWindow
import at.cpickl.gadsu.service.RealClock
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import com.google.common.eventbus.EventBus
import org.mockito.Mockito.mock

fun main(args: Array<String>) {
    val mainFrame = mock(MainFrame::class.java)
    val bus = EventBus()
    val clock = RealClock()
    val swingFactory = SwingFactory(bus, clock)
    val window = PreferencesSwingWindow(mainFrame, bus, swingFactory)

//    window.initData()
    window.start()
}
