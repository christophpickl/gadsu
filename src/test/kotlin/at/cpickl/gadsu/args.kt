package at.cpickl.gadsu

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.testng.annotations.Test

@Test class ParseArgsTest {

    private val testDatabaseUrl = "testDatabaseUrl"

    fun help_sunshine() {
        assertThat(parseArgs(arrayOf("--help")).help, notNullValue())
    }

    fun databaseUrl_sunshine() {
        assertThat(parseArgs(arrayOf("--databaseUrl", testDatabaseUrl)).databaseUrl, equalTo(testDatabaseUrl))
    }

    fun debug_sunshine() {
        assertThat(parseArgs(arrayOf("--debug")).debug, equalTo(true))
    }

    @Test(expectedExceptions = arrayOf(ArgsException::class))
    fun databaseUrl_notGivenArgument_shouldFail() {
        parseArgs(arrayOf("--databaseUrl"))
    }

    fun preferences_sunshine() {
        val fqn = javaClass.name
        assertThat(parseArgs(arrayOf("--preferences", fqn)).preferencesNode, equalTo(fqn))
    }

    @Test(expectedExceptions = arrayOf(ArgsException::class))
    fun preferences_notGivenArgument_shouldFail() {
        parseArgs(arrayOf("--preferences"))
    }

    @Test(expectedExceptions = arrayOf(ArgsException::class))
    fun invalidParamFails() {
        parseArgs(arrayOf("--foo"))
    }


}
