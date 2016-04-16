package at.cpickl.gadsu.service

import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.Point
import java.util.prefs.Preferences


interface Prefs {
    var windowDescriptor: WindowDescriptor?
}

class JavaPrefs : Prefs {
    companion object {
        private val KEY_WINDOW_X = "WINDOW_X"
        private val KEY_WINDOW_Y = "WINDOW_Y"
        private val KEY_WINDOW_WIDTH = "WINDOW_WIDTH"
        private val KEY_WINDOW_HEIGHT = "WINDOW_HEIGHT"
    }
    private val log = LoggerFactory.getLogger(javaClass)

    private var _preferences: Preferences? = null
    private val preferences: Preferences
        get() {
            if (_preferences == null) {
                _preferences = Preferences.userNodeForPackage(javaClass)
            }
            return _preferences!!
        }

        override var windowDescriptor: WindowDescriptor?
        get() {
            val x = preferences.getInt(KEY_WINDOW_X, -1)
            if (x == -1) {
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
            preferences.putInt(KEY_WINDOW_X, value.location.x)
            preferences.putInt(KEY_WINDOW_Y, value.location.y)
            preferences.putInt(KEY_WINDOW_WIDTH, value.size.width)
            preferences.putInt(KEY_WINDOW_HEIGHT, value.size.height)
            preferences.flush()
        }
}

data class WindowDescriptor(val location: Point, val size: Dimension)
