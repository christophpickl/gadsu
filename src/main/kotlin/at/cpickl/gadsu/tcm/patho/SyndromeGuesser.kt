package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps

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
)

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

