package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.service.RealClock
import at.cpickl.gadsu.testinfra.ui.UiTest
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import com.google.common.eventbus.EventBus
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.mockito.Mockito.mock
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.Window
import java.awt.Point
import javax.swing.JFrame


@Test(groups = arrayOf("uiTest"))
class PreferencesSimpleUiTest {

    fun `initData prefills fields`() {
        val bus = EventBus()
        val window = PreferencesSwingWindow(mock(MainFrame::class.java), bus, SwingFactory(bus, RealClock()))
        val preferencesData = PreferencesData.testInstance()

        window.initData(preferencesData)

        assertThat(window.readData(), equalTo(preferencesData))
    }

}


@Test(groups = arrayOf("uiTest"))
class PreferencesUiTest : UiTest() {

    private lateinit var driver: PreferencesDriver

    @BeforeMethod
    fun openPreferencesWindow() {
        driver = openPreferencesDriver()
    }

    @AfterMethod
    fun closePreferencesWindow() {
        driver.close()
    }

    fun `when open preferences two times should not change location and just get it back to foreground again`() {
        val originalLocation = driver.location
        val newLocation = Point(originalLocation.x + 10, originalLocation.y + 10)
        driver.moveWindowTo(newLocation)

        menuBarDriver.menuItemPreferences.click()
        driver.assertLocation(newLocation)
    }

}



class PreferencesDriver(private val test: UiTest, private val preferencesWindow: Window) {

    private val frame: JFrame get() = preferencesWindow.awtComponent as JFrame

    val location: Point get() = frame.location

    fun moveWindowTo(newLocation: Point) {
        frame.location = newLocation
    }

    fun close() {
        preferencesWindow.dispose()
    }

    fun assertLocation(expectedLocation: Point) {
        test.assertThat(preferencesWindow.isVisible)
        MatcherAssert.assertThat(frame.location, Matchers.equalTo(expectedLocation))
    }

}

