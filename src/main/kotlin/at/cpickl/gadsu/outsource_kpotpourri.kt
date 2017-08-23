package at.cpickl.gadsu

import java.io.File

// MOVE THIS STUFF TO KPOTPOURRI

val File.humanReadableSize: String get() {
    val bytes = length()
    val kilo = bytes / 1024
    if (kilo < 1024) {
        return "$kilo KB"
    }
    val mega = kilo / 1024
    if (mega < 1024) {
        return "$mega MB"
    }
    val giga = mega / 1024
    return "$giga GB"
}
