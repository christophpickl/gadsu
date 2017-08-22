package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosis
import com.github.christophpickl.kpotpourri.common.logging.LOG

fun Collection<Symptom>.labelsJoined() = map { it.source.label }.joinToString()

data class PossibleSyndrom(
        val syndrome: OrganSyndrome,
        /** from >0.0 to <=1.0 */
        val matchRation: Double,
        val matchedSymptoms: Set<Symptom>
) : Comparable<PossibleSyndrom> {

    val matchPercentage = (matchRation * 100).toInt()
    val notMatchedSymptoms = syndrome.symptoms.minus(matchedSymptoms)

    override fun compareTo(other: PossibleSyndrom) = matchRation.compareTo(other.matchRation)
}

data class SyndromeReport(
        val possibleSyndromes: List<PossibleSyndrom>
) {

    companion object {
        val dummy = SyndromeReport(
                possibleSyndromes = listOf(
                        PossibleSyndrom(OrganSyndrome.LuQiMangel, 0.75, setOf(Symptom.FlacheAtmung, Symptom.LeichtesSchwitzen))
                )
        )
        val empty = SyndromeReport(emptyList())
    }

    val asHtml by lazy {
        if (possibleSyndromes.isEmpty()) "<i>Keine gefunden.</i>"
        else
            "<ul>" +
                    possibleSyndromes.sortedDescending().map {
                        """
            |<li>
            |  <span style="font-weight:bold;font-size:16">${it.syndrome.label}</span><br/>
            |  <b>Trefferquote:</b> ${it.matchPercentage}%<br/>
            |  <span color="green">${it.matchedSymptoms.labelsJoined()}</span>
            |    ${if (it.matchedSymptoms.isNotEmpty() && it.notMatchedSymptoms.isNotEmpty()) "," else ""}
            |    <span color="red">${it.notMatchedSymptoms.labelsJoined()}</span>
            |  <br/><br/>
            |</li>
            |""".trimMargin()
                    }.joinToString(separator = "\n") +
                    "</ul>"
    }
}

class SyndromeGuesser {

    private val log = LOG {}

    fun guess(client: Client, treatments: List<Treatment>): SyndromeReport {
        log.debug { "guess(client=..)" }
        val foundSyndromes = mutableListOf<PossibleSyndrom>()

        OrganSyndrome.values().forEach { syndrome ->
            val clientSymptoms = extractSymptoms(client, treatments)
            val matchingSymptoms = syndrome.symptoms.intersect(clientSymptoms)
            val match = calculateMatch(syndrome, matchingSymptoms)
            if (match != 0.0) {
                foundSyndromes += PossibleSyndrom(syndrome, match, matchingSymptoms)
            }
        }
        return SyndromeReport(foundSyndromes)
    }

    private fun extractSymptoms(client: Client, treatments: List<Treatment>): Collection<Symptom> {
        return extractSymptomsFromXProps(client.cprops).union(extractSymptomsFromTreatments(treatments))
    }

    private fun extractSymptomsFromTreatments(treatments: List<Treatment>): Collection<Symptom> {
        val allDiagnostedPulseProps = treatments.map { it.dynTreatments.filter { it is PulseDiagnosis } }.flatten().map {
            val pulseDiagnosis = it as PulseDiagnosis
            pulseDiagnosis.properties
        }.flatten().distinct()
        return allDiagnostedPulseProps.map {
             Symptom.byPulseProperty[it]
        }.filterNotNull()

        // FIXME implement tongue props scan
    }

    private fun extractSymptomsFromXProps(cprops: CProps): Collection<Symptom> {
        return cprops.map { it.clientValue }.flatten().map {
            Symptom.byXpropEnumOpt[it]
        }.filterNotNull().toSet()
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

