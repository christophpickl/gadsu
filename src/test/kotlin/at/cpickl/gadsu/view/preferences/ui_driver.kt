package at.cpickl.gadsu.view.preferences

import at.cpickl.gadsu.testinfra.MainDriver
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.view.Preferences
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.uispec4j.MenuItem
import org.uispec4j.Window
import org.uispec4j.interception.WindowInterceptor
import java.awt.Point
import javax.swing.JFrame


val MainDriver.menuItemPreferences: MenuItem get() = window.menuBar.getMenu("Datei").getSubMenu("Einstellungen")

fun MainDriver.openPreferencesWindow(): Window {
    log.debug("openPreferencesWindow()")
    val window = WindowInterceptor.run(menuItemPreferences.triggerClick())
    MatcherAssert.assertThat(window.getName(), Matchers.equalTo(at.cpickl.gadsu.view.ViewNames.Preferences.Window))
    return window
}

fun UiTest.openPreferencesDriver(): PreferencesDriver {
    return PreferencesDriver(this, mainDriver().openPreferencesWindow())
}

class PreferencesDriver(private val test: UiTest, private val preferencesWindow: Window) {
    private val frame: JFrame get() = preferencesWindow.awtComponent as JFrame
    val location: Point get() = frame.location

    fun assertLocation(expectedLocation: Point) {
        test.assertThat(preferencesWindow.isVisible)
        MatcherAssert.assertThat(frame.location, Matchers.equalTo(expectedLocation))
    }

    fun moveWindowTo(newLocation: Point) {
        frame.location = newLocation
    }

    fun close() {
        preferencesWindow.dispose()
    }

}
