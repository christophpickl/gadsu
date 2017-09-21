package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.duplicates
import org.testng.Assert.fail
import org.testng.annotations.Test

@Test
class OrganSyndromeTest {

    fun `no duplicate symptoms`() {
        val foundDuplicates = mutableListOf<Duplicate>()
        OrganSyndrome.values().forEach { syndrome ->
            val duplicateSymptoms = syndrome.symptoms.duplicates()
            if (duplicateSymptoms.isNotEmpty()) {
                foundDuplicates += Duplicate(syndrome, duplicateSymptoms)
            }
        }

        if (foundDuplicates.isNotEmpty()) {
            val report = "Found duplicate symptoms in syndromes!" +
                    foundDuplicates.map { (syndrome, duplicateSymptoms) ->
                        syndrome.label + "\n========================\n" +
                                duplicateSymptoms.joinToString("\n") { "- " + it::class.simpleName }
                    }.joinToString("\n\n")
            fail(report)
        }
    }

}

private data class Duplicate(
        val syndrome: OrganSyndrome,
        val duplicateSymptoms: List<Symptom>
)

