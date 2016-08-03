package at.cpickl.gadsu

import at.cpickl.gadsu.persistence.DatabaseManager
import at.cpickl.gadsu.preferences.Prefs
import com.google.common.annotations.VisibleForTesting
import com.google.common.base.Splitter
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ArgsActionException(message: String) : GadsuException(message)

class ArgsActionExecutor @Inject constructor(
        resetPrefsAction: ResetPrefsArgAction,
        helpAction: HelpArgAction,
        repairDbAction: RepairDatabaseArgAction
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val actions = listOf<ArgAction>(resetPrefsAction, helpAction, repairDbAction)

    fun execute(actionUrl: String) {
        log.debug("execute(actionUrl={})", actionUrl)

        val actionName = if (!actionUrl.contains(";")) actionUrl else actionUrl.substring(0, actionUrl.indexOf(";"))
        val action = actions.firstOrNull { it.actionName.toLowerCase().equals(actionName.toLowerCase()) } ?:
                throw ArgsActionException("Invalid action requested '${actionName}'!")

        val params = parseParams(actionUrl)
        // parse args: "action;foo=bar", watch out for "action"
        action.execute(params)
    }

    @VisibleForTesting
    fun parseParams(actionUrl: String): Map<String, String> {
        if (!actionUrl.contains(";")) return emptyMap()
        val rawParams = actionUrl.substring(actionUrl.indexOf(";") + 1)
        log.trace("Raw params: '{}'", rawParams)
        return Splitter.on(",").trimResults().withKeyValueSeparator("=").split(rawParams)
    }
}

interface ArgAction {
    val actionName: String
    fun execute(params: Map<String, String>)
}


abstract class BaseArgAction(override val actionName: String) : ArgAction

class HelpArgAction() : BaseArgAction("help") {
    override fun execute(params: Map<String, String>) {
        println("""Available actions:
  --action=help ... prints this help
  --action=clearPrefs ... reset preferences to factory default
  --action=repairDb ... invoke FlyWay's repair method (useful on migrating problems)""")
        // --action="clearPrefs;foo=bar"... reset preferences to factory default
    }
}

class ResetPrefsArgAction @Inject constructor(
        private val prefs: Prefs
) : BaseArgAction("clearPrefs") {

    override fun execute(params: Map<String, String>) {
        prefs.clear()
        println("Preferences cleared to factory defaults.")
    }
}

class RepairDatabaseArgAction @Inject constructor(
        private val db: DatabaseManager
) : BaseArgAction("repairDb") {

    override fun execute(params: Map<String, String>) {
        println("Repairing flyway database ...")
        db.repairDatabase()
    }
}
