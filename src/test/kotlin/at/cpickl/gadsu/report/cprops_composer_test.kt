package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.xprops.model.CPropsBuilder
import at.cpickl.gadsu.tcm.model.XProps
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test class CPropsComposerTest {

    fun `compose cprops for single entry`() {
        assertCProps({ it.add(XProps.Hungry, XProps.HungryOpts.BigHunger, XProps.HungryOpts.DietVegan) },
                "Essen: Hunger gro\u00df; Vegan")

    }

    fun `compose cprops for two opts`() {
        assertCProps({
            it.add(XProps.Hungry, XProps.HungryOpts.BigHunger)
                    .add(XProps.Sleep, XProps.SleepOpts.NeedLess)
        }, "Essen: Hunger gro\u00df\nSchlaf: wenig Schlaf")
    }

    private fun assertCProps(withBuilder: (CPropsBuilder) -> Unit, expectedText: String) {
        val builder = CPropsBuilder()
        withBuilder(builder)
        val cprops = builder.build()
        assertThat(CPropsComposer.compose(cprops), equalTo(expectedText))
    }

}
