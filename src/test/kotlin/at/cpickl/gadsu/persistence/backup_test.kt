package at.cpickl.gadsu.persistence

import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.FileSystem
import at.cpickl.gadsu.service.MetaInf
import at.cpickl.gadsu.testinfra.SimpleTestableClock
import at.cpickl.gadsu.testinfra.TEST_DATETIME2
import at.cpickl.gadsu.testinfra.TEST_VERSION000SS
import at.cpickl.gadsu.testinfra.TEST_VERSION1
import at.cpickl.gadsu.testinfra.TEST_VERSION123R
import at.cpickl.gadsu.version.Version
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.File

@Test class BackupControllerTest {

    private val version1 = TEST_VERSION1
    private val date1 = TEST_DATETIME2
    private val date1Yesterday = date1.minusDays(1)
    private val date1Tomorrow = date1.plusDays(1)

    private lateinit var clock: Clock
    private lateinit var assist: BackupAssist
    private lateinit var metaInf: MetaInf
    private lateinit var files: FileSystem

    @BeforeMethod
    fun initState() {
        clock = SimpleTestableClock(date1)
        assist = BackupAssist(File(javaClass.getResource("/gadsu_test/backup_test_db").toURI()), File("test_backup"))
        metaInf = MetaInf(version1, date1)
        files = Mockito.mock(FileSystem::class.java)
    }

    fun `hasTodayBackup, empty files should be false`() {
        whenListFiles()
        assertThat(testee().hasTodayBackup(), equalTo(false))
        verifyListBackups()
    }

    fun `hasTodayBackup, with todays backup should be true`() {
        whenListFiles(backupItem(date1))
        assertThat(testee().hasTodayBackup(), equalTo(true))
        verifyListBackups()
    }

    fun `hasTodayBackup, with yesterdays backup should be false`() {
        whenListFiles(backupItem(date1Yesterday))
        assertThat(testee().hasTodayBackup(), equalTo(false))
        verifyListBackups()
    }


    fun `hasTodayBackup, with yesterdays and todays backup should be true`() {
        whenListFiles(backupItem(date1), backupItem(date1Yesterday))
        assertThat(testee().hasTodayBackup(), equalTo(true))
        verifyListBackups()
    }

    fun `hasTodayBackup, with yesterdays and yesteryesterday backup should be false`() {
        whenListFiles(backupItem(date1Yesterday.minusDays(1)), backupItem(date1Yesterday))
        assertThat(testee().hasTodayBackup(), equalTo(false))
        verifyListBackups()
    }

    fun `createBackup should simply delegate to FileSystems zip method`() {
        testee().createBackup()
        verify(files).zip(assist.databaseDirectory, BackupItem(clock.now(), metaInf.applicationVersion).toFile(assist.backupDirectory))
    }

    fun `ensureMax, no files, should do nothing`() {
        whenListFiles()
        testee().ensureMaxBackups()

        verifyBackupDirExists()
        verifyListBackups()
        verifyNoMoreInteractions(files)
    }

    fun `ensureMax, two files, should do nothing`() {
        whenListFiles(backupItem(date1), backupItem(date1Yesterday))
        testee().ensureMaxBackups()

        verifyBackupDirExists()
        verifyListBackups()
        verifyNoMoreInteractions(files)
    }

    fun `ensureMax, four files, should delete oldest`() {
        val oldestBackup = backupItem(date1Yesterday)
        whenListFiles(backupItem(date1), oldestBackup, backupItem(date1Yesterday), backupItem(date1Tomorrow.plusDays(1)))

        testee().ensureMaxBackups()

        verifyBackupDirExists()
        verifyListBackups()
        verify(files).delete(oldestBackup.toFile(assist.backupDirectory))
        verifyNoMoreInteractions(files)
    }

    private fun testee() = BackupController(clock, assist, metaInf, files)

    private fun backupItem(date: DateTime) = BackupItem(date, version1)

    private fun whenListFiles(vararg backupItems: BackupItem) {
        `when`(files.listFiles(assist.backupDirectory, "zip")).thenReturn(backupItems.map { it.toFile(assist.backupDirectory) })
    }

    private fun verifyBackupDirExists() {
        verify(files).ensureExists(assist.backupDirectory)
    }

    private fun verifyListBackups() {
        verify(files).listFiles(assist.backupDirectory, "zip")
    }

}

@Test class BackupExtensionsTest {

    fun `BackupItem, toFile`() {
        val parentDirectory = File(".")
        assertThat(BackupItem(TEST_DATETIME2, TEST_VERSION123R).toFile(parentDirectory),
                equalTo(File(parentDirectory, "gadsu_backup-v1_2_3-2002_12_31_23_59_58.zip")))
    }


    fun `Version, toFileName`() {
        assertThat(TEST_VERSION123R.toFileName(), equalTo("v1_2_3"))
        assertThat(TEST_VERSION000SS.toFileName(), equalTo("v0_0_0_SS"))
    }

    fun `Version, parseByFile`() {
        assertThat(Version.parseByFile("1_2_3"), equalTo(TEST_VERSION123R))
        assertThat(Version.parseByFile("0_0_0_SS"), equalTo(TEST_VERSION000SS))
    }

}
