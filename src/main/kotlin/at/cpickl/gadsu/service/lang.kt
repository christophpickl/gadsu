package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException


fun <K, V> Map<K, V>.verifyNoIntersection(that: Map<K, V>) {
    this.keys.forEach { if (that.containsKey(it)) throw GadsuException("Expected no intersections! This: $this. That: $that.") }
}

fun String.nullIfEmpty() = if (this.isEmpty()) null else this
