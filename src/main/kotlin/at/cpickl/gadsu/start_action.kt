package at.cpickl.gadsu

import at.cpickl.gadsu.preferences.Prefs
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import javax.inject.Inject


class ArgsActionExecutor @Inject constructor(
        private val bus: EventBus,
        resetPrefsAction: ResetPrefsArgAction,
        helpAction: HelpArgAction
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val actions = listOf<ArgAction>(resetPrefsAction, helpAction)

    fun execute(actionUrl: String) {
        log.debug("execute(actionUrl={})", actionUrl)

        val actionName = if (!actionUrl.contains(";")) actionUrl else actionUrl.substring(0, actionUrl.indexOf(";"))
        val action = actions.firstOrNull { it.actionName.equals(actionName.toLowerCase()) } ?: throw GadsuException("Invalid action requested '${actionName}'!")
        // parse args: "action;foo=bar", watch out for "action"
        action.execute()
        bus.post(QuitUserEvent())
    }
}

abstract class BaseArgAction(override val actionName: String) : ArgAction

class HelpArgAction() : BaseArgAction("help") {
    override fun execute() {
        println("""Available actions:
  --action=help ... prints this help
  --action="resetPrefs;foo=bar"... reset preferences to factory default""")
    }
}

class ResetPrefsArgAction @Inject constructor(
        private val prefs: Prefs
) : BaseArgAction("resetPrefs") {

    override fun execute() {
        prefs.reset()
    }
}

interface ArgAction {
    val actionName: String

    fun execute()
}
