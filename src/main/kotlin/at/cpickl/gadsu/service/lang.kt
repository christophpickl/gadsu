package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException
import com.google.common.io.Files
import java.io.File
import java.util.HashMap


fun <K, V> Map<K, V>.verifyNoIntersection(that: Map<K, V>) {
    this.keys.forEach { if (that.containsKey(it)) throw GadsuException("Expected no intersections! This: $this. That: $that.") }
}

fun String.nullIfEmpty() = if (this.isEmpty()) null else this


fun String.wrapIf(condition: Boolean, wrappingLeft: String, wrappingRight: String) = if (condition) wrappingLeft + this + wrappingRight else this

fun String.wrapParenthesisIf(condition: Boolean) = wrapIf(condition, "(", ")")

fun String.htmlize() = "<html>" + this.replace("\n", "<br/>") + "</html>"

fun String.saveToFile(target: File) {
    Files.write(this, target, Charsets.UTF_8)
}

fun String.times(count: Int): String {
    val symbol = this
    return StringBuilder().apply {
        0.until(count).forEach { append(symbol) }
    }.toString()
}

fun <T> T?.nullOrWith(wither: (T) -> T): T? {
    if (this == null) {
        return null
    }
    return wither(this)
}


fun <IN, OUT> IN?.nullOrWith2(wither: (IN) -> OUT): OUT? {
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
