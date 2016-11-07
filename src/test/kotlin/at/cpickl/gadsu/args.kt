package at.cpickl.gadsu

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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

    @Test(expectedExceptions = arrayOf(ArgsException::class))
    fun preferences_notGivenArgument_shouldFail() {
        parseArgs(arrayOf("--preferences"))
    }

    @Test(expectedExceptions = arrayOf(ArgsException::class))
    fun invalidParamFails() {
        parseArgs(arrayOf("--foo"))
    }

    fun `parseArgsOrHelp sunshine`() {
        assertThat(parseArgsOrHelp(arrayOf("")), equalTo(Args.EMPTY))

        // both should print a help menu on the CLI
        assertThat(parseArgsOrHelp(arrayOf("--help")), nullValue())
        assertThat(parseArgsOrHelp(arrayOf("--invalidArg")), nullValue())
    }

    fun `action help`() {
        assertThat(parseArgsOrHelp(arrayOf("--action=help"))!!.action, equalTo("help"))
    }

}
