package at.cpickl.gadsu.preferences

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.awt.Dimension
import java.awt.Point
import java.io.File
import java.util.prefs.Preferences

@Test class JavaPrefsTest {

    private val testDescriptor = WindowDescriptor(Point(1, 2), Dimension(300, 400))
    private val testData = PreferencesData("testUsername")
    private val testPicFolder = File("testPicFolder/foobar/")

    private val prefNode = javaClass
    private var prefs = JavaPrefs(prefNode)

    @BeforeMethod
    fun initTestee() {
        Preferences.userNodeForPackage(prefNode).clear()
        prefs = JavaPrefs(prefNode)
    }

    //<editor-fold desc="windowDescriptor">

    fun `windowDescriptor, get null at startup`() {
        assertThat(prefs.windowDescriptor, nullValue())
    }

    fun `windowDescriptor, set and get sunshine`() {
        prefs.windowDescriptor = testDescriptor
        assertThat(prefs.windowDescriptor, equalTo(testDescriptor))
    }

    fun `windowDescriptor, when too small set, then get will return null startup value`() {
        val invalidDescriptor = testDescriptor.copy(size = Dimension(0, 0))
        prefs.windowDescriptor = invalidDescriptor

        assertThat(prefs.windowDescriptor, nullValue())
    }

    fun `windowDescriptor, set null will override`() {
        prefs.windowDescriptor = testDescriptor
        prefs.windowDescriptor = null
        assertThat(prefs.windowDescriptor, nullValue())
    }

    fun `windowDescriptor, isValidSize`() {
        assertThat(testDescriptor.copy(size = Dimension(200, 200)).isValidSize, equalTo(true))
        assertThat(testDescriptor.copy(size = Dimension(200, 0)).isValidSize, equalTo(false))
        assertThat(testDescriptor.copy(size = Dimension(0, 200)).isValidSize, equalTo(false))
        assertThat(testDescriptor.copy(size = Dimension(0, 0)).isValidSize, equalTo(false))
    }

    //</editor-fold>


    //<editor-fold desc="preferencesData">

    fun `preferencesData, null at startup`() {
        assertThat(prefs.preferencesData, nullValue())
    }

    fun `preferencesData, set and get sunshine`() {
        prefs.preferencesData = testData
        assertThat(prefs.preferencesData, equalTo(testData))
    }

    fun `preferencesData, set null will override`() {
        prefs.preferencesData = testData
        prefs.preferencesData = null
        assertThat(prefs.preferencesData, nullValue())
    }

    //</editor-fold>


    //<editor-fold desc="clientPicture">

    fun `clientPictureDefaultFolder, get user home directory at startup`() {
        assertPrefsDefaultPicFolder()
    }

    fun `clientPictureDefaultFolder, set and get sunshine`() {
        prefs.clientPictureDefaultFolder = testPicFolder
        assertThat(prefs.clientPictureDefaultFolder.absolutePath, equalTo(testPicFolder.absolutePath))
    }

    //</editor-fold>

    fun `clear, clears all`() {
        prefs.windowDescriptor = testDescriptor
        prefs.clientPictureDefaultFolder = testPicFolder
        prefs.preferencesData = testData

        prefs.clear()

        assertThat(prefs.windowDescriptor, nullValue())
        assertThat(prefs.preferencesData, nullValue())
        assertPrefsDefaultPicFolder()
    }


    private fun assertPrefsDefaultPicFolder() {
        assertThat(prefs.clientPictureDefaultFolder, equalTo(File(System.getProperty("user.home"))))
    }

}
