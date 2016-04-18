package at.cpickl.gadsu

import at.cpickl.gadsu.service.DateFormats
import java.io.File

val GADSU_DIRECTORY = File(System.getProperty("user.home"), ".gadsu")
val DUMMY_CREATED = DateFormats.DATE_TIME.parseDateTime("01.01.2000 00:00:00")
