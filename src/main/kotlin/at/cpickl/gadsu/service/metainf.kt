package at.cpickl.gadsu.service

import at.cpickl.gadsu.version.Version
import com.github.christophpickl.kpotpourri.common.io.closeSilently
import com.google.inject.Provider
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory
import java.util.Properties

/**
 * Load additionally info at runtime (data provided by build process).
 */
class MetaInfLoader : Provider<MetaInf> {

    companion object {
        private val PROPERTIES_CLASSPATH = "/gadsu/metainf.properties"
        private val PROPKEY_VERSION = "application.version"
        private val PROPKEY_BUILT_DATE = "built.date"
        // 15.04.2016 19:56
        private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
    }

    private val log = LoggerFactory.getLogger(javaClass)
    private var cachedValue: MetaInf? = null

    override fun get(): MetaInf {
        if (cachedValue != null) {
            return cachedValue!!
        }
        log.debug("load() ... initializing cached value from '{}'", PROPERTIES_CLASSPATH)

        val props = Properties()
        val inStream = javaClass.getResourceAsStream(PROPERTIES_CLASSPATH)
        try {
            props.load(inStream)
        } finally {
            inStream.closeSilently()
        }

        val version = Version.parse(props.getProperty(PROPKEY_VERSION))
        val builtString = props.getProperty(PROPKEY_BUILT_DATE)

        val built = if (builtString.equals("@built.date@")) DateTime.now() else DATE_FORMATTER.parseDateTime(builtString)
        cachedValue = MetaInf(version, built)
        return cachedValue!!
    }
}

/**
 * Programmatic representation of the metainf.properties file.
 */
data class MetaInf(val applicationVersion: Version, val built: DateTime) {
    companion object {} // for extensions only
}
