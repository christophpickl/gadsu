package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.service.nullIfEmpty
import com.google.common.annotations.VisibleForTesting
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.SingleColumnRowMapper
import java.awt.Dimension
import java.awt.Point
import java.io.File
import javax.inject.Inject

class JdbcPrefs @Inject constructor(
        private val jdbcx: Jdbcx
) : Prefs {

    companion object {

        @VisibleForTesting val TABLE = "prefs"

        private val KEY_USERNAME = "USERNAME"
        private val KEY_CHECK_UPDATES = "CHECK_UPDATES"
        private val KEY_PROXY = "PROXY"
        private val KEY_GCAL_NAME = "GCAL_NAME"
        private val KEY_TREATMENT_GOAL = "TREATMENT_GOAL"

        private val KEY_WINDOW_X = "WINDOW_X"
        private val KEY_WINDOW_Y = "WINDOW_Y"
        private val KEY_WINDOW_WIDTH = "WINDOW_WIDTH"
        private val KEY_WINDOW_HEIGHT = "WINDOW_HEIGHT"

        private val KEY_CLIENT_PICTURE_DEFAULT_FOLDER = "CLIENT_PICTURE_DEFAULT_FOLDER"
        private val KEY_RECENT_SAVE_REPORT_FOLDER = "RECENT_SAVE_REPORT_FOLDER"
        private val KEY_RECENT_SAVE_MULTIPROTOCOL_FOLDER = "KEY_RECENT_SAVE_MULTIPROTOCOL_FOLDER"

    }

    private val log = LoggerFactory.getLogger(javaClass)

    override var preferencesData: PreferencesData
        get() {
            log.trace("get preferencesData()")
            val username = queryValue(KEY_USERNAME) ?: return PreferencesData.DEFAULT
            val checkUpdates = queryValue(KEY_CHECK_UPDATES)!!.toBoolean()
            val proxy = queryValue(KEY_PROXY)?.nullIfEmpty()
            val gcalName = queryValue(KEY_GCAL_NAME)?.nullIfEmpty()
            val treatmentGoal = queryValue(KEY_TREATMENT_GOAL)?.nullIfEmpty()?.toInt()

            return PreferencesData(username, checkUpdates, proxy, gcalName, treatmentGoal)
        }
        set(value) {
            log.trace("set preferencesData(value={})", value)
            storeValue(KEY_USERNAME, value.username)
            storeValue(KEY_CHECK_UPDATES, value.checkUpdates.toString())
            storeValue(KEY_PROXY, value.proxy ?: "")
            storeValue(KEY_GCAL_NAME, value.gcalName ?: "")
            storeValue(KEY_TREATMENT_GOAL, value.treatmentGoal?.toString() ?: "")
        }

    override var windowDescriptor: WindowDescriptor?
        get() {
            val windowX = queryValue(KEY_WINDOW_X)?.toInt() ?: return null
            val windowY = queryValue(KEY_WINDOW_Y)?.toInt() ?: return null
            val windowWidth = queryValue(KEY_WINDOW_WIDTH)?.toInt() ?: return null
            val windowHeight = queryValue(KEY_WINDOW_HEIGHT)?.toInt() ?: return null

            return WindowDescriptor(Point(windowX, windowY), Dimension(windowWidth, windowHeight))
        }
        set(value) {
            log.trace("set windowDescriptor(value={})", value)
            if (value == null) {
                jdbcx.update("DELETE FROM $TABLE WHERE data_key = ? OR data_key = ? OR data_key = ? OR data_key = ?",
                        KEY_WINDOW_X, KEY_WINDOW_Y, KEY_WINDOW_WIDTH, KEY_WINDOW_HEIGHT)
                return
            }
            if (!value.isValidSize) {
                log.trace("ignoring invalid size for window descriptor: {}", value)
                return
            }
            storeValue(KEY_WINDOW_X, value.location.x.toString())
            storeValue(KEY_WINDOW_Y, value.location.y.toString())
            storeValue(KEY_WINDOW_WIDTH, value.size.width.toString())
            storeValue(KEY_WINDOW_HEIGHT, value.size.height.toString())
        }

    override var clientPictureDefaultFolder: File
        get() = recentFolder(KEY_CLIENT_PICTURE_DEFAULT_FOLDER)
        set(value) {
            log.trace("set clientPictureDefaultFolder(value={})", value.absolutePath)
            storeValue(KEY_CLIENT_PICTURE_DEFAULT_FOLDER, value.absolutePath)
        }

    override var recentSaveReportFolder: File
        get() = recentFolder(KEY_RECENT_SAVE_REPORT_FOLDER)
        set(value) {
            log.trace("set recentSaveReportFolder(value={})", value.absolutePath)
            storeValue(KEY_RECENT_SAVE_REPORT_FOLDER, value.absolutePath)
        }

    override var recentSaveMultiProtocolFolder: File
        get() = recentFolder(KEY_RECENT_SAVE_MULTIPROTOCOL_FOLDER)
        set(value) {
            log.trace("set recentSaveMultiProtocolFolder(value={})", value.absolutePath)
            storeValue(KEY_RECENT_SAVE_MULTIPROTOCOL_FOLDER, value.absolutePath)
        }

    override fun clear() {
        jdbcx.execute("DELETE FROM $TABLE")
    }

    private fun recentFolder(key: String): File {
        val path = queryValue(key) ?: return File(System.getProperty("user.home"))
        val folder = File(path)
        if (!folder.exists() || !folder.isDirectory) {
            log.debug("Recent folder does not exist (anymore), going to return user home directory instead.")
            return File(System.getProperty("user.home"))
        }
        return folder
    }

    private fun storeValue(key: String, value: String) {
        jdbcx.update("INSERT INTO $TABLE (data_key, data_value) VALUES (?, ?)", key, value)
    }

    private fun queryValue(key: String): String? {
        return jdbcx.queryMaybeSingle(SingleColumnRowMapper<String>(), "SELECT data_value FROM $TABLE WHERE data_key = ?", arrayOf(key))
    }

}
