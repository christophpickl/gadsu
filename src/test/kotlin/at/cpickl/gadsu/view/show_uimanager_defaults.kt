package at.cpickl.gadsu.view

import java.util.*
import javax.swing.UIManager

fun main(args: Array<String>) {
//    UIManager.getInstalledLookAndFeels()
//            .forEach {
//        UIManager.setLookAndFeel(it.className)

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    printDefaults()
}

private fun printDefaults() {
    println("Defaults for: ${UIManager.getLookAndFeel().name}")
    val defaults = UIManager.getDefaults()
    val defaultsOrdered = defaults.toSortedMap(Comparator<kotlin.Any> { o1, o2 -> (o1.toString()).compareTo(o2.toString()) })
    defaultsOrdered.forEach() {
        println("    ${it.key} = ${it.value}")
    }
    println()
    println()
    println()
}