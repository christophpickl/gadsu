package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.CPropsBuilder
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.tcm.model.IsEnumOption
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.PulseProperty
import com.github.christophpickl.kpotpourri.common.enforceAllBranchesCovered
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.greaterThan
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.isEmpty
import org.testng.annotations.Test

@Test
class SyndromeGuesserTest {

    private val guesser = SyndromeGuesser()
    private val anyClient = Client.INSERT_PROTOTYPE
    private val clientWithEmptyCProps = anyClient.copy(cprops = CProps.empty)
    private val noTreatments = emptyList<Treatment>()

    fun `When client has no cprop Then the report should be empty`() {
        val client = anyClient.copy(cprops = CProps.empty)

        val report = guesser.guess(client, noTreatments)

        assertThat(report.possibleSyndromes, isEmpty)
    }

    fun `When client has cprop SweatEasily Then the report contains LuQiMangel syndrome`() {
        val client = buildClient {
            add(XProps.Temperature, XProps.TemperatureOpts.SweatEasily)
        }

        val report = guesser.guess(client, noTreatments)

        report.assertHas(OrganSyndrome.LuQiMangel)
    }

    fun `When client has soft pulse property in a treatment, Then the report contains LuQiMangel syndrome`() {
        val treatment = Treatment.savedValidInstance("").copy(
                dynTreatments = listOf(PulseDiagnosis(listOf(PulseProperty.Soft), "")))

        val report = guesser.guess(clientWithEmptyCProps, listOf(treatment))

        report.assertHas(OrganSyndrome.LuQiMangel)
    }


    fun `When client has all LuQiMangel symptoms Then the ratio is high - TODO test for 100`() {
        val client = buildClient {

            val xpropsForSyndrom = mutableListOf<Pair<XPropEnum, IsEnumOption>>()
            val pulsePropertiesForSyndrom = mutableListOf<PulseProperty>()

            OrganSyndrome.LuQiMangel.symptoms.forEach {
                when (it.source) {
                    is Symptom.SymptomSource.XPropSource -> {
                        val xsource = (it.source as Symptom.SymptomSource.XPropSource)
                        xpropsForSyndrom += xsource.xenum to xsource.option
                    }
                    is Symptom.SymptomSource.PulseSource -> {
                        val psource = (it.source as Symptom.SymptomSource.PulseSource)
                        pulsePropertiesForSyndrom += psource.property
                    }
                    Symptom.SymptomSource.NOT_IMPLEMENTED -> Unit
                }
            }.enforceAllBranchesCovered

            val groupedXprops = mutableMapOf<XPropEnum, MutableList<IsEnumOption>>()
            xpropsForSyndrom.map { it.first }.distinct().forEach {
                groupedXprops += it to mutableListOf()
            }
            xpropsForSyndrom.forEach {
                groupedXprops[it.first]!! += it.second
            }
            groupedXprops.forEach {
                add(it.key, *it.value.toTypedArray())
            }
        }
        // MINOR TEST pulsePropertiesForSyndrom go through treatments

        val report = guesser.guess(client, noTreatments)

        report.assertHas(OrganSyndrome.LuQiMangel)
        report.possibleSyndromes.first { it.syndrome == OrganSyndrome.LuQiMangel }.apply {
            assertThat(matchPercentage, greaterThan(0))
            assertThat(matchRation, greaterThan(0.0))
//            assertThat(matchPercentage, equalTo(100))
//            assertThat(matchRation, equalTo(1.0))
        }
    }

    private fun buildClient(withCProps: CPropsBuilder.() -> Unit): Client {
        val builder = CProps.builder()
        builder.withCProps()
        return Client.savedValidInstance().copy(
                cprops = builder
                        .build()
        )
    }

    private fun SyndromeReport.assertHas(needle: OrganSyndrome) {
        assertThat(possibleSyndromes.map { it.syndrome },
                hasElement(needle))
    }
}
