package at.cpickl.gadsu.global

import at.cpickl.gadsu.global.GadsuSystemProperty
import at.cpickl.gadsu.service.DateFormats
import java.awt.event.KeyEvent
import java.io.File
import java.net.URL

val GADSU_DIRECTORY = File(System.getProperty("user.home"),
        if (GadsuSystemProperty.testRun.isEnabledOrFalse()) ".gadsu_testRun"
        else if (GadsuSystemProperty.development.isEnabledOrFalse()) ".gadsu_dev"
        else ".gadsu")

val DUMMY_CREATED = DateFormats.DATE_TIME.parseDateTime("01.01.2000 00:00:00")!!

val GADSU_LATEST_VERSION_URL = URL("https://raw.githubusercontent.com/christophpickl/gadsu/master/version_latest.txt")

val IS_OS_MAC = System.getProperty("os.name").toLowerCase().contains("mac")
val IS_OS_WIN = System.getProperty("os.name").toLowerCase().contains("win")

// actually same as: Toolkit.getDefaultToolkit().menuShortcutKeyMask
val SHORTCUT_MODIFIER = if (IS_OS_MAC) KeyEvent.META_DOWN_MASK else KeyEvent.CTRL_DOWN_MASK
val KeyEvent.isShortcutDown: Boolean get() = if (IS_OS_MAC) this.isMetaDown else this.isControlDown

val APP_SUFFIX =
    if (IS_OS_MAC) "dmg"
    else if (IS_OS_WIN) "exe"
    else "jar"
