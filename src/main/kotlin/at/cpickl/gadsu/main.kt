package at.cpickl.gadsu

import at.cpickl.gadsu.service.DateFormats
import java.io.File
import java.net.URL

val GADSU_DIRECTORY = File(System.getProperty("user.home"), if (GadsuSystemProperty.development.isEnabledOrFalse()) ".gadsu_dev" else ".gadsu")
val DUMMY_CREATED = DateFormats.DATE_TIME.parseDateTime("01.01.2000 00:00:00")

val GADSU_LATEST_VERSION_URL = URL("https://raw.githubusercontent.com/christophpickl/gadsu/master/version_latest.txt")

val IS_OS_MAC = System.getProperty("os.name").toLowerCase().contains("mac")
