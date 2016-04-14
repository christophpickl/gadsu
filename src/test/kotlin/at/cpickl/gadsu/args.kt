package at.cpickl.gadsu

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.testng.annotations.Test

@Test class ParseArgsTest {

    private val testDatabaseUrl = "testDatabaseUrl"

    fun databaseUrl_sunshine() {
        assertThat(parseArgs(arrayOf("--databaseUrl", testDatabaseUrl)), equalTo(Args(null, testDatabaseUrl)))
    }

    @Test(expectedExceptions = arrayOf(ArgsException::class))
    fun databaseUrl_notGivenArgument_shouldFail() {
        parseArgs(arrayOf("--databaseUrl"))
    }

    @Test(expectedExceptions = arrayOf(ArgsException::class))
    fun invalidParamFails() {
        parseArgs(arrayOf("--foo"))
    }

    fun help_sunshine() {
        assertThat(parseArgs(arrayOf("--help")).help, notNullValue())
    }

}
