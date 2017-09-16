package non_test._main_

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.tcm.patho.SyndromeGuesser
import at.cpickl.gadsu.tcm.patho.ZangOrgan
import at.cpickl.gadsu.testinfra.savedValidInstance
import com.github.christophpickl.kpotpourri.common.collection.prettyPrint


fun main(args: Array<String>) {
    printAll()
//    printGuess()
}

private fun printAll() {
    ZangOrgan.values().forEach { zang ->
        println(zang.meridian.labelLong)
        println("==========================================")
        zang.syndromes.forEach { syndrome ->
            println(syndrome.label)
            println("------------------------------------------")
            syndrome.symptoms.groupBy { it.category }.forEach { (category, symptoms) ->
                println("$category:")
                symptoms.sortedBy { it.label }.forEach { symptom ->
                    println("- ${symptom.label}${if (symptom.leadSymptomFor == zang) " (Leitsymptom)" else ""}")
                }
            }
            println()
        }
        println()
    }
}

private fun printGuess() {
    val client = Client.savedValidInstance().copy(
            cprops = CProps.builder()
                    .add(XProps.Hungry, XProps.HungryOpts.LittleHunger, XProps.HungryOpts.BigHunger)
                    .add(XProps.Temperature, XProps.TemperatureOpts.SweatEasily)
                    .build()
    )
    println()
    println("Client props: ${client.cprops.map { it.clientValue.map { it.label }.joinToString() }.joinToString()}")
    println()

    println("Guessed syndroms:")
    val report = SyndromeGuesser().guess(client, emptyList())
    report.possibleSyndromes.sortedDescending().map {
        String.format("[%3s%%] %s: %s (NOT: %s)", it.matchPercentage, it.syndrome.label,
                it.matchedSymptoms.map { it.source.label }.joinToString(),
                it.notMatchedSymptoms.map { it.source.label }.joinToString()
        )
    }.prettyPrint()
}
