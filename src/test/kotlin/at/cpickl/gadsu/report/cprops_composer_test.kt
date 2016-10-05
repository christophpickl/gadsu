package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CPropsBuilder
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test
class CPropsComposerTest {

    fun `compose cprops`() {
        assertCProps({ it
            .add(XProps.Hungry, XProps.HungryOpts.BigHunger, XProps.HungryOpts.StoolSoft)
        },
            "Essen: Hunger gro\u00df; Stuhl ungeformt, weich")
        assertCProps({ it
            .add(XProps.Hungry, XProps.HungryOpts.BigHunger)
            .add(XProps.Sleep, XProps.SleepOpts.NeedLess)
        },
            "Essen: Hunger gro\u00df\n" +
            "Schlaf: wenig Schlaf")
    }

    private fun assertCProps(withBuilder: (CPropsBuilder) -> Unit, expectedText: String) {
        val builder = CPropsBuilder()
        withBuilder(builder)
        val cprops = builder.build()
        val client = Client.unsavedValidInstance().copy(cprops = cprops)
        assertThat(CPropsComposer.compose(client), equalTo(expectedText))
    }

}
