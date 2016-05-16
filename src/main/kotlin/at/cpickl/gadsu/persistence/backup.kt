package at.cpickl.gadsu.persistence

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.GADSU_DIRECTORY
import at.cpickl.gadsu.GlobalExceptionHandler
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.FileSystem
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.MetaInf
import at.cpickl.gadsu.service.clearTime
import at.cpickl.gadsu.service.formatDateTimeFile
import at.cpickl.gadsu.service.parseDateTimeFile
import at.cpickl.gadsu.version.Version
import at.cpickl.gadsu.version.VersionTag
import com.google.common.annotations.VisibleForTesting
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import org.joda.time.DateTime
import java.io.File
import javax.inject.Inject

val GADSU_BACKUPS_DIRECTORY = File(GADSU_DIRECTORY, "backups")

class BackupModule : AbstractModule() {
    override fun configure() {
        bind(BackupAssist::class.java).toInstance(BackupAssist(GADSU_DATABASE_DIRECTORY, GADSU_BACKUPS_DIRECTORY))
        bind(BackupController::class.java).to(BackupControllerImpl::class.java).asEagerSingleton()
    }

}

data class BackupAssist(val databaseDirectory: File, val backupDirectory: File)

fun Version.Companion.parseByFile(fileName: String): Version {
    val isSnapshot = fileName.endsWith("_SS")
    val cleanedFileName = if (isSnapshot) fileName.substring(0, fileName.length - "_SS".length) else fileName
    val parts = cleanedFileName.split("_")
    return Version(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), if (isSnapshot) VersionTag.Snapshot else VersionTag.Release)
}

fun Version.toFileName() = "v${major}_${minor}_$bugfix${if (tag == VersionTag.Snapshot) "_SS" else ""}"

data class BackupItem(val date: DateTime, val gadsuVersion: Version) {
    companion object {
        private val log = LOG(javaClass)

        fun newByFile(file: File) : BackupItem {
            log.trace("newByFile(file.name={})", file.name)
            val parts = file.name.substring(0, file.name.length - ".zip".length).split("-")
            // parts[0] == "gadsu_backup"
            val versionString = parts[1].substring("v".length)
            val dateString = parts[2]
            return BackupItem(dateString.parseDateTimeFile(), Version.parseByFile(versionString))
        }
    }

    fun toFile(parentDirectory: File): File {
        return File(parentDirectory, "gadsu_backup-v${gadsuVersion.major}_${gadsuVersion.minor}_${gadsuVersion.bugfix}-${date.formatDateTimeFile()}.zip")
    }
}

interface BackupController {
    fun findLatestBackup(): BackupItem?

}
open class BackupControllerImpl @Inject constructor(
        private val clock: Clock,
        private val assist: BackupAssist,
        private val metaInf: MetaInf,
        private val files: FileSystem
) : BackupController {
    private val log = LOG(javaClass)

    private val maxBackups = 3
    init {
        files.ensureExists(assist.backupDirectory)
    }

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        GlobalExceptionHandler.startThread {
            if (!hasTodayBackup()) {
                log.info("Going to create a fresh backup.")
                createBackup()
                ensureMaxBackups()
            }
        }
    }

    override fun findLatestBackup(): BackupItem? {
        return listBackups().sortedByDescending { it.date }.firstOrNull()
    }

    @VisibleForTesting fun createBackup() {
        val backup = BackupItem(clock.now(), metaInf.applicationVersion)
        // we should actually exclude the "*.lck" file here :)
        val zipFile = backup.toFile(assist.backupDirectory)
        files.zip(assist.databaseDirectory, zipFile)
        log.debug("Created ZIP file at: {}", zipFile.absolutePath)
    }

    @VisibleForTesting fun ensureMaxBackups() {
        val sortedBackups = listBackups().sortedBy { it.date }
        if (sortedBackups.size > maxBackups) {
            val oldestBackup = sortedBackups.first()
            files.delete(oldestBackup.toFile(assist.backupDirectory))
        }
    }

    @VisibleForTesting fun hasTodayBackup(): Boolean {
        val latestBackup = findLatestBackup() ?: return false
        val checkDate = clock.now().clearTime().minusSeconds(1)
        val result = latestBackup.date.isAfter(checkDate)
        log.trace("hasTodayBackup() = {} ... latest.date={}, checkDate={}", result, latestBackup.date, checkDate)
        return result
    }

    private fun listBackups() = files.listFiles(assist.backupDirectory, "zip").map { BackupItem.newByFile(it) }

}

