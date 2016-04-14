package at.cpickl.gadsu

import org.apache.commons.cli.*

fun main(args: Array<String>) {
    parseArgs(arrayOf("--help")).help!!()
}

/**
 * @throws ArgsException if CLI args are somehow wrong.
 */
fun parseArgs(cliArgs: Array<String>): Args {
    return CommonsCliArgsParser().parse(cliArgs)
}

interface ArgsParser {
    fun parse(cliArgs: Array<String>): Args
}

data class Args(val help: (() -> Unit)?, val databaseUrl: String?)

class ArgsException(message: String, cause: Exception, val help: () -> Unit) : GadsuException(message, cause)


/**
 * See: http://commons.apache.org/proper/commons-cli/introduction.html
 */
private class CommonsCliArgsParser : ArgsParser {
    companion object {
        private val DATABASE_URL_SHORT = "d"
        private val DATABASE_URL_LONG = "databaseUrl"
        private val HELP_SHORT = "?"
        private val HELP_LONG = "help"
    }
    override fun parse(cliArgs: Array<String>): Args {

        val options = Options()
        options.addOption(DATABASE_URL_SHORT, DATABASE_URL_LONG, true, "Override JDBC URL to e.g.: 'jdbc:hsqldb:mem:mymemdb' (default is: 'jdbc:hsqldb:file:/some/path').")
        options.addOption(HELP_SHORT, HELP_LONG, false, "Print this usage help.")

        val parser = DefaultParser()
        val commands: CommandLine
        val help = HelpFormatter()
        help.width = 150
        val helpFunction = { help.printHelp("gadsu", options) }

        try {
            commands = parser.parse(options, cliArgs)
        } catch (e: ParseException) {
            throw ArgsException("Parsing CLI arguments failed: ${e.message}! ($cliArgs)", e, helpFunction)
        }

        if (commands.hasOption(HELP_SHORT)) {
            return Args(helpFunction, null)
        }

        return Args(
                null,
                if(commands.hasOption(DATABASE_URL_SHORT)) commands.getOptionValue(DATABASE_URL_SHORT) else null
        )
    }

}
