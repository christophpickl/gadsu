package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException
import com.google.common.io.Files
import java.io.File


fun <K, V> Map<K, V>.verifyNoIntersection(that: Map<K, V>) {
    this.keys.forEach { if (that.containsKey(it)) throw GadsuException("Expected no intersections! This: $this. That: $that.") }
}

fun String.nullIfEmpty() = if (this.isEmpty()) null else this


fun String.saveToFile(target: File) {
    Files.write(this, target, Charsets.UTF_8)
}

fun File.readContent(): String {
    return Files.toString(this, Charsets.UTF_8)
}
