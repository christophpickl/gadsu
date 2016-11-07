package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.persistence.Jdbcx
import com.google.common.annotations.VisibleForTesting
import java.io.File
import javax.inject.Inject

class JdbcPrefs @Inject constructor(
        private val jdbcx: Jdbcx
) : Prefs {

    companion object {
        @VisibleForTesting val TABLE = "prefs"


    }

    override var preferencesData: PreferencesData
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var windowDescriptor: WindowDescriptor?
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var clientPictureDefaultFolder: File
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var recentSaveReportFolder: File
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var recentSaveMultiProtocolFolder: File
        get() = throw UnsupportedOperationException()
        set(value) {
        }

    override fun clear() {
        jdbcx.execute("DELETE FROM $TABLE")
    }

}
