package at.cpickl.gadsu

import at.cpickl.gadsu.service.DateFormats
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

@Suppress("UNUSED")
val DateFormats.Companion.SQL_TIMESTAMP: DateTimeFormatter
    get() = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

open class PersistenceException(message: String, cause: Exception? = null) : GadsuException(message, cause)
