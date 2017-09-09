package at.cpickl.gadsu

import at.cpickl.gadsu.persistence.DatabaseManager
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.start.ArgsActionException
import at.cpickl.gadsu.start.ArgsActionExecutor
import at.cpickl.gadsu.start.HelpArgAction
import at.cpickl.gadsu.start.RepairDatabaseArgAction
import at.cpickl.gadsu.start.ResetPrefsArgAction
import at.cpickl.gadsu.testinfra.Expects.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class ArgsActionExecutorTest {

    private var prefs = Mockito.mock(Prefs::class.java)
    private var resetPrefsAction = ResetPrefsArgAction(prefs)
    private var helpAction = HelpArgAction()
    private var repairDbAction = RepairDatabaseArgAction(Mockito.mock(DatabaseManager::class.java))
    private var testee = ArgsActionExecutor(resetPrefsAction, helpAction, repairDbAction)

    @BeforeMethod
    fun resetState() {
        prefs = Mockito.mock(Prefs::class.java)
        resetPrefsAction = ResetPrefsArgAction(prefs)
        helpAction = HelpArgAction()
        testee = ArgsActionExecutor(resetPrefsAction, helpAction, repairDbAction)
    }

    @AfterMethod
    fun verifyMocks() {
        verifyNoMoreInteractions(prefs)
    }

    fun `execute invalid action fails`() {
        expect(
                type = ArgsActionException::class,
                messageContains = "testInvalid",
                action = {testee.execute("testInvalid")}
        )
    }

    fun `help simply prints`() {
        testee.execute("help")
    }

    fun `clearPrefs, sunshine`() {
        testee.execute("clearPrefs")
        verify(prefs).clear()
    }

    fun `clearPrefs, case insensitive`() {
        testee.execute("ClEaRpReFs")
        verify(prefs).clear()
    }

    fun `parseParams, sunshine`() {
        assertParseParams("command", emptyMap())
        assertParseParams("command;key1=val1", mapOf(Pair("key1", "val1")))
        assertParseParams("command;key1=val1,key2=val2", mapOf(Pair("key1", "val1"), Pair("key2", "val2")))
    }

    fun `parseParams, no params given but introduced fails`() {
        expect(IllegalArgumentException::class, { testee.parseParams("command;") })
    }

    fun `parseParams, empty param fails`() {
        expect(IllegalArgumentException::class, { testee.parseParams("command;x=1,,y=2") })
    }

    fun `parseParams, special cases`() {
        assertParseParams("command;x=", mapOf(Pair("x", "")))
        assertParseParams("command;x=,y=", mapOf(Pair("x", ""), Pair("y", "")))
    }

    private fun assertParseParams(given: String, expected: Map<String, String>) {
        assertThat(testee.parseParams(given), equalTo(expected))
    }

}
