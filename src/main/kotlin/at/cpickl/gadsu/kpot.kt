package at.cpickl.gadsu

// OUTSOURCE to kpotpourri

fun firstNotEmpty(vararg strings: String) = strings.toList().firstOrNull { it.isNotEmpty() } ?: ""
