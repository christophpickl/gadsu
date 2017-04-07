package at.cpickl.gadsu.service

import com.google.common.io.Files
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.util.HashMap

// TODO delete those and replace with KPot

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("use different import statement"))
fun String.wrapIf(condition: Boolean, wrappingLeft: String, wrappingRight: String) = if (condition) wrappingLeft + this + wrappingRight else this

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("use different import statement"))
fun String.wrapParenthesisIf(condition: Boolean) = wrapIf(condition, "(", ")")

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("use different import statement"))
fun String.htmlize() = "<html>" + this.replace("\n", "<br/>") + "</html>"

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("use different import statement"))
fun String.saveToFile(target: File) {
    Files.write(this, target, Charsets.UTF_8)
}

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("use different import statement"))
fun String.times(count: Int): String {
    val symbol = this
    return StringBuilder().apply {
        0.until(count).forEach { append(symbol) }
    }.toString()
}

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("use different import statement"))
fun String.removePreAndSuffix(search: String) = this.removePrefix(search).removeSuffix(search)

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("use different import statement"))
fun <K, V> Iterable<Pair<K, V>>.toMutableMap(): HashMap<K, V> {
    val immutableMap = toMap()
    val map = HashMap<K, V>(immutableMap.size)
    map.putAll(immutableMap)
    return map
}

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("use different import statement"))
fun Int.isBetweenInclusive(lower: Int, upper: Int) = this >= lower && this <= upper

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("use different import statement"))
fun Int.forEach(code: () -> Unit) {
    for (i in 1..this) {
        code()
    }
}

val LOG_Closeable = LoggerFactory.getLogger(Closeable::class.java)!!
@Deprecated("Outsourced to kpotpourri.", ReplaceWith("closeSilently()"))
fun Closeable.closeQuietly() {
    try {
        close()
    } catch (e: Exception) {
        LOG_Closeable.warn("Could not close '${this}'!", e)
    }
}
