package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException
import com.google.common.io.Files
import java.io.File
import java.util.*


fun <K, V> Map<K, V>.verifyNoIntersection(that: Map<K, V>) {
    this.keys.forEach { if (that.containsKey(it)) throw GadsuException("Expected no intersections! This: $this. That: $that.") }
}

fun String.nullIfEmpty() = if (this.isEmpty()) null else this

fun String.wrapParenthesisIf(condition: Boolean) = if (condition) "($this)" else this

fun String.saveToFile(target: File) {
    Files.write(this, target, Charsets.UTF_8)
}


fun <K, V> Iterable<Pair<K, V>>.toMutableMap(): HashMap<K, V> {
    val immutableMap = toMap()
    val map = HashMap<K, V>(immutableMap.size)
    map.putAll(immutableMap)
    return map
}

fun Int.isBetweenInclusive(lower: Int, upper: Int) = this >= lower && this <= upper
