package at.cpickl.gadsu.view.preferences

import at.cpickl.gadsu.testinfra.ui.UiTest
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.awt.Point

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

    fun openPreferencesTwoTimes_shouldNotChangeLocationAndJustGetItBackToForegroundAgain() {
        val originalLocation = driver.location
        val newLocation = Point(originalLocation.x + 10, originalLocation.y + 10)
        driver.moveWindowTo(newLocation)

        menuBarDriver.menuItemPreferences.click()
        driver.assertLocation(newLocation)
    }

}

