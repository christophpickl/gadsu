package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.xprops.model.CPropsBuilder
import at.cpickl.gadsu.tcm.model.XProps
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test class CPropsComposerTest {

    fun `compose cprops for single entry`() {
        assertCProps({ it.add(XProps.Hungry, XProps.HungryOpts.BigHunger, XProps.HungryOpts.DietVegan) },
                "${XProps.Hungry.label}: ${XProps.HungryOpts.BigHunger.opt.label}; ${XProps.HungryOpts.DietVegan.opt.label}")

    }

    fun `compose cprops for two opts`() {
        assertCProps({
            it.add(XProps.Hungry, XProps.HungryOpts.BigHunger)
                    .add(XProps.Sleep, XProps.SleepOpts.NeedLess)
        }, "${XProps.Hungry.label}: ${XProps.HungryOpts.BigHunger.opt.label}\n" +
                "${XProps.Sleep.label}: ${XProps.SleepOpts.NeedLess.opt.label}")
    }

    private fun assertCProps(withBuilder: (CPropsBuilder) -> Unit, expectedText: String) {
        val builder = CPropsBuilder()
        withBuilder(builder)
        val cprops = builder.build()
        assertThat(CPropsComposer.compose(cprops), equalTo(expectedText))
    }

}
