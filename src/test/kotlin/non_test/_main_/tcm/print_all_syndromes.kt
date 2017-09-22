package non_test._main_.tcm

import at.cpickl.gadsu.tcm.patho.OrganSyndrome

fun main(args: Array<String>) {
    OrganSyndrome.values().forEach { syndrome ->
        println(syndrome.label)
        println("=====================")
        syndrome.symptoms.sortedBy { it::class.simpleName }.forEach { symptom ->
            println("- " + symptom::class.simpleName)
        }
        println()
    }
}
