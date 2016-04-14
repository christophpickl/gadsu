package at.cpickl.gadsu

import joptsimple.OptionParser

fun parseArgs(args: Array<String>): Args {
    return JOptArgsParser().parse(args)
}

interface ArgsPaser {
    fun parse(args: Array<String>): Args
}

private class JOptArgsParser : ArgsPaser {

    override fun parse(args: Array<String>): Args {
        // http://pholser.github.io/jopt-simple/examples.html
        val options = OptionParser("databaseUrl:").parse(*args)

        var databaseUrl: String? = null
        if (options.has("databaseUrl")) {
            databaseUrl = options.valueOf("databaseUrl") as String
        }

        return Args(databaseUrl)

    }

}

class Args(val databaseUrl: String?)
