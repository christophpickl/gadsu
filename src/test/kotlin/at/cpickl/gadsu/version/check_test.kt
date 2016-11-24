package at.cpickl.gadsu.version

import at.cpickl.gadsu.service.MetaInf
import at.cpickl.gadsu.testinfra.TEST_DATETIME1
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.net.URL

@Test class VersionCheckerImplTest {

    private val version1 = Version(1, 2, 3, VersionTag.Release)
    private val version2 = Version(2, 3, 4, VersionTag.Release)

    private lateinit var fetcher: LatestVersionFetcher

    @BeforeMethod fun initMocks() {
        fetcher = mock(LatestVersionFetcher::class.java)
    }

    fun `check up2date`() {
        val appVersion = version1
        val webVersion = version1
        `when`(fetcher.fetch()).thenReturn(webVersion)

        assertThat(testee(appVersion).check(),
                equalTo(VersionCheckResult.UpToDate as VersionCheckResult))
    }

    fun `check out dated`() {
        val appVersion = version1
        val webVersion = version2
        `when`(fetcher.fetch()).thenReturn(webVersion)

        assertThat(testee(appVersion).check(),
                equalTo(VersionCheckResult.OutDated(appVersion, webVersion) as VersionCheckResult))
    }

    private fun testee(appVersion: Version) = VersionCheckerImpl(fetcher, MetaInf(appVersion, TEST_DATETIME1))

}

/**
 * Requires active internet connection.
 */
@Test(groups = arrayOf("mTest", "System"))
class VersionCheckerManualTest {

    private val url = URL("https://raw.githubusercontent.com/christophpickl/gadsu/master/src/test/resources/gadsu_test/version_latest_TEST.txt")
    private val latest = Version(12, 42, 56, VersionTag.Release)
    private val builtDateUnused = TEST_DATETIME1

    fun `check up2date`() {
        val current = Version(12, 42, 56, VersionTag.Release)
        val testee = VersionCheckerImpl(WebLatestVersionFetcher(url), MetaInf(current, builtDateUnused))
        assertThat(testee.check(), equalTo(VersionCheckResult.UpToDate as VersionCheckResult))
    }

    fun `check out dated`() {
        val current = Version(1, 0, 0, VersionTag.Release)
        val testee = VersionCheckerImpl(WebLatestVersionFetcher(url), MetaInf(current, builtDateUnused))
        assertThat(testee.check(), equalTo(VersionCheckResult.OutDated(current, latest) as VersionCheckResult))
    }

}
