package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException
import com.google.common.io.Files
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.util.HashMap


fun <K, V> Map<K, V>.verifyNoIntersection(that: Map<K, V>) {
//    if (this.keys.any { that.containsKey(it) }) throw GadsuException("Expected no intersections! This: $this. That: $that.")
    this.keys.forEach { if (that.containsKey(it)) throw GadsuException("Expected no intersections! This: $this. That: $that.") }
}

@Deprecated("Outsourced to kpotpourri.", ReplaceWith("this.nullIfEmpty()"))
fun String.nullIfEmpty() = if (isEmpty()) null else this


fun String.wrapIf(condition: Boolean, wrappingLeft: String, wrappingRight: String) = if (condition) wrappingLeft + this + wrappingRight else this

fun String.wrapParenthesisIf(condition: Boolean) = wrapIf(condition, "(", ")")

fun String.htmlize() = "<html>" + this.replace("\n", "<br/>") + "</html>"

fun String.saveToFile(target: File) {
    Files.write(this, target, Charsets.UTF_8)
}

//fun String.times(count: Int): String = StringBuilder().let { sb ->
//    repeat(count) {
//        sb.append(this)
//    }
//}.toString()
fun String.times(count: Int): String {
    val symbol = this
    return StringBuilder().apply {
        0.until(count).forEach { append(symbol) }
    }.toString()
}

fun String.removePreAndSuffix(search: String) = this.removePrefix(search).removeSuffix(search)

fun <IN, OUT> IN?.nullOrWith(wither: (IN) -> OUT): OUT? {
    if (this == null) {
        return null
    }
    return wither(this)
}


fun <K, V> Iterable<Pair<K, V>>.toMutableMap(): HashMap<K, V> {
    val immutableMap = toMap()
    val map = HashMap<K, V>(immutableMap.size)
    map.putAll(immutableMap)
    return map
}

fun Int.isBetweenInclusive(lower: Int, upper: Int) = this >= lower && this <= upper

fun Int.forEach(code: () -> Unit) {
    for (i in 1..this) {
        code()
    }
}


val LOG_Closeable = LoggerFactory.getLogger(Closeable::class.java)!!
fun Closeable.closeQuietly() {
    try {
        close()
    } catch (e: Exception) {
        LOG_Closeable.warn("Could not close '${this}'!", e)
    }
}
