package at.cpickl.gadsu

import at.cpickl.gadsu.persistence.PersistenceModule
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException

//fun main(args: Array<String>) {
//    parseArgs(arrayOf("--help")).help!!()
//}

/**
 * @throws ArgsException if CLI args are somehow wrong.
 */
fun parseArgs(cliArgs: Array<String>): Args {
    return CommonsCliArgsParser().parse(cliArgs)
}

fun parseArgsOrHelp(cliArgs: Array<String>, suppressExceptionStacktrace: Boolean = false): Args? {
    val args: Args
    try {
        args = parseArgs(cliArgs)
    } catch (e: ArgsException) {
        e.help(if (suppressExceptionStacktrace) null else e)
        return null
    }

    if (args.help != null) {
        args.help.invoke(null)
        return null
    }
    return args
}

interface ArgsParser {
    fun parse(cliArgs: Array<String>): Args
}

/**
 * @param databaseUrl e.g.: "jdbc:hsqldb:mem:mymemdb" or (default): "jdbc:hsqldb:file:$DB_DIR/database"
 */
data class Args(val help: ((e: ArgsException?) -> Unit)?,
                val databaseUrl: String?,
                val debug: Boolean,
                val action: String?) {
    companion object {
        val EMPTY = Args(null, null, false, null)
    }
}

class ArgsException(message: String, cause: Exception, val help: (e: ArgsException?) -> Unit) : GadsuException(message, cause)


/**
 * See: http://commons.apache.org/proper/commons-cli/introduction.html
 */
private class CommonsCliArgsParser : ArgsParser {
    companion object {
        private val DATABASE_URL_SHORT = "d"
        private val DATABASE_URL_LONG = "databaseUrl"
        private val DEBUG_SHORT = "x"
        private val DEBUG_LONG = "debug"
        private val ACTION_SHORT = "a"
        private val ACTION_LONG = "action"
        private val HELP_SHORT = "?"
        private val HELP_LONG = "help"
    }

    override fun parse(cliArgs: Array<String>): Args {

        val options = Options()
        options.addOption(DATABASE_URL_SHORT, DATABASE_URL_LONG, true, "Override JDBC URL to e.g.: 'jdbc:hsqldb:mem:mymemdb' (default is: '${PersistenceModule.DEFAULT_DB_URL}').")
        options.addOption(DEBUG_SHORT, DEBUG_LONG, false, "Increase log level and register additional console appender.")
        options.addOption(ACTION_SHORT, ACTION_LONG, true, "Add a custom action and quit (for debugging purpose).")
        options.addOption(HELP_SHORT, HELP_LONG, false, "Print this usage help.")

        val parser = DefaultParser()
        val commands: CommandLine
        val help = HelpFormatter()
        help.width = 150
        val helpFunction = { e: ArgsException? ->
            e?.printStackTrace()
            help.printHelp("gadsu", options)
        }

        try {
            commands = parser.parse(options, cliArgs)
        } catch (e: ParseException) {
            throw ArgsException("Parsing CLI arguments failed: ${e.message}! ($cliArgs)", e, helpFunction)
        }

        if (commands.hasOption(HELP_SHORT)) {
            return Args.EMPTY.copy(help = helpFunction)
        }

        return Args(
                null,
                if (commands.hasOption(DATABASE_URL_SHORT)) commands.getOptionValue(DATABASE_URL_SHORT) else null,
                commands.hasOption(DEBUG_SHORT),
                if (commands.hasOption(ACTION_SHORT)) commands.getOptionValue(ACTION_SHORT) else null
        )
    }

}
