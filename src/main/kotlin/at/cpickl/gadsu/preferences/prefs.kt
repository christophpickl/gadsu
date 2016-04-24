package at.cpickl.gadsu.preferences

import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.Point
import java.io.File
import java.util.prefs.Preferences


interface Prefs {
    var preferencesData: PreferencesData?

    var windowDescriptor: WindowDescriptor?
    var clientPictureDefaultFolder: File

    fun reset()
}

class JavaPrefs(private val nodeClass: Class<out Any>) : Prefs {
    companion object {

        private val KEY_USERNAME = "USERNAME"
        private val KEY_WINDOW_X = "WINDOW_X"

        private val KEY_WINDOW_Y = "WINDOW_Y"
        private val KEY_WINDOW_WIDTH = "WINDOW_WIDTH"
        private val KEY_WINDOW_HEIGHT = "WINDOW_HEIGHT"
        private val KEY_CLIENT_PICTURE_DEFAULT_FOLDER = "CLIENT_PICTURE_DEFAULT_FOLDER"

    }
    private val log = LoggerFactory.getLogger(javaClass)
    private var _preferences: Preferences? = null

    private val preferences: Preferences
        get() {
            if (_preferences === null) {
                log.info("Initializing preferences for class: {}", nodeClass.name)
                _preferences = Preferences.userNodeForPackage(nodeClass)
            }
            return _preferences!!
        }
    override var preferencesData: PreferencesData?
        get() {
            log.trace("get preferencesData()")
            val username = preferences.get(KEY_USERNAME, null)
            if (username === null) { // just check one of them is enough
                return null
            }

            return PreferencesData(username)
        }
        set(value) {
            log.trace("set preferencesData(value={})", value)
            if (value === null) {
                preferences.remove(KEY_USERNAME)
                preferences.flush()
                return
            }

            preferences.put(KEY_USERNAME, value.username)
            preferences.flush()
        }

    override var windowDescriptor: WindowDescriptor?
        get() {
            val x = preferences.getInt(KEY_WINDOW_X, -1)
            if (x === -1) {
                return null
            }
            val y = preferences.getInt(KEY_WINDOW_Y, -1)
            val width = preferences.getInt(KEY_WINDOW_WIDTH, -1)
            val height = preferences.getInt(KEY_WINDOW_HEIGHT, -1)
            return WindowDescriptor(Point(x, y), Dimension(width, height))
        }
        set(value) {
            log.trace("set windowDescriptor(value={})", value)
            if (value == null) {
                preferences.remove(KEY_WINDOW_X)
                preferences.remove(KEY_WINDOW_Y)
                preferences.remove(KEY_WINDOW_WIDTH)
                preferences.remove(KEY_WINDOW_HEIGHT)
                preferences.flush()
                return
            }
            if (value.size.width < 100 || value.size.height < 100) {
                log.trace("ignoring invalid size for window descriptor: {}", value)
                return
            }
            preferences.putInt(KEY_WINDOW_X, value.location.x)
            preferences.putInt(KEY_WINDOW_Y, value.location.y)
            preferences.putInt(KEY_WINDOW_WIDTH, value.size.width)
            preferences.putInt(KEY_WINDOW_HEIGHT, value.size.height)
            preferences.flush()
        }


    override var clientPictureDefaultFolder: File
        get() = File(preferences.get(KEY_CLIENT_PICTURE_DEFAULT_FOLDER, System.getProperty("user.home")))
        set(value) {
            log.trace("set clientPictureDefaultFolder(value={})", value.absolutePath)
            preferences.put(KEY_CLIENT_PICTURE_DEFAULT_FOLDER, value.absolutePath)
        }

    override fun reset() {
        log.info("reset()")
        println("reset prefs") // FIXME implement me
    }
}

data class WindowDescriptor(val location: Point, val size: Dimension) {
    // MINOR @VIEWBUG - could be that there is some minor glitch and size calculation/prefs-storage (java.awt.Dimension[width=3,height=4])
    val isValidSize = size.width > 100 && size.height > 100
}
