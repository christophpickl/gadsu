package at.cpickl.gadsu.version

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test
class VersionTest {

    private val v1_0 = Version(1, 0, VersionTag.Release)
    private val v1_0_SS = Version(1, 0, VersionTag.Snapshot)
    private val v1_1 = Version(1, 1, VersionTag.Release)
    private val v2_0 = Version(2, 0, VersionTag.Release)
    private val v12_34 = Version(12, 34, VersionTag.Release)

    @DataProvider
    fun provideParse(): Array<Array<out Any>> = arrayOf(
            arrayOf("1.0", v1_0),
            arrayOf("12.34", v12_34),
            arrayOf("1.0-SNAPSHOT", v1_0_SS)
    )

    @Test(dataProvider = "provideParse")
    fun `parse`(rawString: String, expected: Version) {
        assertThat(Version.parse(rawString), equalTo(expected))
    }


    @DataProvider
    fun provideCompares(): Array<Array<out Any>> = arrayOf(
            arrayOf("major", listOf(v2_0, v1_0), listOf(v1_0, v2_0)),
            arrayOf("snapshot", listOf(v1_0_SS, v1_0), listOf(v1_0, v1_0_SS)),
            arrayOf("all combined", listOf(v2_0, v1_0_SS, v1_1, v12_34, v1_0), listOf(v1_0, v1_0_SS, v1_1, v2_0, v12_34))
    )

    @Test(dataProvider = "provideCompares")
    fun `compare`(reason: String, given: List<Version>, expected: List<Version>) {
        assertThat("Test failed, reason: " + reason, given.sorted(), equalTo(expected))
    }

}
