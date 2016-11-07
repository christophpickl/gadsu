package at.cpickl.gadsu.preferences

import java.awt.Dimension
import java.awt.Point
import java.io.File


interface Prefs {
    var preferencesData: PreferencesData

    var windowDescriptor: WindowDescriptor?
    var clientPictureDefaultFolder: File
    var recentSaveReportFolder: File
    var recentSaveMultiProtocolFolder: File

    fun clear()
}

// MINOR @UI - check if screen size changed, or better: just be sure its not over the max (luxury: save per display setting!)
data class WindowDescriptor(val location: Point, val size: Dimension) {

    companion object {
        private val MIN_WIDTH = 100
        private val MIN_HEIGHT = 100
    }

    // MINOR @UI BUG - could be that there is some minor glitch and size calculation/prefs-storage (java.awt.Dimension[width=3,height=4])
    val isValidSize = size.width >= MIN_WIDTH && size.height >= MIN_HEIGHT
}
