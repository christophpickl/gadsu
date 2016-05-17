package at.cpickl.gadsu.version

import at.cpickl.gadsu.service.MetaInf
import at.cpickl.gadsu.testinfra.TEST_DATETIME1
import at.cpickl.gadsu.testinfra.skip
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test
import java.net.URL

@Test(groups = arrayOf("System"))
class VersionCheckerImplTest {

    private val url = URL("https://raw.githubusercontent.com/christophpickl/gadsu/master/src/test/resources/gadsu_test/version_latest_TEST.txt")
    private val latest = Version(12, 42, 56, VersionTag.Release)
    private val builtDateUnused = TEST_DATETIME1

    fun `check up2date`() {
        skip("Requires active internet connection :(")
        val current = Version(12, 42, 56, VersionTag.Release)
        val testee = VersionCheckerImpl(WebLatestVersionFetcher(url), MetaInf(current, builtDateUnused))
        assertThat(testee.check(), equalTo(VersionCheckResult.UpToDate as VersionCheckResult))
    }

    fun `check out dated`() {
        skip("Requires active internet connection :(")
        val current = Version(1, 0, 0, VersionTag.Release)
        val testee = VersionCheckerImpl(WebLatestVersionFetcher(url), MetaInf(current, builtDateUnused))
        assertThat(testee.check(), equalTo(VersionCheckResult.OutDated(current, latest) as VersionCheckResult))
    }

}
