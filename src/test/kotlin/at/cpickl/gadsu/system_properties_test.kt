package at.cpickl.gadsu

import at.cpickl.gadsu.testinfra.Expects.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test class SystemPropertiesSPTest {

    private val stringProp = StringSystemProperty("SystemPropertiesSPTest_keyString")
    private val booleanProp = BooleanSystemProperty("SystemPropertiesSPTest_keyBoolean")

    @BeforeMethod
    fun resetKey() {
        System.clearProperty(stringProp.key)
        System.clearProperty(booleanProp.key)
    }

    fun `stringProp, not initialized returns null or default`() {
        assertThat(stringProp.getOrNull(), nullValue())
        assertThat(stringProp.getOrDefault("def"), equalTo("def"))
    }

    fun `stringProp, sunshine`() {
        setProperty(stringProp, "foo")
        assertThat(stringProp.getOrDefault("def"), equalTo("foo"))
        assertThat(stringProp.getOrNull(), equalTo("foo"))
    }


    fun `booleanProp, not initialized, set to null`() {
        assertThat(booleanProp.isEnabledOrNull(), nullValue())
    }

    fun `booleanProp, sunshine`() {
        setProperty(booleanProp, "true"); assertThat(booleanProp.isEnabledOrNull(), equalTo(true))
        setProperty(booleanProp, "TrUe"); assertThat(booleanProp.isEnabledOrNull(), equalTo(true))
        setProperty(booleanProp, "1"); assertThat(booleanProp.isEnabledOrNull(), equalTo(true))

        setProperty(booleanProp, "false"); assertThat(booleanProp.isEnabledOrNull(), equalTo(false))
        setProperty(booleanProp, "FaLsE"); assertThat(booleanProp.isEnabledOrNull(), equalTo(false))
        setProperty(booleanProp, "0"); assertThat(booleanProp.isEnabledOrNull(), equalTo(false))
    }

    fun `booleanProp, invalid value, fails`() {
        setProperty(booleanProp, "fuchur")
        expect(GadsuException::class, {booleanProp.isEnabledOrNull()}, "fuchur")
    }

    fun `stringProp set`() {
        stringProp.set("foo")
        assertProperty(stringProp, "foo")
    }

    fun `booleanProp set true`() {
        booleanProp.enable()
        assertProperty(booleanProp, "true")
    }

    fun `booleanProp set false`() {
        booleanProp.disable()
        assertProperty(booleanProp, "false")
    }

    fun `clear`() {
        setProperty(stringProp, "abc")
        stringProp.clear()
        assertProperty(stringProp, null)
    }

    private fun setProperty(prop: AbstractSystemProperty, value: String) {
        System.setProperty(prop.key, value)
    }

    private fun assertProperty(prop: AbstractSystemProperty, expected: String?) {
        assertThat(System.getProperty(prop.key), equalTo(expected))
    }

}
