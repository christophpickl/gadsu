package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps

fun List<Symptom>.labelsJoined() = map { it.xprop.opt.label }.joinToString()

data class PossibleSyndrom(
        val syndrome: OrganSyndrome,
        /** from >0.0 to <=1.0 */
        val matchRation: Double,
        val matchedSymptoms: List<Symptom>
) : Comparable<PossibleSyndrom> {

    val matchPercentage = (matchRation * 100).toInt()
    val notMatchedSymptoms = syndrome.symptoms.minus(matchedSymptoms)

    override fun compareTo(other: PossibleSyndrom) = matchRation.compareTo(other.matchRation)
}

data class SyndromeReport(
        val possibleSyndromes: List<PossibleSyndrom>
) {
    val asHtml by lazy {
        """
        |<ul>
        |${possibleSyndromes.sortedDescending().map {
            """
            |<li>
            |    <b>${it.syndrome.label}</b> (${it.matchPercentage}%):
            |    <span color="green">${it.matchedSymptoms.labelsJoined()}</span>
            |    ${if (it.matchedSymptoms.isNotEmpty() && it.notMatchedSymptoms.isNotEmpty()) "," else ""}
            |    <span color="grey">${it.notMatchedSymptoms.labelsJoined()}</span>
            |</li>""".trimMargin()
        }}
        |</ul>
        |""".trimMargin()
    }
}

class SyndromeGuesser {

    fun detect(client: Client): SyndromeReport {
        val foundSyndromes = mutableListOf<PossibleSyndrom>()

        OrganSyndrome.values().forEach { syndrome ->
            val clientSymptoms = extractSymptoms(client.cprops)
            val matchingSymptoms = syndrome.symptoms.intersect(clientSymptoms).toList()
            val match = calculateMatch(syndrome, matchingSymptoms)
            if (match != 0.0) {
                foundSyndromes += PossibleSyndrom(syndrome, match, matchingSymptoms)
            }
        }
        return SyndromeReport(foundSyndromes)
    }

    private fun extractSymptoms(cprops: CProps): List<Symptom> {
        return cprops.map { it.clientValue }.flatten().map {
            Symptom.byXpropEnumOpt[it]
        }.filterNotNull()
    }

    private fun calculateMatch(syndrome: OrganSyndrome, matchingSymptoms: Collection<Symptom>): Double {
        if (matchingSymptoms.isEmpty()) {
            return 0.0
        }
        val maxSize = syndrome.symptoms.size
        val matchSize = matchingSymptoms.size
        return matchSize.toDouble() / maxSize.toDouble()
    }
}

