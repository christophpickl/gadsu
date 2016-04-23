package at.cpickl.gadsu.view.preferences

import at.cpickl.gadsu.testinfra.UiTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.uispec4j.Window
import java.awt.Point
import javax.swing.JFrame


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
