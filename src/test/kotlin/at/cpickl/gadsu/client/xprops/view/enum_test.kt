package at.cpickl.gadsu.client.xprops.view

import at.cpickl.gadsu.client.xprops.model.CProp
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.tcm.model.XProps
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test class CPropExtensionTest {

    fun `formatData enum opts and note`() {
        val cprop = CPropEnum(XProps.Sleep, listOf(XProps.SleepOpts.NeedMuch.opt), "note")
        assertThat(cprop.formatData(), equalTo("* viel Schlaf\n\n[NOTIZ]\nnote"))
    }

    fun `formatData enum opts only`() {
        val cprop = CPropEnum(XProps.Sleep, listOf(XProps.SleepOpts.NeedMuch.opt), "")
        assertThat(cprop.formatData(), equalTo("* viel Schlaf"))
    }

    fun `formatData note only`() {
        val cprop = CPropEnum(XProps.Sleep, emptyList(), "note")
        assertThat(cprop.formatData(), equalTo("[NOTIZ]\nnote"))
    }

    fun `formatData null returns empty`() {
        val cprop: CProp? = null
        assertThat(cprop.formatData(), equalTo(""))
    }

     fun `formatClientValues`() {
         val cprop = CPropEnum(XProps.Sleep, listOf(XProps.SleepOpts.NeedMuch.opt), "IGNORE")
         assertThat(cprop.formatClientValues(), equalTo("* viel Schlaf"))

     }

}
