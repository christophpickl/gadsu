package at.cpickl.gadsu.preferences

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.awt.Dimension
import java.awt.Point
import java.io.File

fun WindowDescriptor.Companion.newWithSize(width: Int, height: Int) =
        WindowDescriptor(Point(0, 0), Dimension(width, height))

@Test class WindowDescriptorTest {

    fun `windowDescriptor, isValidSize`() {
        assertThat(WindowDescriptor.newWithSize(200, 200).isValidSize, equalTo(true))
        assertThat(WindowDescriptor.newWithSize(200, 0).isValidSize, equalTo(false))
        assertThat(WindowDescriptor.newWithSize(0, 200).isValidSize, equalTo(false))
        assertThat(WindowDescriptor.newWithSize(0, 0).isValidSize, equalTo(false))
    }

}

@Test abstract class PrefsTest {

    protected lateinit var prefs: Prefs

    protected abstract fun createPrefs(): Prefs

    @BeforeMethod
    fun init() {
        prefs = createPrefs()
    }


    //<editor-fold desc="windowDescriptor">

    private val validDescriptor = WindowDescriptor.newWithSize(200, 200)
    private val invalidDescriptor = WindowDescriptor.newWithSize(0, 0)

    fun `windowDescriptor, get null at startup`() {
        assertThat(prefs.windowDescriptor, nullValue())
    }

    fun `windowDescriptor, set and get sunshine`() {
        prefs.windowDescriptor = validDescriptor
        assertThat(prefs.windowDescriptor, equalTo(validDescriptor))
    }

    fun `windowDescriptor, set null will override`() {
        prefs.windowDescriptor = validDescriptor
        prefs.windowDescriptor = null
        assertThat(prefs.windowDescriptor, nullValue())
    }

    fun `windowDescriptor, when too small set, then get will return null startup value`() {
        prefs.windowDescriptor = invalidDescriptor

        assertThat(prefs.windowDescriptor, nullValue())
    }

    //</editor-fold>


    //<editor-fold desc="preferencesData">

    private val testData = PreferencesData("testUsername", true, "proxy:42", "gcal_calendar_name", "gmail@gmail.com", 20)

    fun `preferencesData, default at startup`() {
        assertDefaultPreferencesData()
    }

    fun `preferencesData, set and get sunshine`() {
        prefs.preferencesData = testData
        assertThat(prefs.preferencesData, equalTo(testData))
    }

    private fun assertDefaultPreferencesData() {
        assertThat(prefs.preferencesData, sameInstance(PreferencesData.DEFAULT))
    }

    //</editor-fold>


    //<editor-fold desc="clientPicture">

    private val existingFolder = File("/")
    private val existingFolder2 = File(System.getProperty("user.home"))
    private val nonExistingFolder = File("/not_existing")
    private val defaultClientPictureDefaultFolder = File(System.getProperty("user.home"))

    fun `clientPictureDefaultFolder, get user home directory at startup`() {
        assertDefaultPicFolder()
    }

    fun `clientPictureDefaultFolder, set and get sunshine`() {
        prefs.clientPictureDefaultFolder = existingFolder
        assertThat(prefs.clientPictureDefaultFolder.absolutePath, equalTo(existingFolder.absolutePath))
    }

    fun `clientPictureDefaultFolder, set non existing folder should return default folder on get`() {
        prefs.clientPictureDefaultFolder = nonExistingFolder
        assertThat(prefs.clientPictureDefaultFolder.absolutePath, equalTo(defaultClientPictureDefaultFolder.absolutePath))
    }

    fun `clientPictureDefaultFolder, set twice will override`() {
        prefs.clientPictureDefaultFolder = existingFolder
        prefs.clientPictureDefaultFolder = existingFolder2
        assertThat(prefs.clientPictureDefaultFolder.absolutePath, equalTo(existingFolder2.absolutePath))
    }

    private fun assertDefaultPicFolder() {
        assertThat(prefs.clientPictureDefaultFolder, equalTo(defaultClientPictureDefaultFolder))
    }

    //</editor-fold>

    fun `recentSaveReportFolder, set and get sunshine`() {
        prefs.recentSaveReportFolder = existingFolder
        assertThat(prefs.recentSaveReportFolder.absolutePath, equalTo(existingFolder.absolutePath))
    }

    fun `recentSaveMultiProtocolFolder, set and get sunshine`() {
        prefs.recentSaveMultiProtocolFolder = existingFolder
        assertThat(prefs.recentSaveMultiProtocolFolder.absolutePath, equalTo(existingFolder.absolutePath))
    }


    //<editor-fold desc="clear">

    fun `clear, clears all and assert via getters`() {
        prefs.windowDescriptor = validDescriptor
        prefs.clientPictureDefaultFolder = existingFolder
        prefs.preferencesData = testData

        prefs.clear()

        assertThat(prefs.windowDescriptor, nullValue())
        assertDefaultPreferencesData()
        assertDefaultPicFolder()
    }

    //</editor-fold>

}
