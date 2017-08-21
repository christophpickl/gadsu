package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.CPropsBuilder
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.testinfra.savedValidInstance
import com.github.christophpickl.kpotpourri.test4k.hamkrest_matcher.containsExactlyInAnyOrder
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isEmpty
import org.testng.annotations.Test

@Test
class SyndromeGuesserTest {

    fun `When client has no cprop Then the report should be empty`() {
        val client = Client.savedValidInstance().copy(cprops = CProps.empty)

        val report = SyndromeGuesser().detect(client)

        assertThat(report.possibleSyndromes, isEmpty)
    }

    fun `When client has cprop BigHunger Then the report contains LuQiMangel syndrome`() {
        val report = guessForClient {
            add(XProps.Hungry, XProps.HungryOpts.BigHunger)
        }

        assertThat(report.possibleSyndromes.map { it.syndrome },
                containsExactlyInAnyOrder(OrganSyndrome.LuQiMangel))
    }

    fun `When client has all LuQiMangel symptons Then the ratio is 100`() {
        val report = guessForClient {
            add(XProps.Hungry, XProps.HungryOpts.BigHunger)
            add(XProps.Sleep, XProps.SleepOpts.Dreams, XProps.SleepOpts.ProblemsFallAsleep)
        }

        report.possibleSyndromes.first { it.syndrome == OrganSyndrome.LuQiMangel }.apply {
            assertThat(matchPercentage, equalTo(100))
            assertThat(matchRation, equalTo(1.0))
        }
    }

    private fun guessForClient(withBuilder: CPropsBuilder.() -> Unit): SyndromeReport {
        val builder = CProps.builder()
        builder.withBuilder()
        val client = Client.savedValidInstance().copy(
                cprops = builder
                        .build()
        )
        return SyndromeGuesser().detect(client)

    }

}
